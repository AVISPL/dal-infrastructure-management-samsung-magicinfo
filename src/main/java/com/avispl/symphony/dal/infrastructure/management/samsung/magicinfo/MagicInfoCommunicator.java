/*
 *  Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.samsung.magicinfo;

import static com.avispl.symphony.dal.infrastructure.management.samsung.magicinfo.common.DisplayInfo.SCREEN_LAMP_SCHEDULE;

import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
import com.avispl.symphony.dal.infrastructure.management.samsung.magicinfo.common.DisplayInfo;
import com.avispl.symphony.dal.infrastructure.management.samsung.magicinfo.common.EnumTypeHandler;
import com.avispl.symphony.dal.infrastructure.management.samsung.magicinfo.common.GeneralInfo;
import com.avispl.symphony.dal.infrastructure.management.samsung.magicinfo.common.MagicInfoCommand;
import com.avispl.symphony.dal.infrastructure.management.samsung.magicinfo.common.MagicInfoConstant;
import com.avispl.symphony.dal.infrastructure.management.samsung.magicinfo.common.QuickControl;
import com.avispl.symphony.dal.infrastructure.management.samsung.magicinfo.common.SystemInfo;
import com.avispl.symphony.dal.infrastructure.management.samsung.magicinfo.common.general.SourceEnum;
import com.avispl.symphony.dal.infrastructure.management.samsung.magicinfo.common.general.WebBrowserIntervalEnum;
import com.avispl.symphony.dal.infrastructure.management.samsung.magicinfo.common.general.WebBrowserZoomEnum;
import com.avispl.symphony.dal.infrastructure.management.samsung.magicinfo.common.picture.ColorTemperatureEnum;
import com.avispl.symphony.dal.infrastructure.management.samsung.magicinfo.common.picture.ColorToneEnum;
import com.avispl.symphony.dal.infrastructure.management.samsung.magicinfo.common.picture.DigitalCleanViewEnum;
import com.avispl.symphony.dal.infrastructure.management.samsung.magicinfo.common.picture.FilmModeEnum;
import com.avispl.symphony.dal.infrastructure.management.samsung.magicinfo.common.picture.HDMIBlackLevelEnum;
import com.avispl.symphony.dal.infrastructure.management.samsung.magicinfo.common.picture.LEDPictureSizeEnum;
import com.avispl.symphony.dal.infrastructure.management.samsung.magicinfo.common.picture.PictureSizeEnum;
import com.avispl.symphony.dal.infrastructure.management.samsung.magicinfo.common.screen.ImmediateDisplayEnum;
import com.avispl.symphony.dal.infrastructure.management.samsung.magicinfo.common.screen.IntervalModeEnum;
import com.avispl.symphony.dal.infrastructure.management.samsung.magicinfo.common.screen.RepeatModeEnum;
import com.avispl.symphony.dal.infrastructure.management.samsung.magicinfo.common.screen.TimerEnum;
import com.avispl.symphony.dal.infrastructure.management.samsung.magicinfo.common.sound.SoundModeEnum;
import com.avispl.symphony.dal.infrastructure.management.samsung.magicinfo.dto.DeviceType;
import com.avispl.symphony.dal.infrastructure.management.samsung.magicinfo.statistics.DynamicStatisticsDefinition;
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

	/**
	 *
	 */
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
	 * Configurable property for historical properties, comma separated values kept as set locally
	 */
	private Set<String> historicalProperties = new HashSet<>();

	/**
	 * A filter for device type.
	 */
	private String filterDeviceType;

	/**
	 * A filter for source.
	 */
	private String filterSource;

	/**
	 * Retrieves {@link #filterDeviceType}
	 *
	 * @return value of {@link #filterDeviceType}
	 */
	public String getFilterDeviceType() {
		return filterDeviceType;
	}

	/**
	 * Sets {@link #filterDeviceType} value
	 *
	 * @param filterDeviceType new value of {@link #filterDeviceType}
	 */
	public void setFilterDeviceType(String filterDeviceType) {
		this.filterDeviceType = filterDeviceType;
	}

	/**
	 * Retrieves {@link #filterSource}
	 *
	 * @return value of {@link #filterSource}
	 */
	public String getFilterSource() {
		return filterSource;
	}

	/**
	 * Sets {@link #filterSource} value
	 *
	 * @param filterSource new value of {@link #filterSource}
	 */
	public void setFilterSource(String filterSource) {
		this.filterSource = filterSource;
	}

	/**
	 * Retrieves {@link #historicalProperties}
	 *
	 * @return value of {@link #historicalProperties}
	 */
	public String getHistoricalProperties() {
		return String.join(",", this.historicalProperties);
	}

	/**
	 * Sets {@link #historicalProperties} value
	 *
	 * @param historicalProperties new value of {@link #historicalProperties}
	 */
	public void setHistoricalProperties(String historicalProperties) {
		this.historicalProperties.clear();
		Arrays.asList(historicalProperties.split(",")).forEach(propertyName -> {
			this.historicalProperties.add(propertyName.trim());
		});
	}

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
			devicesResponse = this.doPost(MagicInfoCommand.FILTERING_COMMAND, createBodyFilteringRequest(), JsonNode.class);
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

	private JsonNode createBodyFilteringRequest() {
		List<String> sourceValue = new ArrayList<>();
		List<String> deviceTypeValue = new ArrayList<>();
		if (StringUtils.isNotNullOrEmpty(filterDeviceType)) {
			deviceTypeValue = Arrays.stream(filterDeviceType.split(",")).map(String::trim).collect(Collectors.toList());
		}

		if (StringUtils.isNotNullOrEmpty(filterSource)) {
			sourceValue = Arrays.stream(filterSource.split(",")).map(String::trim)
					.map(item -> defaultSourceValue(EnumTypeHandler.getValueByName(SourceEnum.class, item)))
					.collect(Collectors.toList());
		}
		ObjectNode body = objectMapper.createObjectNode();
		body.put(MagicInfoConstant.PAGE_SIZE, "400");
		body.set(MagicInfoConstant.DEVICE_TYPE, objectMapper.valueToTree(deviceTypeValue));
		body.set(MagicInfoConstant.INPUT_SOURCE, objectMapper.valueToTree(sourceValue));
		return body;
	}

	private String defaultSourceValue(String value) {
		if (MagicInfoConstant.NONE.equals(value)) {
			return "100000";
		}
		return value;
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
					mapQuickControlProperties(aggregatedDevice.getProperties(), stats, advancedControllableProperties);
					mapDisplayInformationProperties(aggregatedDevice.getProperties(), stats, advancedControllableProperties);
					mapDynamicStatistic(aggregatedDevice.getProperties(), stats, dynamics);

					aggregatedDevice.setProperties(stats);
					aggregatedDevice.setControllableProperties(advancedControllableProperties);
					aggregatedDevice.setDynamicStatistics(dynamics);
					aggregatedDeviceList.add(aggregatedDevice);
				}
			}
		}
		checkControl = false;
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

	private void mapQuickControlProperties(Map<String, String> mappingStatistic, Map<String, String> stats, List<AdvancedControllableProperty> advancedControllableProperties) {
		String value;
		String propertyName;
		String group = "QuickControl#";
		for (QuickControl item : QuickControl.values()) {
			propertyName = group.concat(item.getName());
			value = getDefaultValueForNullData(mappingStatistic.get(item.getName()));
			switch (item) {
				case POWER:
					addAdvanceControlProperties(advancedControllableProperties, stats, createSwitch(propertyName, MagicInfoConstant.TRUE.equals(value) ? 1 : 0, MagicInfoConstant.OFF, MagicInfoConstant.ON),
							MagicInfoConstant.TRUE.equals(value) ? "1" : "0");
					break;
				case VOLUME:
					addAdvanceControlProperties(advancedControllableProperties, stats, createNumeric(propertyName, value), value);
					break;
				case MUTE:
					addAdvanceControlProperties(advancedControllableProperties, stats, createSwitch(propertyName, Integer.parseInt(value), MagicInfoConstant.OFF, MagicInfoConstant.ON), value);
					break;
				case DISPLAY_PANEL:
					value = MagicInfoConstant.NUMBER_ONE.equals(value) ? "0" : "1";
					addAdvanceControlProperties(advancedControllableProperties, stats, createSwitch(propertyName, Integer.parseInt(value), MagicInfoConstant.OFF, MagicInfoConstant.ON), value);
					break;
				case SOURCE:
					List<String> availableValues = Arrays.stream(EnumTypeHandler.getEnumValues(SourceEnum.class)).collect(Collectors.toList());
					if (availableValues.contains(value)) {
						addAdvanceControlProperties(advancedControllableProperties, stats,
								createDropdown(propertyName, EnumTypeHandler.getEnumNames(SourceEnum.class), EnumTypeHandler.getNameByValue(SourceEnum.class, value)), value);
					} else {
						stats.put(propertyName, MagicInfoConstant.NONE);
					}
					break;
				case RESTART:
					addAdvanceControlProperties(advancedControllableProperties, stats, createButton(propertyName, MagicInfoConstant.RESTART, MagicInfoConstant.RESTARTING, MagicInfoConstant.GRACE_PERIOD),
							MagicInfoConstant.NONE);
					break;
				default:
					stats.put(propertyName, MagicInfoConstant.NONE);
			}
		}
	}

	/**
	 * Maps display information properties from a mapping statistic to a target statistics map.
	 * This method processes specific properties from the provided {@code localCachedStatistic} and updates the {@code stats} map accordingly.
	 *
	 * @param stats The target statistics map where the properties will be mapped.
	 * @param advancedControllableProperties A list to collect advanced controllable properties.
	 */
	private void mapDisplayInformationProperties(Map<String, String> mappingStatistic, Map<String, String> stats, List<AdvancedControllableProperty> advancedControllableProperties) {
		String value;
		String propertyName;
		int status;
		for (DisplayInfo item : DisplayInfo.values()) {
			propertyName = item.getGroup().concat(item.getName());
			value = getDefaultValueForNullData(mappingStatistic.get(item.getName()));
			switch (item) {
				case DISPLAY_PANEL:
					value = MagicInfoConstant.NUMBER_ONE.equals(value) ? "0" : "1";
					addAdvanceControlProperties(advancedControllableProperties, stats, createSwitch(propertyName, Integer.parseInt(value), MagicInfoConstant.OFF, MagicInfoConstant.ON), value);
					break;
				case VOLUME:
				case TEMPERATURE_CONTROL:
				case LAMP_CONTROL:
				case BRIGHTNESS:
				case COLOR:
				case CONTRAST:
				case TINT:
				case SHARPNESS:
				case PIXEL_SHIFT_VERTICAL:
				case PIXEL_SHIFT_HORIZONTAL:
				case PIXEL_SHIFT_TIME:
					addAdvanceControlProperties(advancedControllableProperties, stats, createNumeric(propertyName, value), value);
					break;
				case MIN_VALUE:
				case MAX_VALUE:
					if (MagicInfoConstant.NUMBER_ONE.equals(mappingStatistic.get(SCREEN_LAMP_SCHEDULE.getName()))) {
						addAdvanceControlProperties(advancedControllableProperties, stats, createNumeric(propertyName, value), value);
					} else {
						stats.put(propertyName, value);
					}
					break;
				case WEB_BROWSER_HOME_PAGE:
					status = getSwitchStatus(value);
					if (status == -1) {
						stats.put(propertyName, MagicInfoConstant.NONE);
					} else {
						addAdvanceControlProperties(advancedControllableProperties, stats, createSwitch(propertyName, status, MagicInfoConstant.SAMSUNG_DISPLAY, MagicInfoConstant.CUSTOM), value);
					}
					break;
				case WEB_BROWSER_PAGE_URL:
					if (MagicInfoConstant.NUMBER_ONE.equals(mappingStatistic.get(MagicInfoConstant.WEB_BROWSER_HOME_PAGE))) {
						addAdvanceControlProperties(advancedControllableProperties, stats, createText(propertyName, value), value);
					}
					break;
				case MUTE:
				case MAX_POWER_SAVING:
				case PICTURE_ENHANCER:
				case AUTO_POWER_ON:
				case REMOTE_CONFIGURATION:
				case AUTO_SOURCE_SWITCHING:
				case SCREEN_LAMP_SCHEDULE:
				case PIXEL_SHIFT:
					status = getSwitchStatus(value);
					if (status == -1) {
						stats.put(propertyName, MagicInfoConstant.NONE);
					} else {
						addAdvanceControlProperties(advancedControllableProperties, stats, createSwitch(propertyName, status, MagicInfoConstant.OFF, MagicInfoConstant.ON), value);
					}
					break;
				case WEB_BROWSER_INTERVAL:
					addAdvanceControlProperties(advancedControllableProperties, stats,
							createDropdown(propertyName, EnumTypeHandler.getEnumNames(WebBrowserIntervalEnum.class), EnumTypeHandler.getNameByValue(WebBrowserIntervalEnum.class, value)), value);
					break;
				case WEB_BROWSER_ZOOM:
					addAdvanceControlProperties(advancedControllableProperties, stats,
							createDropdown(propertyName, EnumTypeHandler.getEnumNames(WebBrowserZoomEnum.class), EnumTypeHandler.getNameByValue(WebBrowserZoomEnum.class, value)), value);
					break;
				case SOURCE:
					List<String> availableValues = Arrays.stream(EnumTypeHandler.getEnumValues(SourceEnum.class)).collect(Collectors.toList());
					if (availableValues.contains(value)) {
						addAdvanceControlProperties(advancedControllableProperties, stats,
								createDropdown(propertyName, EnumTypeHandler.getEnumNames(SourceEnum.class), EnumTypeHandler.getNameByValue(SourceEnum.class, value)), value);
					} else {
						stats.put(propertyName, MagicInfoConstant.NONE);
					}
					break;
				case COLOR_TONE:
					addAdvanceControlProperties(advancedControllableProperties, stats,
							createDropdown(propertyName, EnumTypeHandler.getEnumNames(ColorToneEnum.class), EnumTypeHandler.getNameByValue(ColorToneEnum.class, value)), value);
					break;
				case COLOR_TEMPERATURE:
					availableValues = Arrays.stream(EnumTypeHandler.getEnumValues(ColorTemperatureEnum.class)).collect(Collectors.toList());
					if (availableValues.contains(value)) {
						addAdvanceControlProperties(advancedControllableProperties, stats,
								createDropdown(propertyName, EnumTypeHandler.getEnumNames(ColorTemperatureEnum.class), EnumTypeHandler.getNameByValue(ColorTemperatureEnum.class, value)), value);
					} else {
						stats.put(propertyName, MagicInfoConstant.NONE);
					}
					break;
				case PICTURE_SIZE:
					availableValues = Arrays.stream(EnumTypeHandler.getEnumValues(PictureSizeEnum.class)).collect(Collectors.toList());
					if (availableValues.contains(value)) {
						addAdvanceControlProperties(advancedControllableProperties, stats,
								createDropdown(propertyName, EnumTypeHandler.getEnumNames(PictureSizeEnum.class), EnumTypeHandler.getNameByValue(PictureSizeEnum.class, value)), value);
					} else {
						stats.put(propertyName, MagicInfoConstant.NONE);
					}
					break;
				case DIGITAL_CLEAN_VIEW:
					availableValues = Arrays.stream(EnumTypeHandler.getEnumValues(DigitalCleanViewEnum.class)).collect(Collectors.toList());
					if (availableValues.contains(value)) {
						addAdvanceControlProperties(advancedControllableProperties, stats,
								createDropdown(propertyName, EnumTypeHandler.getEnumNames(DigitalCleanViewEnum.class), EnumTypeHandler.getNameByValue(DigitalCleanViewEnum.class, value)), value);
					} else {
						stats.put(propertyName, MagicInfoConstant.NONE);
					}
					break;
				case FILM_MODE:
					availableValues = Arrays.stream(EnumTypeHandler.getEnumValues(FilmModeEnum.class)).collect(Collectors.toList());
					if (availableValues.contains(value)) {
						addAdvanceControlProperties(advancedControllableProperties, stats,
								createDropdown(propertyName, EnumTypeHandler.getEnumNames(FilmModeEnum.class), EnumTypeHandler.getNameByValue(FilmModeEnum.class, value)), value);
					} else {
						stats.put(propertyName, MagicInfoConstant.NONE);
					}
					break;
				case HDMI_BLACK_LEVEL:
					availableValues = Arrays.stream(EnumTypeHandler.getEnumValues(HDMIBlackLevelEnum.class)).collect(Collectors.toList());
					if (availableValues.contains(value)) {
						addAdvanceControlProperties(advancedControllableProperties, stats,
								createDropdown(propertyName, EnumTypeHandler.getEnumNames(HDMIBlackLevelEnum.class), EnumTypeHandler.getNameByValue(HDMIBlackLevelEnum.class, value)), value);
					} else {
						stats.put(propertyName, MagicInfoConstant.NONE);
					}
					break;
				case LED_PICTURE_SIZE:
					String[] arrayValues = value.split(MagicInfoConstant.SEMICOLON);
					if (arrayValues.length == 2) {
						if (MagicInfoConstant.NUMBER_ONE.equals(arrayValues[0])) {
							stats.put(MagicInfoConstant.PICTURE_PC.concat(MagicInfoConstant.RESOLUTION), arrayValues[1]);
						}
						addAdvanceControlProperties(advancedControllableProperties, stats,
								createDropdown(MagicInfoConstant.PICTURE_PC.concat(MagicInfoConstant.LED_PICTURE_SIZE), EnumTypeHandler.getEnumNames(LEDPictureSizeEnum.class),
										EnumTypeHandler.getNameByValue(LEDPictureSizeEnum.class, arrayValues[0])), arrayValues[0]);
					} else {
						stats.put(MagicInfoConstant.PICTURE_PC.concat(MagicInfoConstant.LED_PICTURE_SIZE), MagicInfoConstant.NONE);
					}
					break;
				case LED_HDR:
					arrayValues = value.split(MagicInfoConstant.SEMICOLON);
					if (arrayValues.length == 3) {
						addAdvanceControlProperties(advancedControllableProperties, stats,
								createSwitch(MagicInfoConstant.PICTURE_PC.concat(MagicInfoConstant.INVERSE_TONE_MAPPING), getSwitchStatus(arrayValues[0]), MagicInfoConstant.OFF, MagicInfoConstant.ON),
								arrayValues[0]);
						addAdvanceControlProperties(advancedControllableProperties, stats,
								createSwitch(MagicInfoConstant.PICTURE_PC.concat(MagicInfoConstant.DYNAMIC_PEAKING), getSwitchStatus(arrayValues[1]), MagicInfoConstant.OFF, MagicInfoConstant.ON), arrayValues[1]);
						addAdvanceControlProperties(advancedControllableProperties, stats,
								createSwitch(MagicInfoConstant.PICTURE_PC.concat(MagicInfoConstant.COLOR_MAPPING), getSwitchStatus(arrayValues[2]), MagicInfoConstant.OFF, MagicInfoConstant.ON), arrayValues[1]);
					} else {
						stats.put(MagicInfoConstant.PICTURE_PC.concat(MagicInfoConstant.INVERSE_TONE_MAPPING), MagicInfoConstant.NONE);
						stats.put(MagicInfoConstant.PICTURE_PC.concat(MagicInfoConstant.DYNAMIC_PEAKING), MagicInfoConstant.NONE);
						stats.put(MagicInfoConstant.PICTURE_PC.concat(MagicInfoConstant.COLOR_MAPPING), MagicInfoConstant.NONE);
					}
					break;
				case SOUND_MODE:
					availableValues = Arrays.stream(EnumTypeHandler.getEnumValues(SoundModeEnum.class)).collect(Collectors.toList());
					if (availableValues.contains(value)) {
						addAdvanceControlProperties(advancedControllableProperties, stats,
								createDropdown(propertyName, EnumTypeHandler.getEnumNames(SoundModeEnum.class), EnumTypeHandler.getNameByValue(SoundModeEnum.class, value)), value);
					} else {
						stats.put(propertyName, MagicInfoConstant.NONE);
					}
					break;
				case RESTORE_PRIMARY_SOURCE:
					if (MagicInfoConstant.NUMBER_ONE.equals(mappingStatistic.get(MagicInfoConstant.AUTO_SOURCE_SWITCHING))) {
						addAdvanceControlProperties(advancedControllableProperties, stats, createSwitch(propertyName, Integer.parseInt(value), MagicInfoConstant.OFF, MagicInfoConstant.ON), value);
					}
					break;
				case PRIMARY_SOURCE:
				case SECONDARY_SOURCE:
					if (MagicInfoConstant.NUMBER_ONE.equals(mappingStatistic.get(MagicInfoConstant.AUTO_SOURCE_SWITCHING))) {
						addAdvanceControlProperties(advancedControllableProperties, stats,
								createDropdown(propertyName, EnumTypeHandler.getEnumNames(SourceEnum.class), EnumTypeHandler.getNameByValue(SourceEnum.class, value)), value);
					}
					break;
				case MAX_TIME_HOUR:
				case MIN_TIME_HOUR:
					String time;
					if (MagicInfoConstant.NUMBER_ONE.equals(mappingStatistic.get(SCREEN_LAMP_SCHEDULE.getName()))) {
						time = convert12HourTo24Hour(value);
						if (!MagicInfoConstant.NONE.equals(time)) {
							String hour = time.split(MagicInfoConstant.COLON)[0];
							addAdvanceControlProperties(advancedControllableProperties, stats, createDropdown(propertyName, createArrayNumber(0, 23), hour), hour);
						} else {
							stats.put(propertyName, MagicInfoConstant.NONE);
						}
					} else {
						stats.put(propertyName.replace("(hour)", MagicInfoConstant.EMPTY), value);
					}
					break;
				case TIMER_END_TIME_HOUR:
				case TIMER_START_TIME_HOUR:
					time = convert12HourTo24Hour(value);
					if (!MagicInfoConstant.NONE.equals(time)) {
						String hour = time.split(MagicInfoConstant.COLON)[0];
						addAdvanceControlProperties(advancedControllableProperties, stats, createDropdown(propertyName, createArrayNumber(0, 23), hour), hour);
					} else {
						stats.put(propertyName, MagicInfoConstant.NONE);
					}
					break;
				case MAX_TIME_MINUTE:
				case MIN_TIME_MINUTE:
					if (MagicInfoConstant.NUMBER_ONE.equals(mappingStatistic.get(SCREEN_LAMP_SCHEDULE.getName()))) {
						time = convert12HourTo24Hour(value);
						if (!MagicInfoConstant.NONE.equals(time)) {
							String minute = time.split(MagicInfoConstant.COLON)[1];
							addAdvanceControlProperties(advancedControllableProperties, stats, createDropdown(propertyName, createArrayNumber(0, 59), minute), minute);
						} else {
							stats.put(propertyName, MagicInfoConstant.NONE);
						}
					}
					break;
				case TIMER_START_TIME_MIN:
				case TIMER_END_TIME_MIN:
					time = convert12HourTo24Hour(value);
					if (!MagicInfoConstant.NONE.equals(time)) {
						String minute = time.split(MagicInfoConstant.COLON)[1];
						addAdvanceControlProperties(advancedControllableProperties, stats, createDropdown(propertyName, createArrayNumber(0, 59), minute), minute);
					} else {
						stats.put(propertyName, MagicInfoConstant.NONE);
					}
					break;
				case TIMER:
					availableValues = Arrays.stream(EnumTypeHandler.getEnumValues(TimerEnum.class)).collect(Collectors.toList());
					if (availableValues.contains(value)) {
						addAdvanceControlProperties(advancedControllableProperties, stats,
								createDropdown(propertyName, EnumTypeHandler.getEnumNames(TimerEnum.class), EnumTypeHandler.getNameByValue(TimerEnum.class, value)), value);
					} else {
						stats.put(propertyName, MagicInfoConstant.NONE);
					}
					break;
				case TIMER_MODE:
					if (MagicInfoConstant.NUMBER_ONE.equals(mappingStatistic.get(MagicInfoConstant.TIMER))) {
						addAdvanceControlProperties(advancedControllableProperties, stats,
								createDropdown(propertyName, EnumTypeHandler.getEnumNames(RepeatModeEnum.class), EnumTypeHandler.getNameByValue(RepeatModeEnum.class, value)), value);
					} else if (MagicInfoConstant.NUMBER_TWO.equals(mappingStatistic.get(MagicInfoConstant.TIMER))) {
						addAdvanceControlProperties(advancedControllableProperties, stats,
								createDropdown(propertyName, EnumTypeHandler.getEnumNames(IntervalModeEnum.class), EnumTypeHandler.getNameByValue(IntervalModeEnum.class, value)), value);
					}
					break;
				case TIMER_PERIOD:
					if (MagicInfoConstant.NUMBER_ONE.equals(mappingStatistic.get(MagicInfoConstant.TIMER))) {
						addAdvanceControlProperties(advancedControllableProperties, stats, createNumeric(propertyName, value), value);
					}
					break;
				case TIMER_TIME:
					if (MagicInfoConstant.NUMBER_ONE.equals(mappingStatistic.get(MagicInfoConstant.TIMER))) {
						addAdvanceControlProperties(advancedControllableProperties, stats, createDropdown(propertyName, new String[] { "1", "10", "20", "30", "40", "50" }, value), value);
					}
					break;
				case IMMEDIATE_DISPLAY:
					availableValues = Arrays.stream(EnumTypeHandler.getEnumValues(ImmediateDisplayEnum.class)).collect(Collectors.toList());
					if (availableValues.contains(value)) {
						addAdvanceControlProperties(advancedControllableProperties, stats,
								createDropdown(propertyName, EnumTypeHandler.getEnumNames(ImmediateDisplayEnum.class), EnumTypeHandler.getNameByValue(ImmediateDisplayEnum.class, value)), value);
					} else {
						stats.put(propertyName, MagicInfoConstant.NONE);
					}
					break;
				default:
					stats.put(propertyName, value);
			}
		}
	}

	/**
	 * Maps dynamic statistics properties from a source mapping to either the 'stats' or 'dynamics' map,
	 * based on whether the property is listed in the 'historicalProperties' set.
	 *
	 * @param stats A map to store properties that are not listed in 'historicalProperties' with their values.
	 * @param dynamics A map to store properties that are listed in 'historicalProperties' with their values.
	 */
	private void mapDynamicStatistic(Map<String, String> mappingStatistic, Map<String, String> stats, Map<String, String> dynamics) {
		for (DynamicStatisticsDefinition property : DynamicStatisticsDefinition.values()) {
			String propertyName = property.getName();
			String groupName = property.getGroup();
			String propertyValue = mappingStatistic.get(propertyName);
			boolean propertyListed = false;
			if (!historicalProperties.isEmpty()) {
				if (propertyName.contains(MagicInfoConstant.HASH)) {
					propertyListed = historicalProperties.contains(propertyName.split(MagicInfoConstant.HASH)[1]);
				} else {
					propertyListed = historicalProperties.contains(propertyName);
				}
			}
			if (propertyListed && StringUtils.isNotNullOrEmpty(propertyValue)) {
				dynamics.put(groupName + propertyName, propertyValue);
			} else {
				stats.put(groupName + propertyName, getDefaultValueForNullData(propertyValue));
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
	 * Creates an array of formatted numbers within the specified range.
	 *
	 * This method generates an array of strings representing numbers within the given range [min, max].
	 * The numbers in the range are formatted using the specified format defined by `SolsticeConstant.NUMBER_FORMAT`.
	 *
	 * @param min The minimum value of the range (inclusive).
	 * @param max The maximum value of the range (inclusive).
	 * @return An array of strings containing formatted numbers within the specified range.
	 */
	private String[] createArrayNumber(int min, int max) {
		return IntStream.rangeClosed(min, max).mapToObj(minute -> String.format("%02d", minute)).toArray(String[]::new);
	}

	/**
	 * Converts a 12-hour formatted time string to a 24-hour formatted time string.
	 *
	 * @param time12h The input time string in 12-hour format (e.g., "12:02AM" or "12:02PM").
	 * @return The converted time string in 24-hour format (e.g., "00:02" or "12:02").
	 */
	private String convert12HourTo24Hour(String time12h) {
		try {
			String[] parts = time12h.split(MagicInfoConstant.COLON);
			if (parts.length == 2) {
				int hour = Integer.parseInt(parts[0]);
				String minutesAndAMPM = parts[1];

				if (minutesAndAMPM.endsWith("AM")) {
					if (hour == 12) {
						hour = 0;
					}
				} else {
					if (hour != 12) {
						hour += 12;
					}
				}
				String minutes = minutesAndAMPM.substring(0, 2);
				return String.format("%02d:%s", hour, minutes);
			}
			return MagicInfoConstant.NONE;
		} catch (Exception e) {
			return MagicInfoConstant.NONE;
		}
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

	/**
	 * Converts a switch status value to an integer representation.
	 *
	 * @param value The switch status value, which can be "1" (ON), "0" (OFF), or any other value.
	 * @return An integer representation of the switch status: 1 for ON, 0 for OFF, or -1 for any other value.
	 */
	private int getSwitchStatus(String value) {
		if (MagicInfoConstant.NUMBER_ONE.equals(value)) {
			return 1;
		}
		if (MagicInfoConstant.ZERO.equals(value)) {
			return 0;
		}
		return -1;
	}

	/**
	 * Add advancedControllableProperties if advancedControllableProperties different empty
	 *
	 * @param advancedControllableProperties advancedControllableProperties is the list that store all controllable properties
	 * @param stats store all statistics
	 * @param property the property is item advancedControllableProperties
	 * @return String response
	 * @throws IllegalStateException when exception occur
	 */
	private void addAdvanceControlProperties(List<AdvancedControllableProperty> advancedControllableProperties, Map<String, String> stats, AdvancedControllableProperty property, String value) {
		if (property != null) {
			for (AdvancedControllableProperty controllableProperty : advancedControllableProperties) {
				if (controllableProperty.getName().equals(property.getName())) {
					advancedControllableProperties.remove(controllableProperty);
					break;
				}
			}
			if (StringUtils.isNotNullOrEmpty(value)) {
				stats.put(property.getName(), value);
			} else {
				stats.put(property.getName(), MagicInfoConstant.EMPTY);
			}
			advancedControllableProperties.add(property);
		}
	}

	/**
	 * Create text is control property for metric
	 *
	 * @param name the name of the property
	 * @param stringValue character string
	 * @return AdvancedControllableProperty Text instance
	 */
	private AdvancedControllableProperty createText(String name, String stringValue) {
		AdvancedControllableProperty.Text text = new AdvancedControllableProperty.Text();
		return new AdvancedControllableProperty(name, new Date(), text, stringValue);
	}

	/**
	 * Create switch is control property for metric
	 *
	 * @param name the name of property
	 * @param status initial status (0|1)
	 * @return AdvancedControllableProperty switch instance
	 */
	private AdvancedControllableProperty createSwitch(String name, int status, String labelOff, String labelOn) {
		AdvancedControllableProperty.Switch toggle = new AdvancedControllableProperty.Switch();
		toggle.setLabelOff(labelOff);
		toggle.setLabelOn(labelOn);

		AdvancedControllableProperty advancedControllableProperty = new AdvancedControllableProperty();
		advancedControllableProperty.setName(name);
		advancedControllableProperty.setValue(status);
		advancedControllableProperty.setType(toggle);
		advancedControllableProperty.setTimestamp(new Date());

		return advancedControllableProperty;
	}

	/**
	 * Create numeric is control property for metric
	 *
	 * @param name the name of the property
	 * @param stringValue character string
	 * @return AdvancedControllableProperty Text instance
	 */
	private AdvancedControllableProperty createNumeric(String name, String stringValue) {
		AdvancedControllableProperty.Numeric text = new AdvancedControllableProperty.Numeric();
		return new AdvancedControllableProperty(name, new Date(), text, stringValue);
	}

	/**
	 * Create a button.
	 *
	 * @param name name of the button
	 * @param label label of the button
	 * @param labelPressed label of the button after pressing it
	 * @param gracePeriod grace period of button
	 * @return This returns the instance of {@link AdvancedControllableProperty} type Button.
	 */
	private AdvancedControllableProperty createButton(String name, String label, String labelPressed, long gracePeriod) {
		AdvancedControllableProperty.Button button = new AdvancedControllableProperty.Button();
		button.setLabel(label);
		button.setLabelPressed(labelPressed);
		button.setGracePeriod(gracePeriod);
		return new AdvancedControllableProperty(name, new Date(), button, "");
	}

	/***
	 * Create dropdown advanced controllable property
	 *
	 * @param name the name of the control
	 * @param initialValue initial value of the control
	 * @return AdvancedControllableProperty dropdown instance
	 */
	private AdvancedControllableProperty createDropdown(String name, String[] values, String initialValue) {
		AdvancedControllableProperty.DropDown dropDown = new AdvancedControllableProperty.DropDown();
		dropDown.setOptions(values);
		dropDown.setLabels(values);

		return new AdvancedControllableProperty(name, new Date(), dropDown, initialValue);
	}
}
