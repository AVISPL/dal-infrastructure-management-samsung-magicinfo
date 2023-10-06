/*
 *  Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.samsung.magicinfo;

import static com.avispl.symphony.dal.infrastructure.management.samsung.magicinfo.common.DisplayInfo.*;

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
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import javax.security.auth.login.FailedLoginException;

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
import com.avispl.symphony.dal.infrastructure.management.samsung.magicinfo.common.SystemInfo;
import com.avispl.symphony.dal.infrastructure.management.samsung.magicinfo.common.filter.FunctionFilterEnum;
import com.avispl.symphony.dal.infrastructure.management.samsung.magicinfo.common.general.SourceEnum;
import com.avispl.symphony.dal.infrastructure.management.samsung.magicinfo.common.general.WebBrowserIntervalEnum;
import com.avispl.symphony.dal.infrastructure.management.samsung.magicinfo.common.general.WebBrowserZoomEnum;
import com.avispl.symphony.dal.infrastructure.management.samsung.magicinfo.common.picture.ColorTemperatureEnum;
import com.avispl.symphony.dal.infrastructure.management.samsung.magicinfo.common.picture.ColorToneEnum;
import com.avispl.symphony.dal.infrastructure.management.samsung.magicinfo.common.picture.DigitalCleanViewEnum;
import com.avispl.symphony.dal.infrastructure.management.samsung.magicinfo.common.picture.FilmModeEnum;
import com.avispl.symphony.dal.infrastructure.management.samsung.magicinfo.common.picture.HDMIBlackLevelEnum;
import com.avispl.symphony.dal.infrastructure.management.samsung.magicinfo.common.picture.PictureSizeEnum;
import com.avispl.symphony.dal.infrastructure.management.samsung.magicinfo.common.screen.ImmediateDisplayEnum;
import com.avispl.symphony.dal.infrastructure.management.samsung.magicinfo.common.screen.IntervalModeEnum;
import com.avispl.symphony.dal.infrastructure.management.samsung.magicinfo.common.screen.RepeatModeEnum;
import com.avispl.symphony.dal.infrastructure.management.samsung.magicinfo.common.screen.TimerEnum;
import com.avispl.symphony.dal.infrastructure.management.samsung.magicinfo.common.sound.SoundModeEnum;
import com.avispl.symphony.dal.infrastructure.management.samsung.magicinfo.dto.AutoSourceSwitching;
import com.avispl.symphony.dal.infrastructure.management.samsung.magicinfo.dto.IntervalTimer;
import com.avispl.symphony.dal.infrastructure.management.samsung.magicinfo.dto.Maintenance;
import com.avispl.symphony.dal.infrastructure.management.samsung.magicinfo.dto.PixelShift;
import com.avispl.symphony.dal.infrastructure.management.samsung.magicinfo.dto.RepeatTimer;
import com.avispl.symphony.dal.infrastructure.management.samsung.magicinfo.dto.WebBrowserUrl;
import com.avispl.symphony.dal.infrastructure.management.samsung.magicinfo.statistics.DynamicStatisticsDefinition;
import com.avispl.symphony.dal.util.StringUtils;

/**
 * MagicInfoCommunicator
 * Supported features are:
 * Monitoring Aggregator Device:
 *  <ul>
 *  <li> - DevicesConnected</li>
 *  <li> - DevicesDisconnected</li>
 *  <li> - DevicesInError</li>
 *  <li> - DevicesWithWarnings</li>
 *  <ul>
 *
 * General Info Aggregated Device:
 * <ul>
 * <li> - ApprovalDate</li>
 * <li> - AvailableCapacity</li>
 * <li> - Code</li>
 * <li> - CPU</li>
 * <li> - deviceId</li>
 * <li> - deviceModel</li>
 * <li> - deviceName</li>
 * <li> - deviceOnline</li>
 * <li> - DeviceType</li>
 * <li> - DeviceTypeVersion</li>
 * <li> - DiskSpaceUsage</li>
 * <li> - FirmwareVersion</li>
 * <li> - IP</li>
 * <li> - LastConnectionTime</li>
 * <li> - Location</li>
 * <li> - MACAddress</li>
 * <li> - MapLocation</li>
 * <li> - MemorySize</li>
 * <li> - OSImageVersion</li>
 * <li> - PanelOnTime</li>
 * <li> - PlayerVersion</li>
 * <li> - Resolution</li>
 * <li> - ScreenSize</li>
 * <li> - SerialKey</li>
 * <li> - StorageSize</li>
 * <li> - Temperature</li>
 * <li> - VideoCard</li>
 * <li> - VideoDriver</li>
 * </ul>
 *
 * AdvancedSetting Group:
 * <ul>
 * <li> - AutoPowerOn</li>
 * <li> - AutoSourceSwitching</li>
 * <li> - MaxPowerSaving</li>
 * <li> - PictureEnhancer</li>
 * <li> - PrimarySource</li>
 * <li> - RemoteConfiguration</li>
 * <li> - RestorePrimarySource</li>
 * <li> - SecondarySource</li>
 * </ul>
 *
 * DisplayControls Group:
 * <ul>
 * <li> - DisplayPanel</li>
 * <li> - Mute</li>
 * <li> - Power</li>
 * <li> - Restart</li>
 * <li> - Source</li>
 * <li> - Volume</li>
 * <li> - WebBrowserHomePage</li>
 * <li> - WebBrowserRefreshInterval</li>
 * <li> - WebBrowserZoom</li>
 * </ul>
 *
 * FanAndTemperature Group:
 * <ul>
 * <li> - TemperatureControl(C)</li>
 * </ul>
 *
 * Maintenance Group:
 * <ul>
 * <li> - MaxTime</li>
 * <li> - MaxValue</li>
 * <li> - MinTime</li>
 * <li> - MinValue</li>
 * <li> - ScreenLampSchedule</li>
 * </ul>
 *
 * Picture(VIDEO) Group:
 * <ul>
 * <li> - Brightness</li>
 * <li> - Color</li>
 * <li> - ColorTemperature</li>
 * <li> - ColorTone</li>
 * <li> - Contrast</li>
 * <li> - DigitalCleanView</li>
 * <li> - FilmMode</li>
 * <li> - HDMIBlackLevel</li>
 * <li> - LampControl</li>
 * <li> - PictureSize</li>
 * <li> - ResetPicture</li>
 * <li> - Sharpness</li>
 * <li> - Tint(G/R)</li>
 * </ul>
 *
 * Maintenance Group:
 * <ul>
 * <li> - ImmediateDisplay</li>
 * <li> - PixelShift</li>
 * <li> - PixelShiftHorizontal</li>
 * <li> - PixelShiftTime</li>
 * <li> - PixelShiftVertical</li>
 * <li> - Timer</li>
 * </ul>
 *
 * FanAndTemperature Group:
 * <ul>
 * <li> - Mode</li>
 * <li> - ResetSound</li>
 * </ul>
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

				// next line will determine whether MagicInfo monitoring was paused
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
	 * A private field that represents an instance of the MagicInfoDataLoader class, which is responsible for loading device data for MagicInfo
	 */
	private MagicInfoDataLoader deviceDataLoader;

	/**
	 * An instance of the AggregatedDeviceProcessor class used to process and aggregate device-related data.
	 */
	private AggregatedDeviceProcessor aggregatedDeviceProcessor;

	/**
	 * List of aggregated device
	 */
	private List<AggregatedDevice> cachedAggregatedDeviceList = Collections.synchronizedList(new ArrayList<>());

	/**
	 * List of aggregated device
	 */
	private List<AggregatedDevice> aggregatedDeviceList = Collections.synchronizedList(new ArrayList<>());

	/**
	 * check control
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
	 * A filter for source.
	 */
	private String filterFunction;

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
	 * Retrieves {@link #filterFunction}
	 *
	 * @return value of {@link #filterFunction}
	 */
	public String getFilterFunction() {
		return filterFunction;
	}

	/**
	 * Sets {@link #filterFunction} value
	 *
	 * @param filterFunction new value of {@link #filterFunction}
	 */
	public void setFilterFunction(String filterFunction) {
		this.filterFunction = filterFunction;
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
				throw new FailedLoginException("API Token cannot be null or empty, please enter valid password and username field.");
			}
			Map<String, String> statistics = new HashMap<>();
			ExtendedStatistics extendedStatistics = new ExtendedStatistics();
			retrieveSystemInfo();
			filterDevice();
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
		String property = controllableProperty.getProperty();
		String deviceId = controllableProperty.getDeviceId();
		String value = String.valueOf(controllableProperty.getValue());
		String requestValue;

		String[] propertyList = property.split(MagicInfoConstant.HASH);
		String propertyName = property;
		if (property.contains(MagicInfoConstant.HASH)) {
			propertyName = propertyList[1];
		}

		reentrantLock.lock();
		try {
			Optional<AggregatedDevice> aggregatedDevice = aggregatedDeviceList.stream().filter(item -> item.getDeviceId().equals(deviceId)).findFirst();
			if (aggregatedDevice.isPresent()) {
				Map<String, String> stats = aggregatedDevice.get().getProperties();
				List<AdvancedControllableProperty> advancedControllableProperties = aggregatedDevice.get().getControllableProperties();
				boolean controlPropagated = true;

				DisplayInfo propertyItem = getByName(propertyName);
				JsonNode cachedValue = null;
				if (propertyItem.isObject()) {
					cachedValue = getDisplayControlsInfo(deviceId);
				}
				switch (propertyItem) {
					case POWER:
						sendPowerCommand(deviceId, value);
						break;
					case RESTART:
						sendRestartCommand(deviceId);
						break;
					case RESET_SOUND:
						sendResetControl(propertyItem, deviceId, "1");
						cachedValue = getDisplayControlsInfo(deviceId);
						String soundMode = cachedValue.get(SOUND_MODE.getFieldName()).asText();
						//populate SOUND_MODE control
						addAdvanceControlProperties(advancedControllableProperties, stats,
								createDropdown(MagicInfoConstant.SOUND.concat(SOUND_MODE.getName()), EnumTypeHandler.getEnumNames(SoundModeEnum.class),
										EnumTypeHandler.getNameByValue(SoundModeEnum.class, soundMode)), soundMode);
						break;
					case RESET_PICTURE:
						sendResetControl(propertyItem, deviceId, "0");
						cachedValue = getDisplayControlsInfo(deviceId);
						String lampControl = cachedValue.get(LAMP_CONTROL.getFieldName()).asText();
						String contrast = cachedValue.get(CONTRAST.getFieldName()).asText();
						String brightness = cachedValue.get(BRIGHTNESS.getFieldName()).asText();
						String sharpness = cachedValue.get(SHARPNESS.getFieldName()).asText();
						String color = cachedValue.get(COLOR.getFieldName()).asText();
						String tint = cachedValue.get(TINT.getFieldName()).asText();
						String colorTone = cachedValue.get(COLOR_TONE.getFieldName()).asText();
						String colorTemperature = cachedValue.get(COLOR_TEMPERATURE.getFieldName()).asText();
						String pictureSize = cachedValue.get(PICTURE_SIZE.getFieldName()).asText();
						String digitalCleanView = cachedValue.get(DIGITAL_CLEAN_VIEW.getFieldName()).asText();
						String filmMode = cachedValue.get(FILM_MODE.getFieldName()).asText();
						String hdmiBlackLevel = cachedValue.get(HDMI_BLACK_LEVEL.getFieldName()).asText();
						//populate controlling property in PICTURE group
						addAdvanceControlProperties(advancedControllableProperties, stats, createNumeric(MagicInfoConstant.PICTURE_VIDEO.concat(LAMP_CONTROL.getName()), lampControl), lampControl);
						addAdvanceControlProperties(advancedControllableProperties, stats, createNumeric(MagicInfoConstant.PICTURE_VIDEO.concat(CONTRAST.getName()), contrast), contrast);
						addAdvanceControlProperties(advancedControllableProperties, stats, createNumeric(MagicInfoConstant.PICTURE_VIDEO.concat(BRIGHTNESS.getName()), brightness), brightness);
						addAdvanceControlProperties(advancedControllableProperties, stats, createNumeric(MagicInfoConstant.PICTURE_VIDEO.concat(SHARPNESS.getName()), sharpness), sharpness);
						addAdvanceControlProperties(advancedControllableProperties, stats, createNumeric(MagicInfoConstant.PICTURE_VIDEO.concat(COLOR.getName()), color), color);
						addAdvanceControlProperties(advancedControllableProperties, stats, createNumeric(MagicInfoConstant.PICTURE_VIDEO.concat(TINT.getName()), tint), tint);
						addAdvanceControlProperties(advancedControllableProperties, stats,
								createDropdown(MagicInfoConstant.PICTURE_VIDEO.concat(COLOR_TONE.getName()), EnumTypeHandler.getEnumNames(ColorToneEnum.class),
										EnumTypeHandler.getNameByValue(ColorToneEnum.class, colorTone)), colorTone);

						stats.put(MagicInfoConstant.PICTURE_VIDEO.concat(COLOR_TEMPERATURE.getName()), EnumTypeHandler.getNameByValue(ColorTemperatureEnum.class, colorTemperature));
						stats.put(MagicInfoConstant.PICTURE_VIDEO.concat(PICTURE_SIZE.getName()), EnumTypeHandler.getNameByValue(PictureSizeEnum.class, pictureSize));
						stats.put(MagicInfoConstant.PICTURE_VIDEO.concat(DIGITAL_CLEAN_VIEW.getName()), EnumTypeHandler.getNameByValue(DigitalCleanViewEnum.class, digitalCleanView));
						stats.put(MagicInfoConstant.PICTURE_VIDEO.concat(FILM_MODE.getName()), EnumTypeHandler.getNameByValue(FilmModeEnum.class, filmMode));
						stats.put(MagicInfoConstant.PICTURE_VIDEO.concat(HDMI_BLACK_LEVEL.getName()), EnumTypeHandler.getNameByValue(HDMIBlackLevelEnum.class, hdmiBlackLevel));
						break;
					case VOLUME:
					case LAMP_CONTROL:
					case BRIGHTNESS:
					case CONTRAST:
					case SHARPNESS:
					case COLOR:
					case TINT:
						value = checkValidInput(0, 100, value);
						sendControlRequest(propertyItem, deviceId, value);
						break;
					case TEMPERATURE_CONTROL:
						value = checkValidInput(75, 124, value);
						sendControlRequest(propertyItem, deviceId, value);
						break;
					case COLOR_TONE:
						requestValue = EnumTypeHandler.getValueByName(ColorToneEnum.class, value);
						sendControlRequest(propertyItem, deviceId, requestValue);
						break;
					case COLOR_TEMPERATURE:
						requestValue = EnumTypeHandler.getValueByName(ColorTemperatureEnum.class, value);
						sendControlRequest(propertyItem, deviceId, requestValue);
						break;
					case PICTURE_SIZE:
						requestValue = EnumTypeHandler.getValueByName(PictureSizeEnum.class, value);
						sendControlRequest(propertyItem, deviceId, requestValue);
						break;
					case DIGITAL_CLEAN_VIEW:
						requestValue = EnumTypeHandler.getValueByName(DigitalCleanViewEnum.class, value);
						sendControlRequest(propertyItem, deviceId, requestValue);
						break;
					case FILM_MODE:
						requestValue = EnumTypeHandler.getValueByName(FilmModeEnum.class, value);
						sendControlRequest(propertyItem, deviceId, requestValue);
						break;
					case HDMI_BLACK_LEVEL:
						requestValue = EnumTypeHandler.getValueByName(HDMIBlackLevelEnum.class, value);
						sendControlRequest(propertyItem, deviceId, requestValue);
						break;
					case SOUND_MODE:
						requestValue = EnumTypeHandler.getValueByName(SoundModeEnum.class, value);
						sendControlRequest(propertyItem, deviceId, requestValue);
						break;
					case MUTE:
					case PICTURE_ENHANCER:
					case REMOTE_CONFIGURATION:
					case MAX_POWER_SAVING:
					case AUTO_POWER_ON:
						sendControlRequest(propertyItem, deviceId, value);
						break;
					case DISPLAY_PANEL:
						requestValue = MagicInfoConstant.NUMBER_ONE.equals(value) ? MagicInfoConstant.ZERO : MagicInfoConstant.NUMBER_ONE;
						sendControlRequest(propertyItem, deviceId, requestValue);
						break;
					case WEB_BROWSER_ZOOM:
						String webBrowserZoom;
						String webBrowserInterval;
						String webBrowserHomepage;
						String webBrowserPageUrl;
						WebBrowserUrl webBrowserUrl;
						if (cachedValue != null && cachedValue.has(MagicInfoConstant.WEB_BROWSER_URL) && checkChildNodeWebBrowser(cachedValue.get(MagicInfoConstant.WEB_BROWSER_URL))) {
							webBrowserInterval = cachedValue.get(MagicInfoConstant.WEB_BROWSER_URL).get(WEB_BROWSER_INTERVAL.getFieldName()).asText();
							webBrowserHomepage = cachedValue.get(MagicInfoConstant.WEB_BROWSER_URL).get(WEB_BROWSER_HOME_PAGE.getFieldName()).asText();
							webBrowserPageUrl = cachedValue.get(MagicInfoConstant.WEB_BROWSER_URL).get(WEB_BROWSER_PAGE_URL.getFieldName()).asText();
							requestValue = EnumTypeHandler.getValueByName(WebBrowserZoomEnum.class, value);
							webBrowserUrl = new WebBrowserUrl(false, false, false, false, webBrowserInterval, requestValue, webBrowserHomepage, webBrowserPageUrl, true);
							sendGroupControl(propertyItem, deviceId, requestValue, MagicInfoConstant.WEB_BROWSER_URL, webBrowserUrl);
						}
						break;
					case WEB_BROWSER_INTERVAL:
						if (cachedValue != null && cachedValue.has(MagicInfoConstant.WEB_BROWSER_URL) && checkChildNodeWebBrowser(cachedValue.get(MagicInfoConstant.WEB_BROWSER_URL))) {
							webBrowserZoom = cachedValue.get(MagicInfoConstant.WEB_BROWSER_URL).get(WEB_BROWSER_ZOOM.getFieldName()).asText();
							webBrowserHomepage = cachedValue.get(MagicInfoConstant.WEB_BROWSER_URL).get(WEB_BROWSER_HOME_PAGE.getFieldName()).asText();
							webBrowserPageUrl = cachedValue.get(MagicInfoConstant.WEB_BROWSER_URL).get(WEB_BROWSER_PAGE_URL.getFieldName()).asText();
							requestValue = EnumTypeHandler.getValueByName(WebBrowserIntervalEnum.class, value);
							webBrowserUrl = new WebBrowserUrl(false, false, false, false, requestValue, webBrowserZoom, webBrowserHomepage, webBrowserPageUrl, true);
							sendGroupControl(propertyItem, deviceId, requestValue, MagicInfoConstant.WEB_BROWSER_URL, webBrowserUrl);
						}
						break;
					case WEB_BROWSER_PAGE_URL:
						if (cachedValue != null && cachedValue.has(MagicInfoConstant.WEB_BROWSER_URL) && checkChildNodeWebBrowser(cachedValue.get(MagicInfoConstant.WEB_BROWSER_URL))) {
							webBrowserInterval = cachedValue.get(MagicInfoConstant.WEB_BROWSER_URL).get(WEB_BROWSER_INTERVAL.getFieldName()).asText();
							webBrowserZoom = cachedValue.get(MagicInfoConstant.WEB_BROWSER_URL).get(WEB_BROWSER_ZOOM.getFieldName()).asText();
							webBrowserHomepage = cachedValue.get(MagicInfoConstant.WEB_BROWSER_URL).get(WEB_BROWSER_HOME_PAGE.getFieldName()).asText();
							webBrowserUrl = new WebBrowserUrl(false, false, false, false, webBrowserInterval, webBrowserZoom, webBrowserHomepage, value, true);
							sendGroupControl(propertyItem, deviceId, value, MagicInfoConstant.WEB_BROWSER_URL, webBrowserUrl);
						}
						break;
					case WEB_BROWSER_HOME_PAGE:
						if (cachedValue != null && cachedValue.has(MagicInfoConstant.WEB_BROWSER_URL) && checkChildNodeWebBrowser(cachedValue.get(MagicInfoConstant.WEB_BROWSER_URL))) {
							webBrowserInterval = cachedValue.get(MagicInfoConstant.WEB_BROWSER_URL).get(WEB_BROWSER_INTERVAL.getFieldName()).asText();
							webBrowserZoom = cachedValue.get(MagicInfoConstant.WEB_BROWSER_URL).get(WEB_BROWSER_ZOOM.getFieldName()).asText();
							webBrowserPageUrl = cachedValue.get(MagicInfoConstant.WEB_BROWSER_URL).get(WEB_BROWSER_PAGE_URL.getFieldName()).asText();
							webBrowserUrl = new WebBrowserUrl(false, false, false, false, webBrowserInterval, webBrowserZoom, value, webBrowserPageUrl, true);
							sendGroupControl(propertyItem, deviceId, value, MagicInfoConstant.WEB_BROWSER_URL, webBrowserUrl);
							if (MagicInfoConstant.ZERO.equals(value)) {
								removeValueForTheControllableProperty(stats, advancedControllableProperties, MagicInfoConstant.DISPLAY_CONTROLS_GROUP.concat(WEB_BROWSER_PAGE_URL.getName()));
							} else {
								addValueForTheControllableProperty(stats, advancedControllableProperties,
										createText(MagicInfoConstant.DISPLAY_CONTROLS_GROUP.concat(WEB_BROWSER_PAGE_URL.getName()), webBrowserPageUrl),
										webBrowserPageUrl);
							}
						}
						break;
					case SCREEN_LAMP_SCHEDULE:
						String maxTime;
						String minTime;
						String maxValue;
						String minValue;
						Maintenance maintenance;
						String mntAutoIsEnable;
						if (cachedValue != null && cachedValue.has(MagicInfoConstant.MNT_AUTO) && checkChildNodeMaintenance(cachedValue.get(MagicInfoConstant.MNT_AUTO))) {
							maxTime = cachedValue.get(MagicInfoConstant.MNT_AUTO).get(MAX_TIME_HOUR.getFieldName()).asText();
							minTime = cachedValue.get(MagicInfoConstant.MNT_AUTO).get(MIN_TIME_HOUR.getFieldName()).asText();
							maxValue = cachedValue.get(MagicInfoConstant.MNT_AUTO).get(MAX_VALUE.getFieldName()).asText();
							minValue = cachedValue.get(MagicInfoConstant.MNT_AUTO).get(MIN_VALUE.getFieldName()).asText();
							maintenance = new Maintenance(true, false, false, false, false, value, maxTime, maxValue, minTime, minValue);
							sendGroupControl(propertyItem, deviceId, value, MagicInfoConstant.MNT_AUTO, maintenance);
							if (MagicInfoConstant.NUMBER_ONE.equals(value)) {
								//turn on
								stats.remove(MagicInfoConstant.MAINTENANCE_GROUP.concat(MagicInfoConstant.MAX_TIME));
								stats.remove(MagicInfoConstant.MAINTENANCE_GROUP.concat(MagicInfoConstant.MIN_TIME));
								stats.remove(MagicInfoConstant.MAINTENANCE_GROUP.concat(MAX_VALUE.getName()));
								stats.remove(MagicInfoConstant.MAINTENANCE_GROUP.concat(MIN_VALUE.getName()));
								String hour = convert12HourTo24Hour(maxTime).split(MagicInfoConstant.COLON)[0];
								String minute = convert12HourTo24Hour(maxTime).split(MagicInfoConstant.COLON)[1];
								addAdvanceControlProperties(advancedControllableProperties, stats, createDropdown(MagicInfoConstant.MAINTENANCE_GROUP.concat(MAX_TIME_HOUR.getName()), createArrayNumber(0, 23), hour),
										hour);
								addAdvanceControlProperties(advancedControllableProperties, stats,
										createDropdown(MagicInfoConstant.MAINTENANCE_GROUP.concat(MAX_TIME_MINUTE.getName()), createArrayNumber(0, 59), minute), minute);

								hour = convert12HourTo24Hour(minTime).split(MagicInfoConstant.COLON)[0];
								minute = convert12HourTo24Hour(minTime).split(MagicInfoConstant.COLON)[1];
								addAdvanceControlProperties(advancedControllableProperties, stats, createDropdown(MagicInfoConstant.MAINTENANCE_GROUP.concat(MIN_TIME_HOUR.getName()), createArrayNumber(0, 23), hour),
										hour);
								addAdvanceControlProperties(advancedControllableProperties, stats,
										createDropdown(MagicInfoConstant.MAINTENANCE_GROUP.concat(MIN_TIME_MINUTE.getName()), createArrayNumber(0, 59), minute), minute);
								addAdvanceControlProperties(advancedControllableProperties, stats, createNumeric(MagicInfoConstant.MAINTENANCE_GROUP.concat(MAX_VALUE.getName()), maxValue), maxValue);
								addAdvanceControlProperties(advancedControllableProperties, stats, createNumeric(MagicInfoConstant.MAINTENANCE_GROUP.concat(MIN_VALUE.getName()), minValue), minValue);
							} else {
								//turn off
								removeValueForTheControllableProperty(stats, advancedControllableProperties, MagicInfoConstant.MAINTENANCE_GROUP.concat(MAX_TIME_HOUR.getName()));
								removeValueForTheControllableProperty(stats, advancedControllableProperties, MagicInfoConstant.MAINTENANCE_GROUP.concat(MAX_TIME_MINUTE.getName()));
								removeValueForTheControllableProperty(stats, advancedControllableProperties, MagicInfoConstant.MAINTENANCE_GROUP.concat(MIN_TIME_HOUR.getName()));
								removeValueForTheControllableProperty(stats, advancedControllableProperties, MagicInfoConstant.MAINTENANCE_GROUP.concat(MIN_TIME_MINUTE.getName()));
								removeValueForTheControllableProperty(stats, advancedControllableProperties, MagicInfoConstant.MAINTENANCE_GROUP.concat(MAX_VALUE.getName()));
								removeValueForTheControllableProperty(stats, advancedControllableProperties, MagicInfoConstant.MAINTENANCE_GROUP.concat(MIN_VALUE.getName()));
								stats.put(MagicInfoConstant.MAINTENANCE_GROUP.concat(MagicInfoConstant.MAX_TIME), maxTime);
								stats.put(MagicInfoConstant.MAINTENANCE_GROUP.concat(MagicInfoConstant.MIN_TIME), minTime);
								stats.put(MagicInfoConstant.MAINTENANCE_GROUP.concat(MAX_VALUE.getName()), maxValue);
								stats.put(MagicInfoConstant.MAINTENANCE_GROUP.concat(MIN_VALUE.getName()), minValue);
							}
						}
						break;
					case MAX_VALUE:
						if (cachedValue != null && cachedValue.has(MagicInfoConstant.MNT_AUTO) && checkChildNodeMaintenance(cachedValue.get(MagicInfoConstant.MNT_AUTO))) {
							maxTime = cachedValue.get(MagicInfoConstant.MNT_AUTO).get(MAX_TIME_HOUR.getFieldName()).asText();
							minTime = cachedValue.get(MagicInfoConstant.MNT_AUTO).get(MIN_TIME_HOUR.getFieldName()).asText();
							mntAutoIsEnable = cachedValue.get(MagicInfoConstant.MNT_AUTO).get(SCREEN_LAMP_SCHEDULE.getFieldName()).asText();
							minValue = cachedValue.get(MagicInfoConstant.MNT_AUTO).get(MIN_VALUE.getFieldName()).asText();
							value = checkValidInput(0, 100, value);
							maintenance = new Maintenance(true, false, false, false, false, mntAutoIsEnable, maxTime, value, minTime, minValue);
							sendGroupControl(propertyItem, deviceId, value, MagicInfoConstant.MNT_AUTO, maintenance);
						}
						break;
					case MIN_VALUE:
						if (cachedValue != null && cachedValue.has(MagicInfoConstant.MNT_AUTO) && checkChildNodeMaintenance(cachedValue.get(MagicInfoConstant.MNT_AUTO))) {
							maxTime = cachedValue.get(MagicInfoConstant.MNT_AUTO).get(MAX_TIME_HOUR.getFieldName()).asText();
							minTime = cachedValue.get(MagicInfoConstant.MNT_AUTO).get(MIN_TIME_HOUR.getFieldName()).asText();
							mntAutoIsEnable = cachedValue.get(MagicInfoConstant.MNT_AUTO).get(SCREEN_LAMP_SCHEDULE.getFieldName()).asText();
							maxValue = cachedValue.get(MagicInfoConstant.MNT_AUTO).get(MAX_VALUE.getFieldName()).asText();
							value = checkValidInput(0, 100, value);
							maintenance = new Maintenance(true, false, false, false, false, mntAutoIsEnable, maxTime, maxValue, minTime, value);
							sendGroupControl(propertyItem, deviceId, value, MagicInfoConstant.MNT_AUTO, maintenance);
						}
						break;
					case MAX_TIME_MINUTE:
						if (cachedValue != null && cachedValue.has(MagicInfoConstant.MNT_AUTO) && checkChildNodeMaintenance(cachedValue.get(MagicInfoConstant.MNT_AUTO))) {
							maxTime = cachedValue.get(MagicInfoConstant.MNT_AUTO).get(MAX_TIME_HOUR.getFieldName()).asText();
							minTime = cachedValue.get(MagicInfoConstant.MNT_AUTO).get(MIN_TIME_HOUR.getFieldName()).asText();
							maxValue = cachedValue.get(MagicInfoConstant.MNT_AUTO).get(MAX_VALUE.getFieldName()).asText();
							minValue = cachedValue.get(MagicInfoConstant.MNT_AUTO).get(MIN_VALUE.getFieldName()).asText();
							mntAutoIsEnable = cachedValue.get(MagicInfoConstant.MNT_AUTO).get(SCREEN_LAMP_SCHEDULE.getFieldName()).asText();
							String hour = convert12HourTo24Hour(maxTime).split(MagicInfoConstant.COLON)[0];
							requestValue = convertTo12HourFormat(hour, value);
							maintenance = new Maintenance(true, false, false, false, false, mntAutoIsEnable, requestValue, maxValue, minTime, minValue);
							sendGroupControl(propertyItem, deviceId, requestValue, MagicInfoConstant.MNT_AUTO, maintenance);
						}
						break;
					case MAX_TIME_HOUR:
						if (cachedValue != null && cachedValue.has(MagicInfoConstant.MNT_AUTO) && checkChildNodeMaintenance(cachedValue.get(MagicInfoConstant.MNT_AUTO))) {
							maxTime = cachedValue.get(MagicInfoConstant.MNT_AUTO).get(MAX_TIME_HOUR.getFieldName()).asText();
							minTime = cachedValue.get(MagicInfoConstant.MNT_AUTO).get(MIN_TIME_HOUR.getFieldName()).asText();
							maxValue = cachedValue.get(MagicInfoConstant.MNT_AUTO).get(MAX_VALUE.getFieldName()).asText();
							minValue = cachedValue.get(MagicInfoConstant.MNT_AUTO).get(MIN_VALUE.getFieldName()).asText();
							mntAutoIsEnable = cachedValue.get(MagicInfoConstant.MNT_AUTO).get(SCREEN_LAMP_SCHEDULE.getFieldName()).asText();
							String minute = convert12HourTo24Hour(maxTime).split(MagicInfoConstant.COLON)[1];
							requestValue = convertTo12HourFormat(value, minute);
							maintenance = new Maintenance(true, false, false, false, false, mntAutoIsEnable, requestValue, maxValue, minTime, minValue);
							sendGroupControl(propertyItem, deviceId, requestValue, MagicInfoConstant.MNT_AUTO, maintenance);
						}
						break;
					case MIN_TIME_MINUTE:
						if (cachedValue != null && cachedValue.has(MagicInfoConstant.MNT_AUTO) && checkChildNodeMaintenance(cachedValue.get(MagicInfoConstant.MNT_AUTO))) {
							maxTime = cachedValue.get(MagicInfoConstant.MNT_AUTO).get(MAX_TIME_HOUR.getFieldName()).asText();
							minTime = cachedValue.get(MagicInfoConstant.MNT_AUTO).get(MIN_TIME_HOUR.getFieldName()).asText();
							maxValue = cachedValue.get(MagicInfoConstant.MNT_AUTO).get(MAX_VALUE.getFieldName()).asText();
							minValue = cachedValue.get(MagicInfoConstant.MNT_AUTO).get(MIN_VALUE.getFieldName()).asText();
							mntAutoIsEnable = cachedValue.get(MagicInfoConstant.MNT_AUTO).get(SCREEN_LAMP_SCHEDULE.getFieldName()).asText();
							String hour = convert12HourTo24Hour(minTime).split(MagicInfoConstant.COLON)[0];
							requestValue = convertTo12HourFormat(hour, value);
							maintenance = new Maintenance(true, false, false, false, false, mntAutoIsEnable, maxTime, maxValue, requestValue, minValue);
							sendGroupControl(propertyItem, deviceId, requestValue, MagicInfoConstant.MNT_AUTO, maintenance);
						}
						break;
					case MIN_TIME_HOUR:
						if (cachedValue != null && cachedValue.has(MagicInfoConstant.MNT_AUTO) && checkChildNodeMaintenance(cachedValue.get(MagicInfoConstant.MNT_AUTO))) {
							maxTime = cachedValue.get(MagicInfoConstant.MNT_AUTO).get(MAX_TIME_HOUR.getFieldName()).asText();
							minTime = cachedValue.get(MagicInfoConstant.MNT_AUTO).get(MIN_TIME_HOUR.getFieldName()).asText();
							maxValue = cachedValue.get(MagicInfoConstant.MNT_AUTO).get(MAX_VALUE.getFieldName()).asText();
							minValue = cachedValue.get(MagicInfoConstant.MNT_AUTO).get(MIN_VALUE.getFieldName()).asText();
							mntAutoIsEnable = cachedValue.get(MagicInfoConstant.MNT_AUTO).get(SCREEN_LAMP_SCHEDULE.getFieldName()).asText();
							String minute = convert12HourTo24Hour(minTime).split(MagicInfoConstant.COLON)[1];
							requestValue = convertTo12HourFormat(value, minute);
							maintenance = new Maintenance(true, false, false, false, false, mntAutoIsEnable, maxTime, maxValue, requestValue, minValue);
							sendGroupControl(propertyItem, deviceId, requestValue, MagicInfoConstant.MNT_AUTO, maintenance);
						}
						break;
					case AUTO_SOURCE_SWITCHING:
						if (cachedValue != null && cachedValue.has(MagicInfoConstant.AUTO_SOURCE) && checkChildNodeAutoSourceSwitching(cachedValue.get(MagicInfoConstant.AUTO_SOURCE))) {
							String restorePrimarySource = cachedValue.get(MagicInfoConstant.AUTO_SOURCE).get(RESTORE_PRIMARY_SOURCE.getFieldName()).asText();
							String primarySource = cachedValue.get(MagicInfoConstant.AUTO_SOURCE).get(PRIMARY_SOURCE.getFieldName()).asText();
							String secondSource = cachedValue.get(MagicInfoConstant.AUTO_SOURCE).get(SECONDARY_SOURCE.getFieldName()).asText();
							AutoSourceSwitching autoSourceSwitching = new AutoSourceSwitching(true, false, false, false, false, value, restorePrimarySource, primarySource, secondSource);
							sendGroupControl(propertyItem, deviceId, value, MagicInfoConstant.AUTO_SOURCE, autoSourceSwitching);
							if (!MagicInfoConstant.ZERO.equals(value)) {
								//turn on
								stats.put(MagicInfoConstant.ADVANCED_SETTING.concat(RESTORE_PRIMARY_SOURCE.getName()), MagicInfoConstant.ZERO.equals(restorePrimarySource) ? "Off" : "On");
								stats.put(MagicInfoConstant.ADVANCED_SETTING.concat(PRIMARY_SOURCE.getName()), EnumTypeHandler.getNameByValue(SourceEnum.class, primarySource));
								stats.put(MagicInfoConstant.ADVANCED_SETTING.concat(SECONDARY_SOURCE.getName()), EnumTypeHandler.getNameByValue(SourceEnum.class, secondSource));
							} else {
								//turn off
								stats.remove(MagicInfoConstant.ADVANCED_SETTING.concat(RESTORE_PRIMARY_SOURCE.getName()));
								stats.remove(MagicInfoConstant.ADVANCED_SETTING.concat(PRIMARY_SOURCE.getName()));
								stats.remove(MagicInfoConstant.ADVANCED_SETTING.concat(SECONDARY_SOURCE.getName()));
							}
						}
						break;
					case PIXEL_SHIFT:
						String pixelShiftEnable;
						String pixelShiftH;
						String pixelShiftV;
						String pixelShiftTime;
						PixelShift pixelShift;
						if (cachedValue != null && cachedValue.has(MagicInfoConstant.MNT_PIXEL_SHIFT) && checkChildNodePixelShift(cachedValue.get(MagicInfoConstant.MNT_PIXEL_SHIFT))) {
							pixelShiftH = cachedValue.get(MagicInfoConstant.MNT_PIXEL_SHIFT).get(PIXEL_SHIFT_HORIZONTAL.getFieldName()).asText();
							pixelShiftV = cachedValue.get(MagicInfoConstant.MNT_PIXEL_SHIFT).get(PIXEL_SHIFT_VERTICAL.getFieldName()).asText();
							pixelShiftTime = cachedValue.get(MagicInfoConstant.MNT_PIXEL_SHIFT).get(PIXEL_SHIFT_TIME.getFieldName()).asText();
							pixelShift = new PixelShift(true, false, false, false, false, value, pixelShiftH, pixelShiftV, pixelShiftTime);
							sendGroupControl(propertyItem, deviceId, value, MagicInfoConstant.MNT_PIXEL_SHIFT, pixelShift);
						}
						break;
					case PIXEL_SHIFT_HORIZONTAL:
						if (cachedValue != null && cachedValue.has(MagicInfoConstant.MNT_PIXEL_SHIFT) && checkChildNodePixelShift(cachedValue.get(MagicInfoConstant.MNT_PIXEL_SHIFT))) {
							value = checkValidInput(0, 4, value);
							pixelShiftEnable = cachedValue.get(MagicInfoConstant.MNT_PIXEL_SHIFT).get(PIXEL_SHIFT.getFieldName()).asText();
							pixelShiftV = cachedValue.get(MagicInfoConstant.MNT_PIXEL_SHIFT).get(PIXEL_SHIFT_VERTICAL.getFieldName()).asText();
							pixelShiftTime = cachedValue.get(MagicInfoConstant.MNT_PIXEL_SHIFT).get(PIXEL_SHIFT_TIME.getFieldName()).asText();
							pixelShift = new PixelShift(true, false, false, false, false, pixelShiftEnable, value, pixelShiftV, pixelShiftTime);
							sendGroupControl(propertyItem, deviceId, value, MagicInfoConstant.MNT_PIXEL_SHIFT, pixelShift);
						}
						break;
					case PIXEL_SHIFT_VERTICAL:
						if (cachedValue != null && cachedValue.has(MagicInfoConstant.MNT_PIXEL_SHIFT) && checkChildNodePixelShift(cachedValue.get(MagicInfoConstant.MNT_PIXEL_SHIFT))) {
							value = checkValidInput(0, 4, value);
							pixelShiftEnable = cachedValue.get(MagicInfoConstant.MNT_PIXEL_SHIFT).get(PIXEL_SHIFT.getFieldName()).asText();
							pixelShiftH = cachedValue.get(MagicInfoConstant.MNT_PIXEL_SHIFT).get(PIXEL_SHIFT_HORIZONTAL.getFieldName()).asText();
							pixelShiftTime = cachedValue.get(MagicInfoConstant.MNT_PIXEL_SHIFT).get(PIXEL_SHIFT_TIME.getFieldName()).asText();
							pixelShift = new PixelShift(true, false, false, false, false, pixelShiftEnable, pixelShiftH, value, pixelShiftTime);
							sendGroupControl(propertyItem, deviceId, value, MagicInfoConstant.MNT_PIXEL_SHIFT, pixelShift);
						}
						break;
					case PIXEL_SHIFT_TIME:
						if (cachedValue != null && cachedValue.has(MagicInfoConstant.MNT_PIXEL_SHIFT) && checkChildNodePixelShift(cachedValue.get(MagicInfoConstant.MNT_PIXEL_SHIFT))) {
							value = checkValidInput(1, 4, value);
							pixelShiftEnable = cachedValue.get(MagicInfoConstant.MNT_PIXEL_SHIFT).get(PIXEL_SHIFT.getFieldName()).asText();
							pixelShiftH = cachedValue.get(MagicInfoConstant.MNT_PIXEL_SHIFT).get(PIXEL_SHIFT_HORIZONTAL.getFieldName()).asText();
							pixelShiftV = cachedValue.get(MagicInfoConstant.MNT_PIXEL_SHIFT).get(PIXEL_SHIFT_VERTICAL.getFieldName()).asText();
							pixelShift = new PixelShift(true, false, false, false, false, pixelShiftEnable, pixelShiftH, pixelShiftV, value);
							sendGroupControl(propertyItem, deviceId, value, MagicInfoConstant.MNT_PIXEL_SHIFT, pixelShift);
						}
						break;
					case TIMER:
						if (cachedValue != null && cachedValue.has(MagicInfoConstant.MNT_SAFETY_SCREEN_TIMER) && checkChildNodeTimer(cachedValue.get(MagicInfoConstant.MNT_SAFETY_SCREEN_TIMER))) {
							requestValue = EnumTypeHandler.getValueByName(TimerEnum.class, value);
							if (MagicInfoConstant.ZERO.equals(requestValue)) {
								String currentTimerMode = cachedValue.get(MagicInfoConstant.MNT_SAFETY_SCREEN_TIMER).get(TIMER.getFieldName()).asText();
								//off mode
								Object timerObject;
								if (MagicInfoConstant.NUMBER_ONE.equals(currentTimerMode)) {
									timerObject = new RepeatTimer(true, false, false, false, false, "0", cachedValue.get(MagicInfoConstant.MNT_SAFETY_SCREEN_TIMER).get(TIMER_MODE.getFieldName()).asText(),
											cachedValue.get(MagicInfoConstant.MNT_SAFETY_SCREEN_TIMER).get(TIMER_PERIOD.getFieldName()).asText(),
											cachedValue.get(MagicInfoConstant.MNT_SAFETY_SCREEN_TIMER).get(TIMER_TIME.getFieldName()).asText());
								} else {
									timerObject = new IntervalTimer(true, false, false, false, false, "0", cachedValue.get(MagicInfoConstant.MNT_SAFETY_SCREEN_TIMER).get(TIMER_MODE.getFieldName()).asText(),
											cachedValue.get(MagicInfoConstant.MNT_SAFETY_SCREEN_TIMER).get(TIMER_START_TIME_HOUR.getFieldName()).asText(),
											cachedValue.get(MagicInfoConstant.MNT_SAFETY_SCREEN_TIMER).get(TIMER_END_TIME_HOUR.getFieldName()).asText());
								}
								sendGroupControl(propertyItem, deviceId, requestValue, MagicInfoConstant.MNT_SAFETY_SCREEN_TIMER, timerObject);

								stats.remove(MagicInfoConstant.SCREEN_BURN_PROTECTION_GROUP.concat(TIMER_MODE.getName()));
								removeValueForTheControllableProperty(stats, advancedControllableProperties, MagicInfoConstant.SCREEN_BURN_PROTECTION_GROUP.concat(TIMER_PERIOD.getName()));
								removeValueForTheControllableProperty(stats, advancedControllableProperties, MagicInfoConstant.SCREEN_BURN_PROTECTION_GROUP.concat(TIMER_TIME.getName()));
								removeValueForTheControllableProperty(stats, advancedControllableProperties, MagicInfoConstant.SCREEN_BURN_PROTECTION_GROUP.concat(TIMER_END_TIME_HOUR.getName()));
								removeValueForTheControllableProperty(stats, advancedControllableProperties, MagicInfoConstant.SCREEN_BURN_PROTECTION_GROUP.concat(TIMER_END_TIME_MIN.getName()));
								removeValueForTheControllableProperty(stats, advancedControllableProperties, MagicInfoConstant.SCREEN_BURN_PROTECTION_GROUP.concat(TIMER_START_TIME_HOUR.getName()));
								removeValueForTheControllableProperty(stats, advancedControllableProperties, MagicInfoConstant.SCREEN_BURN_PROTECTION_GROUP.concat(TIMER_START_TIME_MIN.getName()));
							} else if (MagicInfoConstant.NUMBER_ONE.equals(requestValue)) {
								String scrSafeMode = MagicInfoConstant.SAFE_REPEAT_MODE_DEFAULT;
								String scrSafePeriod = MagicInfoConstant.SAFE_PERIOD_DEFAULT;
								String scrSafeTime = MagicInfoConstant.SAFE_TIME_DEFAULT;
								Object timerObject = new RepeatTimer(true, false, false, false, false, "1", scrSafeMode, scrSafePeriod, scrSafeTime);
								sendGroupControl(propertyItem, deviceId, requestValue, MagicInfoConstant.MNT_SAFETY_SCREEN_TIMER, timerObject);

								stats.remove(MagicInfoConstant.SCREEN_BURN_PROTECTION_GROUP.concat(TIMER_MODE.getName()));
								removeValueForTheControllableProperty(stats, advancedControllableProperties, MagicInfoConstant.SCREEN_BURN_PROTECTION_GROUP.concat(TIMER_END_TIME_HOUR.getName()));
								removeValueForTheControllableProperty(stats, advancedControllableProperties, MagicInfoConstant.SCREEN_BURN_PROTECTION_GROUP.concat(TIMER_END_TIME_MIN.getName()));
								removeValueForTheControllableProperty(stats, advancedControllableProperties, MagicInfoConstant.SCREEN_BURN_PROTECTION_GROUP.concat(TIMER_START_TIME_HOUR.getName()));
								removeValueForTheControllableProperty(stats, advancedControllableProperties, MagicInfoConstant.SCREEN_BURN_PROTECTION_GROUP.concat(TIMER_START_TIME_MIN.getName()));

								stats.put(MagicInfoConstant.SCREEN_BURN_PROTECTION_GROUP.concat(TIMER_MODE.getName()), EnumTypeHandler.getNameByValue(RepeatModeEnum.class, scrSafeMode));
								addAdvanceControlProperties(advancedControllableProperties, stats, createNumeric(MagicInfoConstant.SCREEN_BURN_PROTECTION_GROUP.concat(TIMER_PERIOD.getName()), scrSafePeriod),
										scrSafePeriod);
								addAdvanceControlProperties(advancedControllableProperties, stats,
										createDropdown(MagicInfoConstant.SCREEN_BURN_PROTECTION_GROUP.concat(TIMER_TIME.getName()), MagicInfoConstant.TIMER_TIME_VALUES, scrSafeTime), scrSafeTime);
							} else {
								String scrSafeMode = MagicInfoConstant.SAFE_INTERVAL_MODE_DEFAULT;
								String scrSafeStartTime = MagicInfoConstant.TIME_DEFAULT;
								String scrSafeEndTime = MagicInfoConstant.TIME_DEFAULT;
								Object timerObject = new IntervalTimer(true, false, false, false, false, "2", scrSafeMode, scrSafeStartTime, scrSafeEndTime);
								sendGroupControl(propertyItem, deviceId, requestValue, MagicInfoConstant.MNT_SAFETY_SCREEN_TIMER, timerObject);

								stats.remove(MagicInfoConstant.SCREEN_BURN_PROTECTION_GROUP.concat(TIMER_MODE.getName()));
								removeValueForTheControllableProperty(stats, advancedControllableProperties, MagicInfoConstant.SCREEN_BURN_PROTECTION_GROUP.concat(TIMER_PERIOD.getName()));
								removeValueForTheControllableProperty(stats, advancedControllableProperties, MagicInfoConstant.SCREEN_BURN_PROTECTION_GROUP.concat(TIMER_TIME.getName()));

								stats.put(MagicInfoConstant.SCREEN_BURN_PROTECTION_GROUP.concat(TIMER_MODE.getName()), EnumTypeHandler.getNameByValue(IntervalModeEnum.class, scrSafeMode));
								String hour = convert12HourTo24Hour(scrSafeStartTime).split(MagicInfoConstant.COLON)[0];
								String minute = convert12HourTo24Hour(scrSafeStartTime).split(MagicInfoConstant.COLON)[1];
								addAdvanceControlProperties(advancedControllableProperties, stats,
										createDropdown(MagicInfoConstant.SCREEN_BURN_PROTECTION_GROUP.concat(TIMER_START_TIME_HOUR.getName()), createArrayNumber(0, 23), hour), hour);
								addAdvanceControlProperties(advancedControllableProperties, stats,
										createDropdown(MagicInfoConstant.SCREEN_BURN_PROTECTION_GROUP.concat(TIMER_START_TIME_MIN.getName()), createArrayNumber(0, 59), minute), minute);

								hour = convert12HourTo24Hour(scrSafeEndTime).split(MagicInfoConstant.COLON)[0];
								minute = convert12HourTo24Hour(scrSafeEndTime).split(MagicInfoConstant.COLON)[1];
								addAdvanceControlProperties(advancedControllableProperties, stats,
										createDropdown(MagicInfoConstant.SCREEN_BURN_PROTECTION_GROUP.concat(TIMER_END_TIME_HOUR.getName()), createArrayNumber(0, 23), hour), hour);
								addAdvanceControlProperties(advancedControllableProperties, stats,
										createDropdown(MagicInfoConstant.SCREEN_BURN_PROTECTION_GROUP.concat(TIMER_END_TIME_MIN.getName()), createArrayNumber(0, 59), minute), minute);
							}
						}
						break;
					case TIMER_END_TIME_MIN:
						String mode;
						String startTime;
						String endTime;
						IntervalTimer intervalTimer;
						if (cachedValue != null && cachedValue.has(MagicInfoConstant.MNT_SAFETY_SCREEN_TIMER) && checkChildNodeTimer(cachedValue.get(MagicInfoConstant.MNT_SAFETY_SCREEN_TIMER))) {
							mode = cachedValue.get(MagicInfoConstant.MNT_SAFETY_SCREEN_TIMER).get(TIMER_MODE.getFieldName()).asText();
							startTime = cachedValue.get(MagicInfoConstant.MNT_SAFETY_SCREEN_TIMER).get(TIMER_START_TIME_HOUR.getFieldName()).asText();
							endTime = cachedValue.get(MagicInfoConstant.MNT_SAFETY_SCREEN_TIMER).get(TIMER_END_TIME_HOUR.getFieldName()).asText();
							String hour = convert12HourTo24Hour(endTime).split(MagicInfoConstant.COLON)[0];
							requestValue = convertTo12HourFormat(hour, value);
							intervalTimer = new IntervalTimer(true, false, false, false, false, "2", mode, startTime, requestValue);
							sendGroupControl(propertyItem, deviceId, requestValue, MagicInfoConstant.MNT_SAFETY_SCREEN_TIMER, intervalTimer);
						}
						break;
					case TIMER_START_TIME_MIN:
						if (cachedValue != null && cachedValue.has(MagicInfoConstant.MNT_SAFETY_SCREEN_TIMER) && checkChildNodeTimer(cachedValue.get(MagicInfoConstant.MNT_SAFETY_SCREEN_TIMER))) {
							mode = cachedValue.get(MagicInfoConstant.MNT_SAFETY_SCREEN_TIMER).get(TIMER_MODE.getFieldName()).asText();
							startTime = cachedValue.get(MagicInfoConstant.MNT_SAFETY_SCREEN_TIMER).get(TIMER_START_TIME_HOUR.getFieldName()).asText();
							endTime = cachedValue.get(MagicInfoConstant.MNT_SAFETY_SCREEN_TIMER).get(TIMER_END_TIME_HOUR.getFieldName()).asText();
							String hour = convert12HourTo24Hour(startTime).split(MagicInfoConstant.COLON)[0];
							requestValue = convertTo12HourFormat(hour, value);
							intervalTimer = new IntervalTimer(true, false, false, false, false, "2", mode, requestValue, endTime);
							sendGroupControl(propertyItem, deviceId, requestValue, MagicInfoConstant.MNT_SAFETY_SCREEN_TIMER, intervalTimer);
						}
						break;
					case TIMER_END_TIME_HOUR:
						if (cachedValue != null && cachedValue.has(MagicInfoConstant.MNT_SAFETY_SCREEN_TIMER) && checkChildNodeTimer(cachedValue.get(MagicInfoConstant.MNT_SAFETY_SCREEN_TIMER))) {
							mode = cachedValue.get(MagicInfoConstant.MNT_SAFETY_SCREEN_TIMER).get(TIMER_MODE.getFieldName()).asText();
							startTime = cachedValue.get(MagicInfoConstant.MNT_SAFETY_SCREEN_TIMER).get(TIMER_START_TIME_HOUR.getFieldName()).asText();
							endTime = cachedValue.get(MagicInfoConstant.MNT_SAFETY_SCREEN_TIMER).get(TIMER_END_TIME_HOUR.getFieldName()).asText();
							String minute = convert12HourTo24Hour(endTime).split(MagicInfoConstant.COLON)[1];
							requestValue = convertTo12HourFormat(value, minute);
							intervalTimer = new IntervalTimer(true, false, false, false, false, "2", mode, startTime, requestValue);
							sendGroupControl(propertyItem, deviceId, requestValue, MagicInfoConstant.MNT_SAFETY_SCREEN_TIMER, intervalTimer);
						}
						break;
					case TIMER_START_TIME_HOUR:
						if (cachedValue != null && cachedValue.has(MagicInfoConstant.MNT_SAFETY_SCREEN_TIMER) && checkChildNodeTimer(cachedValue.get(MagicInfoConstant.MNT_SAFETY_SCREEN_TIMER))) {
							mode = cachedValue.get(MagicInfoConstant.MNT_SAFETY_SCREEN_TIMER).get(TIMER_MODE.getFieldName()).asText();
							startTime = cachedValue.get(MagicInfoConstant.MNT_SAFETY_SCREEN_TIMER).get(TIMER_START_TIME_HOUR.getFieldName()).asText();
							endTime = cachedValue.get(MagicInfoConstant.MNT_SAFETY_SCREEN_TIMER).get(TIMER_END_TIME_HOUR.getFieldName()).asText();
							String minute = convert12HourTo24Hour(startTime).split(MagicInfoConstant.COLON)[1];
							requestValue = convertTo12HourFormat(value, minute);
							intervalTimer = new IntervalTimer(true, false, false, false, false, "2", mode, requestValue, endTime);
							sendGroupControl(propertyItem, deviceId, requestValue, MagicInfoConstant.MNT_SAFETY_SCREEN_TIMER, intervalTimer);
						}
						break;
					case TIMER_TIME:
						RepeatTimer repeatTimer;
						if (cachedValue != null && cachedValue.has(MagicInfoConstant.MNT_SAFETY_SCREEN_TIMER) && checkChildNodeTimer(cachedValue.get(MagicInfoConstant.MNT_SAFETY_SCREEN_TIMER))) {
							String modeRepeat = cachedValue.get(MagicInfoConstant.MNT_SAFETY_SCREEN_TIMER).get(TIMER_MODE.getFieldName()).asText();
							String periodTime = cachedValue.get(MagicInfoConstant.MNT_SAFETY_SCREEN_TIMER).get(TIMER_PERIOD.getFieldName()).asText();
							repeatTimer = new RepeatTimer(true, false, false, false, false, "1", modeRepeat, periodTime, value);
							sendGroupControl(propertyItem, deviceId, value, MagicInfoConstant.MNT_SAFETY_SCREEN_TIMER, repeatTimer);
						}
						break;
					case TIMER_PERIOD:
						if (cachedValue != null && cachedValue.has(MagicInfoConstant.MNT_SAFETY_SCREEN_TIMER) && checkChildNodeTimer(cachedValue.get(MagicInfoConstant.MNT_SAFETY_SCREEN_TIMER))) {
							String modeRepeat = cachedValue.get(MagicInfoConstant.MNT_SAFETY_SCREEN_TIMER).get(TIMER_MODE.getFieldName()).asText();
							String timerTime = cachedValue.get(MagicInfoConstant.MNT_SAFETY_SCREEN_TIMER).get(TIMER_TIME.getFieldName()).asText();
							value = checkValidInput(0, 10, value);
							repeatTimer = new RepeatTimer(true, false, false, false, false, "1", modeRepeat, value, timerTime);
							sendGroupControl(propertyItem, deviceId, value, MagicInfoConstant.MNT_SAFETY_SCREEN_TIMER, repeatTimer);
						}
						break;
					default:
						if (logger.isWarnEnabled()) {
							logger.warn(String.format("Unable to execute %s command on device %s: Not Supported", property, deviceId));
						}
						controlPropagated = false;
						break;
				}
				if (controlPropagated) {
					checkControl = true;
					updateLocalControlValue(stats, advancedControllableProperties, property, value);
					updateListAggregatedDevice(deviceId, stats, advancedControllableProperties);
				}
			} else {
				throw new IllegalArgumentException(String.format("Unable to control property: %s as the device does not exist.", property));
			}
		} finally {
			reentrantLock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void controlProperties(List<ControllableProperty> controllableProperties) throws Exception {
		if (CollectionUtils.isEmpty(controllableProperties)) {
			throw new IllegalArgumentException("ControllableProperties can not be null or empty");
		}
		for (ControllableProperty p : controllableProperties) {
			try {
				controlProperty(p);
			} catch (Exception e) {
				logger.error(String.format("Error when control property %s", p.getProperty()), e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<AggregatedDevice> retrieveMultipleStatistics() throws Exception {
		if (!aggregatedIdList.isEmpty()) {
			if (!checkValidApiToken()) {
				throw new FailedLoginException("API Token cannot be null or empty, please enter valid password and username field.");
			}
			if (executorService == null) {
				executorService = Executors.newFixedThreadPool(1);
				executorService.submit(deviceDataLoader = new MagicInfoDataLoader());
			}
			nextDevicesCollectionIterationTimestamp = System.currentTimeMillis();
			updateValidRetrieveStatisticsTimestamp();
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
		super.internalDestroy();
	}

	/**
	 * {@inheritDoc}
	 * set API_Key into Header of Request
	 */
	@Override
	protected HttpHeaders putExtraRequestHeaders(HttpMethod httpMethod, String uri, HttpHeaders headers) {
		headers.set(MagicInfoConstant.API_KEY, apiToken);
		return headers;
	}

	/**
	 * Check API token validation
	 * If the token expires, we send a request to get a new token
	 *
	 * @return boolean
	 */
	private boolean checkValidApiToken() throws Exception {
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
	private String getToken() throws Exception {
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
			throw new FailedLoginException("Failed to retrieve an access token for account with from username and password. Please username id and password");
		}
		return token;
	}

	/**
	 * Get system information of SamsungMagicInfo
	 */
	private void retrieveSystemInfo() {
		try {
			aggregatorResponse = this.doGet(MagicInfoCommand.DEVICE_DASHBOARD, JsonNode.class);
		} catch (Exception e) {
			throw new ResourceNotReachableException("Error when get system information.", e);
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
		try {
			JsonNode devicesResponse = null;
			if (StringUtils.isNotNullOrEmpty(filterDeviceType) || StringUtils.isNotNullOrEmpty(filterSource) || StringUtils.isNotNullOrEmpty(filterFunction)) {
				JsonNode body = createBodyFilteringRequest();
				if (checkNoneValueInJsonArray(body.get(MagicInfoConstant.INPUT_FUNCTION)) && checkNoneValueInJsonArray(body.get(MagicInfoConstant.INPUT_SOURCE))) {
					devicesResponse = this.doPost(MagicInfoCommand.FILTERING_COMMAND, body, JsonNode.class);
				}
			} else {
				devicesResponse = this.doGet(MagicInfoCommand.ALL_DEVICES_COMMAND, JsonNode.class);
			}
			aggregatedIdList.clear();
			if (devicesResponse != null && devicesResponse.has(MagicInfoConstant.ITEMS)) {
				for (JsonNode item : devicesResponse.get(MagicInfoConstant.ITEMS)) {
					aggregatedIdList.add(item.get(MagicInfoConstant.DEVICE_ID).asText());
				}
			}
		} catch (Exception e) {
			aggregatedIdList.clear();
			logger.error(String.format("Error when get system information, %s", e));
		}
	}

	/**
	 * Creates a JSON ObjectNode for filtering request.
	 *
	 * @return JSON ObjectNode containing filtering request information.
	 */
	private JsonNode createBodyFilteringRequest() {
		List<String> sourceValue = new ArrayList<>();
		List<String> deviceTypeValue = new ArrayList<>();
		List<String> functionValue = new ArrayList<>();
		if (StringUtils.isNotNullOrEmpty(filterDeviceType)) {
			deviceTypeValue = Arrays.stream(filterDeviceType.split(MagicInfoConstant.COMMA)).map(String::trim).collect(Collectors.toList());
		}
		if (StringUtils.isNotNullOrEmpty(filterSource)) {
			sourceValue = Arrays.stream(filterSource.split(MagicInfoConstant.COMMA)).map(String::trim)
					.map(item -> EnumTypeHandler.getValueByName(SourceEnum.class, item))
					.collect(Collectors.toList());
		}
		if (StringUtils.isNotNullOrEmpty(filterFunction)) {
			functionValue = Arrays.stream(filterFunction.split(MagicInfoConstant.COMMA)).map(String::trim)
					.map(item -> EnumTypeHandler.getValueByName(FunctionFilterEnum.class, item))
					.collect(Collectors.toList());
		}
		ObjectNode body = objectMapper.createObjectNode();
		body.put(MagicInfoConstant.PAGE_SIZE, MagicInfoConstant.PAGE_SIZE_DEFAULT_FILTER);
		body.set(MagicInfoConstant.DEVICE_TYPE, objectMapper.valueToTree(deviceTypeValue));
		body.set(MagicInfoConstant.INPUT_SOURCE, objectMapper.valueToTree(sourceValue));
		body.set(MagicInfoConstant.INPUT_FUNCTION, objectMapper.valueToTree(functionValue));
		return body;
	}

	/**
	 * Checks if a JSON array contains only non-"None" values.
	 *
	 * @param node The JSON array to be checked.
	 */
	private boolean checkNoneValueInJsonArray(JsonNode node) {
		if (!node.isArray()) {
			return false;
		}
		for (int i = 0; i < node.size(); i++) {
			if (MagicInfoConstant.NONE.equals(node.get(i).asText())) {
				return false;
			}
		}
		return true;
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

			if (checkDeviceInformationResponse(displayInfoResponse) && checkDeviceInformationResponse(generalInfoResponse)) {
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
	 * Retrieves display control information for a specific device.
	 *
	 * @param deviceId The unique identifier of the device for which to retrieve display control information.
	 * @return A JSON object containing display control information for the specified device.
	 * @throws IllegalArgumentException If there is an issue retrieving the display control information.
	 */
	private JsonNode getDisplayControlsInfo(String deviceId) {
		try {
			ObjectNode idListParam = createArrayIdsNode(deviceId, MagicInfoConstant.IDS);
			JsonNode displayInfoResponse = this.doPost(MagicInfoCommand.DISPLAY_INFO_COMMAND, (JsonNode) idListParam, JsonNode.class);
			if (checkDeviceInformationResponse(displayInfoResponse)) {
				return displayInfoResponse.get(MagicInfoConstant.ITEMS).get(MagicInfoConstant.SUCCESS_LIST).get(0);
			}
			throw new IllegalArgumentException("The response is error");
		} catch (Exception e) {
			throw new IllegalArgumentException("Error when get display information", e);
		}
	}

	/**
	 * Checks if the provided JSON response contains device information.
	 *
	 * @param response The JSON response to be checked.
	 * @return if the response contains device information is true
	 */
	private boolean checkDeviceInformationResponse(JsonNode response) {
		if (response != null && response.has(MagicInfoConstant.ITEMS) && response.get(MagicInfoConstant.ITEMS).has(MagicInfoConstant.SUCCESS_LIST)) {
			return true;
		}
		return false;
	}

	/**
	 * Clone an aggregated device list that based on aggregatedDeviceList variable
	 * populate monitoring and controlling for aggregated device
	 *
	 * @return List<AggregatedDevice> aggregated device list
	 */
	private List<AggregatedDevice> cloneAndPopulateAggregatedDeviceList() {
		if (!checkControl) {
			if (!cachedAggregatedDeviceList.isEmpty()) {
				aggregatedDeviceList.clear();
			}
			synchronized (cachedAggregatedDeviceList) {
				for (AggregatedDevice aggregatedDevice : cachedAggregatedDeviceList) {
					List<AdvancedControllableProperty> advancedControllableProperties = new ArrayList<>();
					Map<String, String> dynamics = new HashMap<>();
					Map<String, String> stats = new HashMap<>();
					mapGeneralInformationProperties(aggregatedDevice.getProperties(), stats);
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
	 * This method processes specific properties from the provided localCachedStatistic and updates the stats map accordingly.
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
				case SOURCE:
					List<String> availableValues = Arrays.stream(EnumTypeHandler.getEnumValues(SourceEnum.class)).collect(Collectors.toList());
					stats.put(propertyName, availableValues.contains(value) ? EnumTypeHandler.getNameByValue(SourceEnum.class, value) : MagicInfoConstant.NONE);
					break;
				default:
					stats.put(propertyName, value);
					break;
			}
		}
	}

	/**
	 * Maps display information properties from a mapping statistic to a target statistics map.
	 * This method processes specific properties from the provided localCachedStatistic and updates the stats map accordingly.
	 *
	 * @param stats The target statistics map where the properties will be mapped.
	 * @param advancedControllableProperties A list to collect advanced controllable properties.
	 */
	private void mapDisplayInformationProperties(Map<String, String> mappingStatistic, Map<String, String> stats, List<AdvancedControllableProperty> advancedControllableProperties) {
		String value;
		String propertyName;
		int status;
		for (DisplayInfo item : values()) {
			propertyName = item.getGroup().concat(item.getName());
			value = getDefaultValueForNullData(mappingStatistic.get(item.getName()));
			switch (item) {
				case RESTART:
					addAdvanceControlProperties(advancedControllableProperties, stats, createButton(propertyName, MagicInfoConstant.RESTART, MagicInfoConstant.RESTARTING, MagicInfoConstant.GRACE_PERIOD),
							MagicInfoConstant.NONE);
					break;
				case POWER:
					addAdvanceControlProperties(advancedControllableProperties, stats, createSwitch(propertyName, MagicInfoConstant.TRUE.equals(value) ? 1 : 0, MagicInfoConstant.OFF, MagicInfoConstant.ON),
							MagicInfoConstant.TRUE.equals(value) ? MagicInfoConstant.NUMBER_ONE : MagicInfoConstant.ZERO);
					break;
				case RESET_SOUND:
				case RESET_PICTURE:
					addAdvanceControlProperties(advancedControllableProperties, stats, createButton(propertyName, MagicInfoConstant.RESET, MagicInfoConstant.RESETTING, MagicInfoConstant.GRACE_PERIOD),
							MagicInfoConstant.NONE);
					break;
				case DISPLAY_PANEL:
					value = MagicInfoConstant.NUMBER_ONE.equals(value) ? MagicInfoConstant.ZERO : MagicInfoConstant.NUMBER_ONE;
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
					if (!MagicInfoConstant.NONE.equals(value)) {
						addAdvanceControlProperties(advancedControllableProperties, stats, createNumeric(propertyName, value), value);
					} else {
						stats.put(propertyName, MagicInfoConstant.NONE);
					}
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
				case COLOR_TONE:
					addAdvanceControlProperties(advancedControllableProperties, stats,
							createDropdown(propertyName, EnumTypeHandler.getEnumNames(ColorToneEnum.class), EnumTypeHandler.getNameByValue(ColorToneEnum.class, value)), value);
					break;
				case COLOR_TEMPERATURE:
					List<String> availableValues = Arrays.stream(EnumTypeHandler.getEnumValues(ColorTemperatureEnum.class)).collect(Collectors.toList());
					stats.put(propertyName, availableValues.contains(value) ? EnumTypeHandler.getNameByValue(ColorTemperatureEnum.class, value) : MagicInfoConstant.NONE);
					break;
				case PICTURE_SIZE:
					availableValues = Arrays.stream(EnumTypeHandler.getEnumValues(PictureSizeEnum.class)).collect(Collectors.toList());
					stats.put(propertyName, availableValues.contains(value) ? EnumTypeHandler.getNameByValue(PictureSizeEnum.class, value) : MagicInfoConstant.NONE);
					break;
				case DIGITAL_CLEAN_VIEW:
					availableValues = Arrays.stream(EnumTypeHandler.getEnumValues(DigitalCleanViewEnum.class)).collect(Collectors.toList());
					stats.put(propertyName, availableValues.contains(value) ? EnumTypeHandler.getNameByValue(DigitalCleanViewEnum.class, value) : MagicInfoConstant.NONE);
					break;
				case FILM_MODE:
					availableValues = Arrays.stream(EnumTypeHandler.getEnumValues(FilmModeEnum.class)).collect(Collectors.toList());
					stats.put(propertyName, availableValues.contains(value) ? EnumTypeHandler.getNameByValue(FilmModeEnum.class, value) : MagicInfoConstant.NONE);
					break;
				case HDMI_BLACK_LEVEL:
					availableValues = Arrays.stream(EnumTypeHandler.getEnumValues(HDMIBlackLevelEnum.class)).collect(Collectors.toList());
					stats.put(propertyName, availableValues.contains(value) ? EnumTypeHandler.getNameByValue(HDMIBlackLevelEnum.class, value) : MagicInfoConstant.NONE);
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
				case AUTO_SOURCE_SWITCHING:
					status = 1;
					if (MagicInfoConstant.ZERO.equals(value)) {
						status = 0;
					}
					addAdvanceControlProperties(advancedControllableProperties, stats, createSwitch(propertyName, status, MagicInfoConstant.OFF, MagicInfoConstant.ON), value);
					break;
				case RESTORE_PRIMARY_SOURCE:
					if (!MagicInfoConstant.ZERO.equals(mappingStatistic.get(MagicInfoConstant.AUTO_SOURCE_SWITCHING))) {
						stats.put(propertyName, MagicInfoConstant.ZERO.equals(value) ? MagicInfoConstant.OFF : MagicInfoConstant.ON);
					}
					break;
				case PRIMARY_SOURCE:
				case SECONDARY_SOURCE:
					if (!MagicInfoConstant.ZERO.equals(mappingStatistic.get(MagicInfoConstant.AUTO_SOURCE_SWITCHING))) {
						stats.put(propertyName, EnumTypeHandler.getNameByValue(SourceEnum.class, value));
					}
					break;
				case MAX_TIME_HOUR:
				case MIN_TIME_HOUR:
					String time;
					value = mappingStatistic.get(MagicInfoConstant.MAX_TIME);
					if (MIN_TIME_HOUR.getName().equals(item.getName())) {
						value = mappingStatistic.get(MagicInfoConstant.MIN_TIME);
					}
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
				case MAX_TIME_MINUTE:
				case MIN_TIME_MINUTE:
					value = mappingStatistic.get(MagicInfoConstant.MAX_TIME);
					if (MIN_TIME_MINUTE.getName().equals(item.getName())) {
						value = mappingStatistic.get(MagicInfoConstant.MIN_TIME);
					}
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
				case TIMER_END_TIME_HOUR:
				case TIMER_START_TIME_HOUR:
					if (MagicInfoConstant.NUMBER_TWO.equals(mappingStatistic.get(MagicInfoConstant.TIMER))) {
						time = convert12HourTo24Hour(value);
						if (!MagicInfoConstant.NONE.equals(time)) {
							String hour = time.split(MagicInfoConstant.COLON)[0];
							addAdvanceControlProperties(advancedControllableProperties, stats, createDropdown(propertyName, createArrayNumber(0, 23), hour), hour);
						} else {
							stats.put(propertyName, MagicInfoConstant.NONE);
						}
					}
					break;
				case TIMER_START_TIME_MIN:
				case TIMER_END_TIME_MIN:
					if (MagicInfoConstant.NUMBER_TWO.equals(mappingStatistic.get(MagicInfoConstant.TIMER))) {
						time = convert12HourTo24Hour(value);
						if (!MagicInfoConstant.NONE.equals(time)) {
							String minute = time.split(MagicInfoConstant.COLON)[1];
							addAdvanceControlProperties(advancedControllableProperties, stats, createDropdown(propertyName, createArrayNumber(0, 59), minute), minute);
						} else {
							stats.put(propertyName, MagicInfoConstant.NONE);
						}
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
						stats.put(propertyName, EnumTypeHandler.getNameByValue(RepeatModeEnum.class, value));
					} else if (MagicInfoConstant.NUMBER_TWO.equals(mappingStatistic.get(MagicInfoConstant.TIMER))) {
						stats.put(propertyName, EnumTypeHandler.getNameByValue(IntervalModeEnum.class, value));
					}
					break;
				case TIMER_PERIOD:
					if (MagicInfoConstant.NUMBER_ONE.equals(mappingStatistic.get(MagicInfoConstant.TIMER))) {
						addAdvanceControlProperties(advancedControllableProperties, stats, createNumeric(propertyName, value), value);
					}
					break;
				case TIMER_TIME:
					if (MagicInfoConstant.NUMBER_ONE.equals(mappingStatistic.get(MagicInfoConstant.TIMER))) {
						addAdvanceControlProperties(advancedControllableProperties, stats, createDropdown(propertyName, MagicInfoConstant.TIMER_TIME_VALUES, value), value);
					}
					break;
				case IMMEDIATE_DISPLAY:
					availableValues = Arrays.stream(EnumTypeHandler.getEnumValues(ImmediateDisplayEnum.class)).collect(Collectors.toList());
					stats.put(propertyName, availableValues.contains(value) ? EnumTypeHandler.getNameByValue(ImmediateDisplayEnum.class, value) : MagicInfoConstant.NONE);
					break;
				default:
					stats.put(propertyName, value);
					break;
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
	 * Checks if the provided JSON node contains child nodes related to Web Browser settings.
	 *
	 * @param jsonNode The JSON node to be checked.
	 * @return true if the JSON node contains the required child nodes
	 */
	private boolean checkChildNodeWebBrowser(JsonNode jsonNode) {
		return jsonNode.has(WEB_BROWSER_ZOOM.getFieldName())
				&& jsonNode.has(WEB_BROWSER_INTERVAL.getFieldName())
				&& jsonNode.has(WEB_BROWSER_HOME_PAGE.getFieldName())
				&& jsonNode.has(WEB_BROWSER_PAGE_URL.getFieldName());
	}

	/**
	 * Checks if the provided JSON node contains child nodes related to Maintenance settings.
	 *
	 * @param jsonNode The JSON node to be checked.
	 * @return true if the JSON node contains the required child nodes
	 */
	private boolean checkChildNodeMaintenance(JsonNode jsonNode) {
		return jsonNode.has(MAX_VALUE.getFieldName())
				&& jsonNode.has(MIN_VALUE.getFieldName())
				&& jsonNode.has(MAX_TIME_HOUR.getFieldName())
				&& jsonNode.has(MIN_TIME_HOUR.getFieldName())
				&& jsonNode.has(SCREEN_LAMP_SCHEDULE.getFieldName());
	}

	/**
	 * Checks if the provided JSON node contains child nodes related to Auto Source Switching settings.
	 *
	 * @param jsonNode The JSON node to be checked.
	 * @return true if the JSON node contains the required child nodes
	 */
	private boolean checkChildNodeAutoSourceSwitching(JsonNode jsonNode) {
		return jsonNode.has(RESTORE_PRIMARY_SOURCE.getFieldName())
				&& jsonNode.has(PRIMARY_SOURCE.getFieldName())
				&& jsonNode.has(SECONDARY_SOURCE.getFieldName())
				&& jsonNode.has(AUTO_SOURCE_SWITCHING.getFieldName());
	}

	/**
	 * Checks if the provided JSON node contains child nodes related to Pixel Shift settings.
	 *
	 * @param jsonNode The JSON node to be checked.
	 * @return true if the JSON node contains the required child nodes
	 */
	private boolean checkChildNodePixelShift(JsonNode jsonNode) {
		return jsonNode.has(PIXEL_SHIFT.getFieldName())
				&& jsonNode.has(PIXEL_SHIFT_HORIZONTAL.getFieldName())
				&& jsonNode.has(PIXEL_SHIFT_VERTICAL.getFieldName())
				&& jsonNode.has(PIXEL_SHIFT_TIME.getFieldName());
	}

	/**
	 * Checks if the provided JSON node contains child nodes related to Timer settings.
	 *
	 * @param jsonNode The JSON node to be checked.
	 * @return true if the JSON node contains the required child nodes
	 */
	private boolean checkChildNodeTimer(JsonNode jsonNode) {
		if(!jsonNode.has(TIMER.getFieldName())){
			return false;
		}
		String timerValue = jsonNode.get(TIMER.getFieldName()).asText();
		if (MagicInfoConstant.NUMBER_ONE.equals(timerValue)) {
			return jsonNode.has(TIMER_MODE.getFieldName()) && jsonNode.has(TIMER_TIME.getFieldName()) && jsonNode.has(TIMER_PERIOD.getFieldName());
		} else if (MagicInfoConstant.NUMBER_TWO.equals(timerValue)) {
			return jsonNode.has(TIMER_MODE.getFieldName()) && jsonNode.has(TIMER_END_TIME_HOUR.getFieldName()) && jsonNode.has(TIMER_START_TIME_HOUR.getFieldName());
		}
		return true;
	}

	/**
	 * Updates cached devices' control value, after the control command was executed with the specified value.
	 * It is done in order for aggregator to populate the latest control values, after the control command has been executed,
	 * but before the next devices details polling cycle was addressed.
	 *
	 * @param stats The updated device properties.
	 * @param advancedControllableProperties The updated list of advanced controllable properties.
	 * @param name of the control property
	 * @param value to set to the control property
	 */
	private void updateLocalControlValue(Map<String, String> stats, List<AdvancedControllableProperty> advancedControllableProperties, String name, String value) {
		stats.put(name, value);
		advancedControllableProperties.stream().filter(advancedControllableProperty ->
				name.equals(advancedControllableProperty.getName())).findFirst().ifPresent(advancedControllableProperty ->
				advancedControllableProperty.setValue(value));
	}

	/**
	 * Updates the properties and controllable properties of an aggregated device in the list.
	 *
	 * @param deviceId The unique identifier of the device to update.
	 * @param stats The updated device properties.
	 * @param advancedControllableProperties The updated list of advanced controllable properties.
	 */
	private void updateListAggregatedDevice(String deviceId, Map<String, String> stats, List<AdvancedControllableProperty> advancedControllableProperties) {
		Optional<AggregatedDevice> device = aggregatedDeviceList.stream().filter(aggregatedDevice ->
				deviceId.equals(aggregatedDevice.getDeviceId())).findFirst();
		if (device.isPresent()) {
			device.get().setControllableProperties(advancedControllableProperties);
			device.get().setProperties(stats);
		}
	}

	/**
	 * Checks the connection status of a device using its unique identifier.
	 *
	 * @param id The unique identifier of the device to check.
	 */
	private boolean checkConnectionDevice(String id) {
		try {
			ObjectNode idListParam = createArrayIdsNode(id, MagicInfoConstant.IDS);
			JsonNode response = this.doPost(MagicInfoCommand.CHECK_CONNECTION_COMMAND, (JsonNode) idListParam, JsonNode.class);
			if (response != null && response.has(MagicInfoConstant.STATUS)) {
				return response.get(MagicInfoConstant.STATUS).asText().equals(MagicInfoConstant.SUCCESS);
			}
		} catch (Exception e) {
			logger.error(String.format("Error when send request to check connection device, %s", e));
		}
		return false;
	}

	/**
	 * Checks if the data has been successfully updated on a device after sending a control request.
	 *
	 * @param id The unique identifier of the device.
	 * @param requestId The request identifier associated with the control request.
	 * @param group The group identifier, if applicable (or an empty string if not used).
	 * @param field The field name of the property being checked.
	 * @param value The expected value of the property.
	 */
	private boolean checkDataAfterUpdate(String id, String requestId, String group, String field, String value) {
		try {
			ObjectNode body = createArrayIdsNode(id, MagicInfoConstant.DEVICE_IDS);
			body.put(MagicInfoConstant.REQUEST_ID, requestId);
			boolean status = false;
			JsonNode response = null;
			while (!status) {
				response = this.doPost(MagicInfoCommand.UPDATE_DISPLAY_COMMAND, (JsonNode) body, JsonNode.class);
				if (response != null && response.has(MagicInfoConstant.STATUS) && MagicInfoConstant.SUCCESS.equals(response.get(MagicInfoConstant.STATUS).asText())) {
					status = true;
				} else {
					//sleep 1000 to void send many request to the device
					Thread.sleep(1000);
				}
			}
			if (response.has(MagicInfoConstant.ITEMS) && response.get(MagicInfoConstant.ITEMS).get(MagicInfoConstant.SUCCESS_LIST).size() > 0) {
				if (StringUtils.isNotNullOrEmpty(group)) {
					return response.get(MagicInfoConstant.ITEMS).get(MagicInfoConstant.SUCCESS_LIST).get(0).get(group).get(field).asText().equals(value);
				}
				return response.get(MagicInfoConstant.ITEMS).get(MagicInfoConstant.SUCCESS_LIST).get(0).get(field).asText().equals(value);
			}
		} catch (Exception e) {
			logger.error(String.format("Error when send request to check data after update, %s", e));
		}
		return false;
	}

	/**
	 * Send a restart command to a device with the specified ID.
	 *
	 * @param id The ID of the device to restart.
	 * @throws IllegalArgumentException If the device is disconnected or the restart operation encounters an error.
	 */
	private void sendRestartCommand(String id) {
		if (!checkConnectionDevice(id)) {
			throw new IllegalArgumentException("The device is disconnected.");
		}
		try {
			ObjectNode body = createArrayIdsNode(id, MagicInfoConstant.DEVICE_IDS);
			body.put(MagicInfoConstant.MENU, MagicInfoConstant.RESTART_VALUE);
			body.put(MagicInfoConstant.VALUE, MagicInfoConstant.RESTART_VALUE);
			JsonNode response = this.doPut(MagicInfoCommand.QUICK_CONTROL_COMMAND, (JsonNode) body, JsonNode.class);
			if (!(response != null && response.has(MagicInfoConstant.STATUS) && MagicInfoConstant.SUCCESS.equals(response.get(MagicInfoConstant.STATUS).asText()))) {
				throw new IllegalArgumentException("The device has responded with an error.");
			}
		} catch (Exception e) {
			throw new IllegalArgumentException("Can't control property Restart " + e.getMessage(), e);
		}
	}

	/**
	 * Send a power control command to a device with the specified ID.
	 *
	 * @param id The ID of the device to control.
	 * @param value The power control value (e.g., "on" or "off").
	 * @throws IllegalArgumentException If the device is disconnected or the control operation encounters an error.
	 */
	private void sendPowerCommand(String id, String value) {
		if (!checkConnectionDevice(id)) {
			throw new IllegalArgumentException("The device is disconnected.");
		}
		try {
			ObjectNode body = createArrayIdsNode(id, MagicInfoConstant.DEVICE_IDS);
			body.put(MagicInfoConstant.MENU, MagicInfoConstant.POWER);
			body.put(MagicInfoConstant.VALUE, value);
			JsonNode response = this.doPut(MagicInfoCommand.QUICK_CONTROL_COMMAND, (JsonNode) body, JsonNode.class);
			if (!(response != null && response.has(MagicInfoConstant.STATUS) && MagicInfoConstant.SUCCESS.equals(response.get(MagicInfoConstant.STATUS).asText()))) {
				throw new IllegalArgumentException("The device has responded with an error.");
			}
		} catch (Exception e) {
			throw new IllegalArgumentException("Can't control property Power " + e.getMessage(), e);
		}
	}

	/**
	 * Sends a reset control request to a device for the specified property.
	 *
	 * @param property The property to reset.
	 * @param id The unique identifier of the device.
	 * @param value The value to set for the property.
	 * @throws IllegalArgumentException If the device is disconnected or if an error occurs while sending the control request.
	 */
	private void sendResetControl(DisplayInfo property, String id, String value) {
		if (!checkConnectionDevice(id)) {
			throw new IllegalArgumentException("The device is disconnected.");
		}
		try {
			ObjectNode body = createArrayIdsNode(id, MagicInfoConstant.DEVICE_IDS);
			body.put(property.getFieldName(), value);
			String requestId = getRequestIdByUpdateCommand(body);

			body.remove(property.getFieldName());
			body.put(MagicInfoConstant.REQUEST_ID, requestId);
			boolean status = false;
			JsonNode responseData;
			while (!status) {
				responseData = this.doPost(MagicInfoCommand.UPDATE_DISPLAY_COMMAND, (JsonNode) body, JsonNode.class);
				if (responseData != null && responseData.has(MagicInfoConstant.STATUS) && MagicInfoConstant.SUCCESS.equals(responseData.get(MagicInfoConstant.STATUS).asText())) {
					status = true;
				} else {
					//sleep 1000 to void send many request to the device
					Thread.sleep(1000);
				}
			}
		} catch (Exception e) {
			throw new IllegalArgumentException(String.format("Can't control property %s", property.getName()), e);
		}
	}

	/**
	 * Sends a control request to update the specified property for a device.
	 *
	 * @param property The property to update.
	 * @param id The unique identifier of the device.
	 * @param value The new value to set for the property.
	 * @throws IllegalArgumentException If the device is disconnected or if an error occurs while sending the control request.
	 */
	private void sendControlRequest(DisplayInfo property, String id, String value) {
		if (!checkConnectionDevice(id)) {
			throw new IllegalArgumentException("The device is disconnected.");
		}
		try {
			ObjectNode body = createArrayIdsNode(id, MagicInfoConstant.DEVICE_IDS);
			body.put(property.getFieldName(), value);
			String requestId = getRequestIdByUpdateCommand(body);
			if (!checkDataAfterUpdate(id, requestId, "", property.getFieldName(), value)) {
				throw new IllegalArgumentException(String.format("Can't control property %s with value %s same WebUI", property.getName(), value));
			}
		} catch (Exception e) {
			throw new IllegalArgumentException(String.format("Can't control property %s with value %s ", property.getName(), value), e);
		}
	}

	/**
	 * Sends a control request to update the specified property for a device.
	 *
	 * @param property The property to update.
	 * @param id The unique identifier of the device.
	 * @param value The new value to set for the property.
	 * @throws IllegalArgumentException If the device is disconnected or if an error occurs while sending the control request.
	 */
	private void sendGroupControl(DisplayInfo property, String id, String value, String group, Object object) {
		if (!checkConnectionDevice(id)) {
			throw new IllegalArgumentException("The device is disconnected.");
		}
		try {
			ObjectNode body = createArrayIdsNode(id, MagicInfoConstant.DEVICE_IDS);
			JsonNode jsonValue = objectMapper.valueToTree(object);
			body.set(group, jsonValue);
			String requestId = getRequestIdByUpdateCommand(body);
			if (!checkDataAfterUpdate(id, requestId, group, property.getFieldName(), value)) {
				throw new IllegalArgumentException(String.format("In WebUI We can't control property %s with value %s", property.getName(), value));
			}
		} catch (Exception e) {
			throw new IllegalArgumentException(String.format("Can't control property %s with value %s", property.getName(), value), e);
		}
	}

	/**
	 * Creates a JSON object containing an array of IDs under the specified name.
	 *
	 * @param id The ID to include in the array.
	 * @param name The name to assign to the array in the JSON object.
	 * @return The JSON object containing the array of IDs.
	 */
	private ObjectNode createArrayIdsNode(String id, String name) {
		List<String> ids = Collections.singletonList(id);
		ObjectNode idListNode = objectMapper.createObjectNode();
		idListNode.set(name, objectMapper.valueToTree(ids));
		return idListNode;
	}

	/**
	 * Retrieves the request ID generated by an update command based on the provided JSON body.
	 *
	 * @param body The JSON body used for the update command.
	 * @return The request ID associated with the update command.
	 * @throws IllegalArgumentException If the request ID is not found in the response or if there's an issue sending the update command.
	 */
	private String getRequestIdByUpdateCommand(ObjectNode body) {
		try {
			JsonNode response = this.doPut(MagicInfoCommand.UPDATE_DISPLAY_COMMAND, (JsonNode) body, JsonNode.class);
			if (response != null && response.has(MagicInfoConstant.ITEMS) && response.get(MagicInfoConstant.ITEMS).get(MagicInfoConstant.SUCCESS_LIST).size() > 0) {
				return response.get(MagicInfoConstant.ITEMS).get(MagicInfoConstant.REQUEST_ID).asText();
			}
			throw new IllegalArgumentException("Request Id is null");
		} catch (Exception e) {
			throw new IllegalArgumentException("Can't send update display control command", e);
		}
	}

	/**
	 * Removes a controllable property and its associated value from the provided statistics and advanced controllable properties lists.
	 *
	 * @param stats The statistics map containing property values.
	 * @param advancedControllableProperties The list of advanced controllable properties.
	 * @param name The name of the property to remove.
	 */
	private void removeValueForTheControllableProperty(Map<String, String> stats, List<AdvancedControllableProperty> advancedControllableProperties, String name) {
		stats.remove(name);
		advancedControllableProperties.removeIf(item -> item.getName().equalsIgnoreCase(name));
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
	 * Checks if the input value is valid and converts it to an integer.
	 *
	 * @param value The input value to be checked and converted to an integer.
	 * @param min is the minimum value
	 * @param max is the maximum value
	 * @return The converted integer value if the input is valid.
	 * @throws IllegalArgumentException if the input value is not a valid integer.
	 */
	private String checkValidInput(int min, int max, String value) {
		if (value.contains(MagicInfoConstant.DOT)) {
			value = value.split(MagicInfoConstant.DOT_REGEX)[0];
		}
		int initial = min;
		try {
			int valueCompare = Integer.parseInt(value);
			if (min <= valueCompare && valueCompare <= max) {
				return String.valueOf(valueCompare);
			}
			if (valueCompare > max) {
				initial = max;
			}
		} catch (Exception e) {
			if (!value.contains(MagicInfoConstant.DASH)) {
				initial = max;
			}
		}
		return String.valueOf(initial);
	}

	/**
	 * Adds a controllable property and its associated value to the provided statistics and advanced controllable properties lists.
	 *
	 * @param stats The statistics map to which the property value will be added.
	 * @param advancedControllableProperties The list of advanced controllable properties to which the property will be added.
	 * @param property The advanced controllable property to add.
	 * @param value The value of the property to be added.
	 */
	private void addValueForTheControllableProperty(Map<String, String> stats, List<AdvancedControllableProperty> advancedControllableProperties, AdvancedControllableProperty property, String value) {
		if (property != null) {
			removeValueForTheControllableProperty(stats, advancedControllableProperties, property.getName());
			stats.put(property.getName(), StringUtils.isNotNullOrEmpty(value) ? value : MagicInfoConstant.EMPTY);
			advancedControllableProperties.add(property);
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
		ObjectNode combinedNode = objectMapper.createObjectNode();
		combinedNode.setAll((ObjectNode) nodeA);
		combinedNode.setAll((ObjectNode) nodeB);

		return combinedNode;
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
			logger.debug("Error when convert milliseconds to datetime ", e);
		}
		return MagicInfoConstant.NONE;
	}

	/**
	 * Converts a time in 24-hour format to a 12-hour format with AM/PM indicator.
	 *
	 * @param hourValue The hour value in 24-hour format (0-23).
	 * @param minuteValue The minute value (0-59).
	 * @return A formatted string representing the time in 12-hour format with AM/PM.
	 */
	public static String convertTo12HourFormat(String hourValue, String minuteValue) {
		int hour = Integer.parseInt(hourValue);
		int minute = Integer.parseInt(minuteValue);
		String period = hour >= 12 ? MagicInfoConstant.PM : MagicInfoConstant.AM;
		if (hour == 0) {
			hour = 12;
		} else if (hour > 12) {
			hour -= 12;
		}
		String formattedHour = String.valueOf(hour);
		String formattedMinute = minute < 10 ? MagicInfoConstant.ZERO + minute : String.valueOf(minute);
		return formattedHour + MagicInfoConstant.COLON + formattedMinute + period;
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
		if (MagicInfoConstant.MB.equals(unit)) {
			result = String.valueOf(Math.round(dataSize));
		} else {
			DecimalFormat decimalFormat = new DecimalFormat("#.##");
			result = decimalFormat.format(dataSize);
		}
		return prefix + ": " + result + " " + unit;
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

				if (minutesAndAMPM.endsWith(MagicInfoConstant.AM)) {
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
				return formatDataSize(prefix, dataSize / 1024, MagicInfoConstant.MB);
			} else {
				return formatDataSize(prefix, dataSize / (1024 * 1024), MagicInfoConstant.GB);
			}
		} catch (NumberFormatException e) {
			return MagicInfoConstant.NONE;
		}
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
