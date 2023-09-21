/*
 *  Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.samsung.magicinfo;

import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.avispl.symphony.api.dal.control.Controller;
import com.avispl.symphony.api.dal.dto.control.AdvancedControllableProperty;
import com.avispl.symphony.api.dal.dto.control.ControllableProperty;
import com.avispl.symphony.api.dal.dto.monitor.ExtendedStatistics;
import com.avispl.symphony.api.dal.dto.monitor.Statistics;
import com.avispl.symphony.api.dal.dto.monitor.aggregator.AggregatedDevice;
import com.avispl.symphony.api.dal.error.ResourceNotReachableException;
import com.avispl.symphony.api.dal.monitor.Monitorable;
import com.avispl.symphony.api.dal.monitor.aggregator.Aggregator;
import com.avispl.symphony.dal.aggregator.parser.AggregatedDeviceProcessor;
import com.avispl.symphony.dal.aggregator.parser.PropertiesMapping;
import com.avispl.symphony.dal.aggregator.parser.PropertiesMappingParser;
import com.avispl.symphony.dal.communicator.RestCommunicator;
import com.avispl.symphony.dal.infrastructure.management.samsung.magicinfo.common.GeneralInfo;
import com.avispl.symphony.dal.infrastructure.management.samsung.magicinfo.common.MagicInfoCommand;
import com.avispl.symphony.dal.infrastructure.management.samsung.magicinfo.common.MagicInfoConstant;
import com.avispl.symphony.dal.infrastructure.management.samsung.magicinfo.common.SystemInfo;
import com.avispl.symphony.dal.infrastructure.management.samsung.magicinfo.dto.DeviceType;
import com.avispl.symphony.dal.util.StringUtils;

/**
 * MagicInfoCommunicator
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 9/8/2023
 * @since 1.0.0
 */
public class MagicInfoCommunicator extends RestCommunicator implements Aggregator, Monitorable, Controller {
	/**
	 * Process that is running constantly and triggers collecting data from Samsung MagicInfo API endpoints, based on the given timeouts and thresholds.
	 *
	 * @author Harry
	 * @since 1.0.0
	 */
	class MagicInfoDataLoader implements Runnable {
		private volatile boolean inProgress;
		private volatile boolean flag = false;

		public MagicInfoDataLoader() {
			inProgress = true;
		}

		@Override
		public void run() {
			loop:
			while (inProgress) {
				try {
					TimeUnit.MILLISECONDS.sleep(500);
				} catch (InterruptedException e) {
					// Ignore for now
				}

				if (!inProgress) {
					break loop;
				}

				// next line will determine whether Poly Lens monitoring was paused
				updateAggregatorStatus();
				if (devicePaused) {
					continue loop;
				}
				if (logger.isDebugEnabled()) {
					logger.debug("Fetching other than Chrome OS device list");
				}
				long currentTimestamp = System.currentTimeMillis();
				if (!flag && nextDevicesCollectionIterationTimestamp <= currentTimestamp) {
					populateDeviceDetails();
					flag = true;
				}

				while (nextDevicesCollectionIterationTimestamp > System.currentTimeMillis()) {
					try {
						TimeUnit.MILLISECONDS.sleep(1000);
					} catch (InterruptedException e) {
						//
					}
				}

				if (!inProgress) {
					break loop;
				}
				if (flag) {
					nextDevicesCollectionIterationTimestamp = System.currentTimeMillis() + 30000;
					flag = false;
				}

				if (logger.isDebugEnabled()) {
					logger.debug("Finished collecting devices statistics cycle at " + new Date());
				}
			}
			// Finished collecting
		}

		/**
		 * Triggers main loop to stop
		 */
		public void stop() {
			inProgress = false;
		}
	}

	/**
	 * Private variable representing the local extended statistics.
	 */
	private ExtendedStatistics localExtendedStatistics;

	/**
	 * A private final ReentrantLock instance used to provide exclusive access to a shared resource
	 * that can be accessed by multiple threads concurrently. This lock allows multiple reentrant
	 * locks on the same shared resource by the same thread.
	 */
	private final ReentrantLock reentrantLock = new ReentrantLock();

	/**
	 * A mapper for reading and writing JSON using Jackson library.
	 * ObjectMapper provides functionality for converting between Java objects and JSON.
	 * It can be used to serialize objects to JSON format, and deserialize JSON data to objects.
	 */
	private final ObjectMapper objectMapper = new ObjectMapper();

	/**
	 * Indicates whether a device is considered as paused.
	 * True by default so if the system is rebooted and the actual value is lost -> the device won't start stats
	 * collection unless the {@link MagicInfoCommunicator#retrieveMultipleStatistics()} method is called which will change it
	 * to a correct value
	 */
	private volatile boolean devicePaused = true;

	/**
	 * We don't want the statistics to be collected constantly, because if there's not a big list of devices -
	 * new devices' statistics loop will be launched before the next monitoring iteration. To avoid that -
	 * this variable stores a timestamp which validates it, so when the devices' statistics is done collecting, variable
	 * is set to currentTime + 30s, at the same time, calling {@link #retrieveMultipleStatistics()} and updating the
	 * {@link #cachedAggregatedDeviceList} resets it to the currentTime timestamp, which will re-activate data collection.
	 */
	private long nextDevicesCollectionIterationTimestamp;

	/**
	 * This parameter holds timestamp of when we need to stop performing API calls
	 * It used when device stop retrieving statistic. Updated each time of called #retrieveMultipleStatistics
	 */
	private volatile long validRetrieveStatisticsTimestamp;

	/**
	 * Aggregator inactivity timeout. If the {@link MagicInfoCommunicator#retrieveMultipleStatistics()}  method is not
	 * called during this period of time - device is considered to be paused, thus the Cloud API
	 * is not supposed to be called
	 */
	private static final long retrieveStatisticsTimeOut = 3 * 60 * 1000;

	/**
	 * Executor that runs all the async operations, that is posting and
	 */
	private ExecutorService executorService;

	/**
	 * A private field that represents an instance of the PolyLensDataLoader class, which is responsible for loading device data for PolyLens.
	 */
	private MagicInfoDataLoader deviceDataLoader;

	/**
	 * An instance of the AggregatedDeviceProcessor class used to process and aggregate device-related data.
	 */
	private AggregatedDeviceProcessor aggregatedDeviceProcessor;

	Map<String, Map<String, String>> localCachedStatistic = new HashMap<>();

	/**
	 * List of aggregated device
	 */
	private List<AggregatedDevice> cachedAggregatedDeviceList = Collections.synchronizedList(new ArrayList<>());

	/**
	 * List of aggregated device
	 */
	private List<AggregatedDevice> aggregatedDeviceList = Collections.synchronizedList(new ArrayList<>());

	private boolean checkControl = false;

	/**
	 * API Token
	 */
	private String apiToken;

	/**
	 * save time get token
	 */
	private Long tokenExpire;

	/**
	 * time the token expires
	 */
	private Long expiresIn = 1500L * 1000;

	/**
	 * A JSON node containing the response from an aggregator.
	 */
	private JsonNode aggregatorResponse;

	/**
	 * A list of aggregated IDs, synchronized for thread safety.
	 */
	private List<String> aggregatedIdList = Collections.synchronizedList(new ArrayList<>());

	/**
	 * A list of device types, synchronized for thread safety.
	 */
	private List<DeviceType> deviceTypeList = Collections.synchronizedList(new ArrayList<>());

	/**
	 * A filter for device type.
	 */
	private String filterDeviceType;

	/**
	 * A filter for source.
	 */
	private String filterSource;

	/**
	 * Update the status of the device.
	 * The device is considered as paused if did not receive any retrieveMultipleStatistics()
	 * calls during {@link MagicInfoCommunicator}
	 */
	private synchronized void updateAggregatorStatus() {
		devicePaused = validRetrieveStatisticsTimestamp < System.currentTimeMillis();
	}

	/**
	 * Uptime time stamp to valid one
	 */
	private synchronized void updateValidRetrieveStatisticsTimestamp() {
		validRetrieveStatisticsTimestamp = System.currentTimeMillis() + retrieveStatisticsTimeOut;
		updateAggregatorStatus();
	}

	/**
	 * Constructs a new instance of the MagicInfoCommunicator class.
	 * This constructor initializes the communicator with the necessary components and settings to interact with Samsung MagicInfo.
	 *
	 * @throws IOException if an I/O error occurs during the initialization process.
	 */
	public MagicInfoCommunicator() throws IOException {
		Map<String, PropertiesMapping> mapping = new PropertiesMappingParser().loadYML(MagicInfoConstant.MODEL_MAPPING_AGGREGATED_DEVICE, getClass());
		aggregatedDeviceProcessor = new AggregatedDeviceProcessor(mapping);
		this.setTrustAllCertificates(true);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 *
	 * Check for available devices before retrieving the value
	 * ping latency information to Symphony
	 */
	@Override
	public int ping() throws Exception {
		if (isInitialized()) {
			long pingResultTotal = 0L;

			for (int i = 0; i < this.getPingAttempts(); i++) {
				long startTime = System.currentTimeMillis();

				try (Socket puSocketConnection = new Socket(this.host, this.getPort())) {
					puSocketConnection.setSoTimeout(this.getPingTimeout());
					if (puSocketConnection.isConnected()) {
						long pingResult = System.currentTimeMillis() - startTime;
						pingResultTotal += pingResult;
						if (this.logger.isTraceEnabled()) {
							this.logger.trace(String.format("PING OK: Attempt #%s to connect to %s on port %s succeeded in %s ms", i + 1, host, this.getPort(), pingResult));
						}
					} else {
						if (this.logger.isDebugEnabled()) {
							logger.debug(String.format("PING DISCONNECTED: Connection to %s did not succeed within the timeout period of %sms", host, this.getPingTimeout()));
						}
						return this.getPingTimeout();
					}
				} catch (SocketTimeoutException | ConnectException tex) {
					throw new SocketTimeoutException("Socket connection timed out");
				} catch (UnknownHostException tex) {
					throw new SocketTimeoutException("Socket connection timed out" + tex.getMessage());
				} catch (Exception e) {
					if (this.logger.isWarnEnabled()) {
						this.logger.warn(String.format("PING TIMEOUT: Connection to %s did not succeed, UNKNOWN ERROR %s: ", host, e.getMessage()));
					}
					return this.getPingTimeout();
				}
			}
			return Math.max(1, Math.toIntExact(pingResultTotal / this.getPingAttempts()));
		} else {
			throw new IllegalStateException("Cannot use device class without calling init() first");
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Statistics> getMultipleStatistics() throws Exception {
		reentrantLock.lock();
		try {
			if (!checkValidApiToken()) {
				throw new ResourceNotReachableException("API Token cannot be null or empty, please enter valid API token in the password and username field.");
			}
			Map<String, String> statistics = new HashMap<>();
			ExtendedStatistics extendedStatistics = new ExtendedStatistics();
			retrieveSystemInfo();
			populateSystemData(statistics);
			extendedStatistics.setStatistics(statistics);
			localExtendedStatistics = extendedStatistics;
		} finally {
			reentrantLock.unlock();
		}
		return Collections.singletonList(localExtendedStatistics);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void controlProperty(ControllableProperty controllableProperty) throws Exception {

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void controlProperties(List<ControllableProperty> controllableProperties) throws Exception {

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<AggregatedDevice> retrieveMultipleStatistics() throws Exception {
		if (!aggregatedIdList.isEmpty()) {
			if (checkValidApiToken()) {
				if (executorService == null) {
					executorService = Executors.newFixedThreadPool(1);
					executorService.submit(deviceDataLoader = new MagicInfoDataLoader());
				}
				nextDevicesCollectionIterationTimestamp = System.currentTimeMillis();
				updateValidRetrieveStatisticsTimestamp();
			}
			if (cachedAggregatedDeviceList.isEmpty()) {
				return cachedAggregatedDeviceList;
			}
			return cloneAndPopulateAggregatedDeviceList();
		}
		return Collections.emptyList();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<AggregatedDevice> retrieveMultipleStatistics(List<String> list) throws Exception {
		return retrieveMultipleStatistics().stream().filter(aggregatedDevice -> list.contains(aggregatedDevice.getDeviceId())).collect(Collectors.toList());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void authenticate() throws Exception {
		// Samsung MagicInfo only require API token for each request.
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void internalInit() throws Exception {
		if (logger.isDebugEnabled()) {
			logger.debug("Internal init is called.");
		}
		executorService = Executors.newFixedThreadPool(1);
		executorService.submit(deviceDataLoader = new MagicInfoDataLoader());
		super.internalInit();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void internalDestroy() {
		if (logger.isDebugEnabled()) {
			logger.debug("Internal destroy is called.");
		}
		if (deviceDataLoader != null) {
			deviceDataLoader.stop();
			deviceDataLoader = null;
		}

		if (executorService != null) {
			executorService.shutdownNow();
			executorService = null;
		}
		if (localExtendedStatistics != null && localExtendedStatistics.getStatistics() != null && localExtendedStatistics.getControllableProperties() != null) {
			localExtendedStatistics.getStatistics().clear();
			localExtendedStatistics.getControllableProperties().clear();
		}
		nextDevicesCollectionIterationTimestamp = 0;
		cachedAggregatedDeviceList.clear();
		aggregatedDeviceList.clear();
		aggregatedIdList.clear();
		deviceTypeList.clear();
		localCachedStatistic.clear();
		super.internalDestroy();
	}

	/**
	 * {@inheritDoc}
	 * set API_Key into Header of Request
	 */
	@Override
	protected HttpHeaders putExtraRequestHeaders(HttpMethod httpMethod, String uri, HttpHeaders headers) {
		headers.set("api_key", apiToken);
		return headers;
	}

	/**
	 * Check API token validation
	 * If the token expires, we send a request to get a new token
	 *
	 * @return boolean
	 */
	private boolean checkValidApiToken() {
		if (StringUtils.isNullOrEmpty(getLogin()) || StringUtils.isNullOrEmpty(getPassword())) {
			return false;
		}
		if (StringUtils.isNullOrEmpty(apiToken) || System.currentTimeMillis() - tokenExpire >= expiresIn) {
			apiToken = getToken();
		}
		return StringUtils.isNotNullOrEmpty(apiToken);
	}

	/**
	 * Retrieves a token using the provided username and password
	 *
	 * @return the token string
	 */
	private String getToken() {
		String token = MagicInfoConstant.EMPTY;
		tokenExpire = System.currentTimeMillis();
		Map<String, String> params = new HashMap<>();
		params.put(MagicInfoConstant.USERNAME, this.getLogin());
		params.put(MagicInfoConstant.PASSWORD, this.getPassword());
		try {
			JsonNode response = doPost(MagicInfoCommand.AUTH_COMMAND, params, JsonNode.class);
			if (response != null && response.has(MagicInfoConstant.TOKEN)) {
				token = response.get(MagicInfoConstant.TOKEN).asText();
			}
		} catch (Exception e) {
			throw new ResourceNotReachableException("Can't get token from username and password", e);
		}
		return token;
	}

	/**
	 * Get system information of SamsungMagicInfo
	 */
	private void retrieveSystemInfo() {
		try {
			aggregatorResponse = this.doGet(MagicInfoCommand.DEVICE_DASHBOARD, JsonNode.class);
			JsonNode deviceTypeResponse = this.doGet(MagicInfoCommand.DEVICE_TYPE_COMMAND, JsonNode.class);
			if (deviceTypeResponse != null && deviceTypeResponse.has(MagicInfoConstant.ITEMS) && deviceTypeResponse.get(MagicInfoConstant.ITEMS).has(MagicInfoConstant.DEVICE_LIST)) {
				deviceTypeList.clear();
				deviceTypeList = objectMapper.readValue(deviceTypeResponse.get(MagicInfoConstant.ITEMS).get(MagicInfoConstant.DEVICE_LIST).toString(), new TypeReference<List<DeviceType>>() {
				});
			}

			filterDevice();
		} catch (Exception e) {
			aggregatorResponse = objectMapper.createObjectNode();
			aggregatedIdList.clear();
			logger.error(String.format("Error when get system information, %s", e));
		}
	}

	/**
	 * Filters devices based on the specified device type and source filters, and updates the aggregated ID list accordingly.
	 * If either the device type or source filter is not null or empty, a filtering request is made to retrieve devices that match the criteria.
	 * Otherwise, all devices are retrieved.
	 *
	 * @throws Exception If an error occurs while filtering devices.
	 */
	private void filterDevice() throws Exception {
		JsonNode devicesResponse;
		if (StringUtils.isNotNullOrEmpty(filterDeviceType) || StringUtils.isNotNullOrEmpty(filterSource)) {
			devicesResponse = this.doGet(MagicInfoCommand.FILTERING_COMMAND, JsonNode.class);
		} else {
			devicesResponse = this.doGet(MagicInfoCommand.ALL_DEVICES_COMMAND, JsonNode.class);
		}
		if (devicesResponse != null && devicesResponse.has(MagicInfoConstant.ITEMS)) {
			aggregatedIdList.clear();
			for (JsonNode item : devicesResponse.get(MagicInfoConstant.ITEMS)) {
				aggregatedIdList.add(item.get(MagicInfoConstant.DEVICE_ID).asText());
			}
		}
	}

	/**
	 * Populates the given statistics map with system-related data.
	 *
	 * @param statistics the map to be populated with system data
	 */
	private void populateSystemData(Map<String, String> statistics) {
		for (SystemInfo item : SystemInfo.values()) {
			if (aggregatorResponse != null && aggregatorResponse.has(MagicInfoConstant.ITEMS) && aggregatorResponse.get(MagicInfoConstant.ITEMS).has(MagicInfoConstant.STATUS) && aggregatorResponse.get(
					MagicInfoConstant.ITEMS).get(MagicInfoConstant.STATUS).has(item.getValue())) {
				statistics.put(item.getName(), aggregatorResponse.get(MagicInfoConstant.ITEMS).get(MagicInfoConstant.STATUS).get(item.getValue()).asText());
			} else {
				statistics.put(item.getName(), MagicInfoConstant.NONE);
			}
		}
	}

	/**
	 * populate detail aggregated device
	 * add aggregated device into aggregated device list
	 */
	private void populateDeviceDetails() {
		try {
			ObjectNode idListParam = objectMapper.createObjectNode();
			idListParam.set(MagicInfoConstant.IDS, objectMapper.valueToTree(aggregatedIdList));
			JsonNode generalInfoResponse = this.doPost(MagicInfoCommand.GENERAL_INFO_COMMAND, (JsonNode) idListParam, JsonNode.class);
			JsonNode displayInfoResponse = this.doPost(MagicInfoCommand.DISPLAY_INFO_COMMAND, (JsonNode) idListParam, JsonNode.class);

			if (generalInfoResponse != null && generalInfoResponse.has(MagicInfoConstant.ITEMS) && generalInfoResponse.get(MagicInfoConstant.ITEMS).has(MagicInfoConstant.SUCCESS_LIST) &&
					displayInfoResponse != null && displayInfoResponse.has(MagicInfoConstant.ITEMS) && displayInfoResponse.get(MagicInfoConstant.ITEMS).has(MagicInfoConstant.SUCCESS_LIST)) {
				for (int i = 0; i < aggregatedIdList.size(); i++) {
					JsonNode generalItem = generalInfoResponse.get(MagicInfoConstant.ITEMS).get(MagicInfoConstant.SUCCESS_LIST).get(i);
					JsonNode displayItem = displayInfoResponse.get(MagicInfoConstant.ITEMS).get(MagicInfoConstant.SUCCESS_LIST).get(i);
					String id = generalItem.get(MagicInfoConstant.GENERAL_CONF).get(MagicInfoConstant.DEVICE_ID).asText();
					JsonNode node = objectMapper.createArrayNode().add(combineJsonNodes(generalItem, displayItem));
					cachedAggregatedDeviceList.removeIf(item -> item.getDeviceId().equals(id));
					cachedAggregatedDeviceList.addAll(aggregatedDeviceProcessor.extractDevices(node));
				}
			}
		} catch (Exception e) {
			logger.error("Error while populate aggregated device", e);
		}
	}

	/**
	 * Clone an aggregated device list that based on aggregatedDeviceList variable
	 * populate monitoring and controlling for aggregated device
	 *
	 * @return List<AggregatedDevice> aggregated device list
	 */
	private List<AggregatedDevice> cloneAndPopulateAggregatedDeviceList() {
		if (!checkControl) {
			localCachedStatistic.clear();
			if (!cachedAggregatedDeviceList.isEmpty()) {
				aggregatedDeviceList.clear();
			}
			synchronized (cachedAggregatedDeviceList) {
				for (AggregatedDevice aggregatedDevice : cachedAggregatedDeviceList) {
					localCachedStatistic.put(aggregatedDevice.getDeviceId(), aggregatedDevice.getProperties());
					List<AdvancedControllableProperty> advancedControllableProperties = new ArrayList<>();
					Map<String, String> dynamics = new HashMap<>();
					Map<String, String> stats = new HashMap<>();
					aggregatedDevice.setDeviceOnline(true);
					mapGeneralInformationProperties(aggregatedDevice.getProperties(), stats);
					aggregatedDevice.setProperties(stats);
					aggregatedDevice.setControllableProperties(advancedControllableProperties);
					aggregatedDevice.setDynamicStatistics(dynamics);
					aggregatedDeviceList.add(aggregatedDevice);
				}
			}
		}
		checkControl = false;
		logger.debug(aggregatedDeviceList.get(0).getProperties());
		logger.debug(aggregatedDeviceList.get(0).getControllableProperties());
		return aggregatedDeviceList;
	}

	/**
	 * Maps general information properties from a mapping statistic to a target statistics map.
	 * This method processes specific properties from the provided {@code localCachedStatistic} and updates the {@code stats} map accordingly.
	 *
	 * @param stats The target statistics map where the properties will be mapped.
	 */
	private void mapGeneralInformationProperties(Map<String, String> mappingStatistic, Map<String, String> stats) {
		String value;
		String propertyName;
		for (GeneralInfo item : GeneralInfo.values()) {
			propertyName = item.getName();
			value = getDefaultValueForNullData(mappingStatistic.get(propertyName));
			switch (item) {
				case APPROVAL_DATE:
				case LAST_CONNECTION_TIME:
					stats.put(propertyName, convertMillisecondsToDate(value));
					break;
				case DISK_SPACE_USAGE:
				case AVAILABLE_CAPACITY:
					stats.put(propertyName, convertToMemoryFormat(value));
					break;
				default:
					stats.put(propertyName, value);
			}
		}
	}

	/**
	 * Combines two JSON nodes into a single JSON node.
	 *
	 * @param nodeA The first JSON node to combine.
	 * @param nodeB The second JSON node to combine.
	 * @return A combined JSON node containing properties from both input nodes, or null if either of the input nodes is not an object.
	 */
	private JsonNode combineJsonNodes(JsonNode nodeA, JsonNode nodeB) {
		if (nodeA.isObject() && nodeB.isObject()) {
			ObjectNode combinedNode = JsonNodeFactory.instance.objectNode();
			combinedNode.setAll((ObjectNode) nodeA);
			combinedNode.setAll((ObjectNode) nodeB);

			return combinedNode;
		}
		return null;
	}

	/**
	 * Converts a value from milliseconds to a formatted date string.
	 *
	 * @param value the value in milliseconds
	 * @return the formatted date string in the format "MMM yyyy", or "none" if an error occurs
	 */
	private String convertMillisecondsToDate(String value) {
		if (MagicInfoConstant.NONE.equals(value)) {
			return value;
		}
		try {
			long milliseconds = Long.parseLong(value);
			Date date = new Date(milliseconds);
			SimpleDateFormat dateFormat = new SimpleDateFormat(MagicInfoConstant.NEW_FORMAT_DATETIME);
			return dateFormat.format(date);
		} catch (Exception e) {
			logger.debug("Error when convert milliseconds to datetime " + value + "DDD", e);
		}
		return MagicInfoConstant.NONE;
	}

	/**
	 * Converts a memory size string to a more human-readable format (e.g., from "1024:MB" to "1.0 GB").
	 *
	 * @param input The input memory size string in the format "value:unit" (e.g., "1024:MB").
	 * @return A human-readable memory size string in the format "value unit" (e.g., "1.0 GB").
	 */
	private String convertToMemoryFormat(String input) {
		if (MagicInfoConstant.NONE.equals(input)) {
			return input;
		}

		String[] parts = input.split(MagicInfoConstant.COLON);
		if (parts.length != 2) {
			return input;
		}

		String prefix = parts[0].trim();
		String data = parts[1].trim();
		try {
			double dataSize = Double.parseDouble(data);
			if (dataSize < 1024 * 1024) {
				return formatDataSize(prefix, dataSize / 1024, "MB");
			} else {
				return formatDataSize(prefix, dataSize / (1024 * 1024), "GB");
			}
		} catch (NumberFormatException e) {
			return MagicInfoConstant.NONE;
		}
	}

	/**
	 * Formats a data size value and unit into a human-readable string.
	 *
	 * @param prefix The prefix to include in the formatted string (e.g., "Available Capacity").
	 * @param dataSize The data size value to be formatted.
	 * @param unit The data size unit (e.g., "MB" or "GB").
	 * @return The formatted data size string in the format "prefix: value unit" (e.g., "Available Capacity: 1.0 GB").
	 */
	public static String formatDataSize(String prefix, double dataSize, String unit) {
		String result;
		if ("MB".equals(unit)) {
			result = String.valueOf(Math.round(dataSize));
		} else {
			DecimalFormat decimalFormat = new DecimalFormat("#.##");
			result = decimalFormat.format(dataSize);
		}
		return prefix + ": " + result + " " + unit;
	}

	/**
	 * check value is null or empty
	 *
	 * @param value input value
	 * @return value after checking
	 */
	private String getDefaultValueForNullData(String value) {
		return StringUtils.isNotNullOrEmpty(value) ? value : MagicInfoConstant.NONE;
	}
}
