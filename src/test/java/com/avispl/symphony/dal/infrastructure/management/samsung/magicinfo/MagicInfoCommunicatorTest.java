/*
 *  Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.samsung.magicinfo;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.net.SocketTimeoutException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.security.auth.login.FailedLoginException;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.avispl.symphony.api.dal.dto.control.AdvancedControllableProperty;
import com.avispl.symphony.api.dal.dto.control.ControllableProperty;
import com.avispl.symphony.api.dal.dto.monitor.ExtendedStatistics;
import com.avispl.symphony.api.dal.dto.monitor.aggregator.AggregatedDevice;
import com.avispl.symphony.dal.infrastructure.management.samsung.magicinfo.common.MagicInfoConstant;

/**
 * MagicInfoCommunicatorTest
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 9/8/2023
 * @since 1.0.0
 */
public class MagicInfoCommunicatorTest {
	private MagicInfoCommunicator magicInfoCommunicator;

	private ExtendedStatistics extendedStatistic;

	@BeforeEach
	void setUp() throws Exception {
		magicInfoCommunicator = new MagicInfoCommunicator();
		magicInfoCommunicator.setHost("misstaging.com");
		magicInfoCommunicator.setLogin("aviadmin");
		magicInfoCommunicator.setPassword("AVISPL@Team");
		magicInfoCommunicator.setPort(7001);
		magicInfoCommunicator.init();
		magicInfoCommunicator.connect();
	}

	@AfterEach
	void destroy() throws Exception {
		magicInfoCommunicator.disconnect();
		magicInfoCommunicator.destroy();
	}

	/**
	 * Unit test for the ping method.
	 *
	 * @throws Exception if an exception occurs during the test.
	 */
	@Test
	void testPing() throws Exception {
		destroy();
		magicInfoCommunicator.setHost("misstaging.com1");
		magicInfoCommunicator.setLogin("");
		magicInfoCommunicator.setPassword("");
		magicInfoCommunicator.setPort(7001);
		magicInfoCommunicator.init();
		magicInfoCommunicator.connect();
		assertThrows(SocketTimeoutException.class, () -> magicInfoCommunicator.ping(), "Socket connection timed out misstaging.com1");
	}

	/**
	 * Unit test for the login method.
	 *
	 * @throws Exception if an exception occurs during the test.
	 */
	@Test
	void testFailedLogin() throws Exception {
		destroy();
		magicInfoCommunicator.setHost("misstaging.com");
		magicInfoCommunicator.setLogin("aaa");
		magicInfoCommunicator.setPassword("aaa");
		magicInfoCommunicator.setPort(7001);
		magicInfoCommunicator.init();
		magicInfoCommunicator.connect();
		assertThrows(FailedLoginException.class, () -> magicInfoCommunicator.getMultipleStatistics().get(0), "Failed to retrieve an access token for account with from username and password. Please username id and password");
	}

	/**
	 * Unit test for the {@code getAggregatorData()} method.
	 * It asserts the size of the statistics map to be 4.
	 *
	 * @throws Exception if an exception occurs during the test.
	 */
	@Test
	void testGetAggregatorData() throws Exception {
		extendedStatistic = (ExtendedStatistics) magicInfoCommunicator.getMultipleStatistics().get(0);
		Map<String, String> statistics = extendedStatistic.getStatistics();
		List<AdvancedControllableProperty> advancedControllablePropertyList = extendedStatistic.getControllableProperties();
		Assert.assertEquals(4, statistics.size());
	}

	/**
	 * Test case for retrieving multiple statistics from MagicInfo Communicator.
	 *
	 * @throws Exception If an exception occurs during the test.
	 */
	@Test
	void testGetMultipleStatistics() throws Exception {
		magicInfoCommunicator.getMultipleStatistics();
		magicInfoCommunicator.retrieveMultipleStatistics();
		Thread.sleep(30000);
		List<AggregatedDevice> aggregatedDeviceList = magicInfoCommunicator.retrieveMultipleStatistics();
		Assert.assertEquals(2, aggregatedDeviceList.size());
	}

	@Test
	void testMonitoringAggregated() throws Exception {
		magicInfoCommunicator.getMultipleStatistics();
		magicInfoCommunicator.retrieveMultipleStatistics();
		Thread.sleep(30000);
		List<AggregatedDevice> aggregatedDeviceList = magicInfoCommunicator.retrieveMultipleStatistics();
		Assert.assertEquals(2, aggregatedDeviceList.size());
		Assert.assertEquals("CWSZH4LNA00032W", aggregatedDeviceList.get(1).getProperties().get("SerialKey"));
		Assert.assertEquals("MagicInfo_SPlayer_Tizen_4.0", aggregatedDeviceList.get(1).getProperties().get("PlayerVersion"));
		Assert.assertEquals("5c-49-7d-17-3c-81", aggregatedDeviceList.get(1).getProperties().get("MACAddress"));
		Assert.assertEquals("T-KTM2ELAKUC-2380.21;T-KTM2ELAKUS-1053", aggregatedDeviceList.get(1).getProperties().get("FirmwareVersion"));
		Assert.assertEquals("Internal: 3.9 GB", aggregatedDeviceList.get(1).getProperties().get("AvailableCapacity"));
		Assert.assertEquals("192.168.0.123", aggregatedDeviceList.get(1).getProperties().get("IP"));
	}

	/**
	 * Test case for filtering aggregated devices by device source.
	 *
	 * @throws Exception If an exception occurs during the test.
	 */
	@Test
	void testFiltering() throws Exception {
		magicInfoCommunicator.setFilterDeviceType("");
		magicInfoCommunicator.setFilterSource("MagicInfo-Lite/S,ss");
		magicInfoCommunicator.setFilterFunction("");
		magicInfoCommunicator.getMultipleStatistics();
		magicInfoCommunicator.retrieveMultipleStatistics();
		Thread.sleep(30000);
		List<AggregatedDevice> aggregatedDeviceList = magicInfoCommunicator.retrieveMultipleStatistics();
		Assert.assertEquals(2, aggregatedDeviceList.size());
	}

	/**
	 * Test case for filtering aggregated devices by device type.
	 *
	 * @throws Exception If an exception occurs during the test.
	 */
	@Test
	void testFilteringWithDeviceType() throws Exception {
		magicInfoCommunicator.setFilterDeviceType("S6PLAYER");
		magicInfoCommunicator.setFilterSource("");
		magicInfoCommunicator.setFilterFunction("");
		magicInfoCommunicator.getMultipleStatistics();
		magicInfoCommunicator.retrieveMultipleStatistics();
		Thread.sleep(30000);
		List<AggregatedDevice> aggregatedDeviceList = magicInfoCommunicator.retrieveMultipleStatistics();
		Assert.assertEquals(1, aggregatedDeviceList.size());
	}

	/**
	 * Test case for retrieving multiple statistics with historical data.
	 *
	 * @throws Exception If an exception occurs during the test.
	 */
	@Test
	void testGetMultipleStatisticsWithHistorical() throws Exception {
		magicInfoCommunicator.setHistoricalProperties("Temperature(C)");
		magicInfoCommunicator.getMultipleStatistics();
		magicInfoCommunicator.retrieveMultipleStatistics();
		Thread.sleep(30000);
		List<AggregatedDevice> aggregatedDeviceList = magicInfoCommunicator.retrieveMultipleStatistics();
		Assert.assertEquals(2, aggregatedDeviceList.size());
		Assert.assertEquals(1, aggregatedDeviceList.get(0).getDynamicStatistics().size());
	}

	/**
	 * Test case for controlling a property using MagicInfo Communicator.
	 *
	 * @throws Exception If an exception occurs during the test.
	 */
	@Test
	void testSwitchInMaintenanceGroup() throws Exception {
		magicInfoCommunicator.getMultipleStatistics();
		magicInfoCommunicator.retrieveMultipleStatistics();
		Thread.sleep(30000);
		magicInfoCommunicator.retrieveMultipleStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();
		String property = MagicInfoConstant.MAINTENANCE_GROUP.concat("ScreenLampSchedule");
		String value = "1";
		String deviceId = "5c-49-7d-17-3c-81";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		controllableProperty.setDeviceId(deviceId);
		magicInfoCommunicator.controlProperty(controllableProperty);

		List<AggregatedDevice> aggregatedDeviceList = magicInfoCommunicator.retrieveMultipleStatistics();
		Optional<AdvancedControllableProperty> advancedControllableProperty = aggregatedDeviceList.get(1).getControllableProperties().stream().filter(item ->
				property.equals(item.getName())).findFirst();
		Assert.assertEquals(value,advancedControllableProperty.get().getValue());
	}

	/**
	 * Test case for controlling a property using MagicInfo Communicator.
	 *
	 * @throws Exception If an exception occurs during the test.
	 */
	@Test
	void testDropDownControlInMaintenanceGroup() throws Exception {
		magicInfoCommunicator.getMultipleStatistics();
		magicInfoCommunicator.retrieveMultipleStatistics();
		Thread.sleep(30000);
		magicInfoCommunicator.retrieveMultipleStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();
		String property = MagicInfoConstant.MAINTENANCE_GROUP.concat("MinValue");
		String value = "90";
		String deviceId = "5c-49-7d-17-3c-81";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		controllableProperty.setDeviceId(deviceId);
		magicInfoCommunicator.controlProperty(controllableProperty);

		List<AggregatedDevice> aggregatedDeviceList = magicInfoCommunicator.retrieveMultipleStatistics();
		Optional<AdvancedControllableProperty> advancedControllableProperty = aggregatedDeviceList.get(1).getControllableProperties().stream().filter(item ->
				property.equals(item.getName())).findFirst();
		Assert.assertEquals(value,advancedControllableProperty.get().getValue());
	}

	/**
	 * Test case for performing a text operation using MagicInfo Communicator.
	 *
	 * @throws Exception If an exception occurs during the test.
	 */
	@Test
	void testDropDownControlInScreenBurnProtectionGroup() throws Exception {
		magicInfoCommunicator.getMultipleStatistics();
		magicInfoCommunicator.retrieveMultipleStatistics();
		Thread.sleep(20000);
		magicInfoCommunicator.retrieveMultipleStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();
		String property = MagicInfoConstant.SCREEN_BURN_PROTECTION_GROUP.concat("Timer");
		String value = "Interval";
		String deviceId = "5c-49-7d-17-3c-81";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		controllableProperty.setDeviceId(deviceId);
		magicInfoCommunicator.controlProperty(controllableProperty);

		List<AggregatedDevice> aggregatedDeviceList = magicInfoCommunicator.retrieveMultipleStatistics();
		Optional<AdvancedControllableProperty> advancedControllableProperty = aggregatedDeviceList.get(1).getControllableProperties().stream().filter(item ->
				property.equals(item.getName())).findFirst();
		Assert.assertEquals(value,advancedControllableProperty.get().getValue());
	}

	/**
	 * Test case for performing a text operation using MagicInfo Communicator.
	 *
	 * @throws Exception If an exception occurs during the test.
	 */
	@Test
	void testNumericControlInScreenBurnProtectionGroup() throws Exception {
		magicInfoCommunicator.getMultipleStatistics();
		magicInfoCommunicator.retrieveMultipleStatistics();
		Thread.sleep(20000);
		magicInfoCommunicator.retrieveMultipleStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();
		String property = MagicInfoConstant.SCREEN_BURN_PROTECTION_GROUP.concat("PixelShiftHorizontal");
		String value = "4";
		String deviceId = "5c-49-7d-17-3c-81";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		controllableProperty.setDeviceId(deviceId);
		magicInfoCommunicator.controlProperty(controllableProperty);

		List<AggregatedDevice> aggregatedDeviceList = magicInfoCommunicator.retrieveMultipleStatistics();
		Optional<AdvancedControllableProperty> advancedControllableProperty = aggregatedDeviceList.get(1).getControllableProperties().stream().filter(item ->
				property.equals(item.getName())).findFirst();
		Assert.assertEquals(value,advancedControllableProperty.get().getValue());
	}

	/**
	 * Test case for performing a text operation using MagicInfo Communicator.
	 *
	 * @throws Exception If an exception occurs during the test.
	 */
	@Test
	void testResetPicture() throws Exception {
		magicInfoCommunicator.getMultipleStatistics();
		magicInfoCommunicator.retrieveMultipleStatistics();
		Thread.sleep(20000);
		magicInfoCommunicator.retrieveMultipleStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();
		String property = MagicInfoConstant.PICTURE_VIDEO.concat("ResetPicture");
		String value = "1";
		String deviceId = "5c-49-7d-17-3c-81";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		controllableProperty.setDeviceId(deviceId);
		magicInfoCommunicator.controlProperty(controllableProperty);

		List<AggregatedDevice> aggregatedDeviceList = magicInfoCommunicator.retrieveMultipleStatistics();
		Optional<AdvancedControllableProperty> advancedControllableProperty = aggregatedDeviceList.get(1).getControllableProperties().stream().filter(item ->
				property.equals(item.getName())).findFirst();
		Assert.assertEquals(value,advancedControllableProperty.get().getValue());
	}

	/**
	 * Test case for performing a text operation using MagicInfo Communicator.
	 *
	 * @throws Exception If an exception occurs during the test.
	 */
	@Test
	void testNumericInPictureGroup() throws Exception {
		magicInfoCommunicator.getMultipleStatistics();
		magicInfoCommunicator.retrieveMultipleStatistics();
		Thread.sleep(20000);
		magicInfoCommunicator.retrieveMultipleStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();
		String property = MagicInfoConstant.PICTURE_VIDEO.concat("Contrast(%)");
		String value = "95";
		String deviceId = "5c-49-7d-17-3c-81";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		controllableProperty.setDeviceId(deviceId);
		magicInfoCommunicator.controlProperty(controllableProperty);

		List<AggregatedDevice> aggregatedDeviceList = magicInfoCommunicator.retrieveMultipleStatistics();
		Optional<AdvancedControllableProperty> advancedControllableProperty = aggregatedDeviceList.get(1).getControllableProperties().stream().filter(item ->
				property.equals(item.getName())).findFirst();
		Assert.assertEquals(value,advancedControllableProperty.get().getValue());
	}

	/**
	 * Test case for performing a text operation using MagicInfo Communicator.
	 *
	 * @throws Exception If an exception occurs during the test.
	 */
	@Test
	void testResetSound() throws Exception {
		magicInfoCommunicator.getMultipleStatistics();
		magicInfoCommunicator.retrieveMultipleStatistics();
		Thread.sleep(20000);
		magicInfoCommunicator.retrieveMultipleStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();
		String property = MagicInfoConstant.SOUND.concat("ResetSound");
		String value = "1";
		String deviceId = "5c-49-7d-17-3c-81";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		controllableProperty.setDeviceId(deviceId);
		magicInfoCommunicator.controlProperty(controllableProperty);
	}

	/**
	 * Test case for performing a text operation using MagicInfo Communicator.
	 *
	 * @throws Exception If an exception occurs during the test.
	 */
	@Test
	void testDropdownSoundMode() throws Exception {
		magicInfoCommunicator.getMultipleStatistics();
		magicInfoCommunicator.retrieveMultipleStatistics();
		Thread.sleep(20000);
		magicInfoCommunicator.retrieveMultipleStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();
		String property = MagicInfoConstant.SOUND.concat("Mode");
		String value = "Music";
		String deviceId = "5c-49-7d-17-3c-81";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		controllableProperty.setDeviceId(deviceId);
		magicInfoCommunicator.controlProperty(controllableProperty);

		List<AggregatedDevice> aggregatedDeviceList = magicInfoCommunicator.retrieveMultipleStatistics();
		Optional<AdvancedControllableProperty> advancedControllableProperty = aggregatedDeviceList.get(1).getControllableProperties().stream().filter(item ->
				property.equals(item.getName())).findFirst();
		Assert.assertEquals(value,advancedControllableProperty.get().getValue());
	}

	/**
	 * Test case for performing a text operation using MagicInfo Communicator.
	 *
	 * @throws Exception If an exception occurs during the test.
	 */
	@Test
	void testRestart() throws Exception {
		magicInfoCommunicator.getMultipleStatistics();
		magicInfoCommunicator.retrieveMultipleStatistics();
		Thread.sleep(20000);
		magicInfoCommunicator.retrieveMultipleStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();
		String property = MagicInfoConstant.DISPLAY_CONTROLS_GROUP.concat("Restart");
		String value = "1";
		String deviceId = "5c-49-7d-17-3c-81";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		controllableProperty.setDeviceId(deviceId);
		magicInfoCommunicator.controlProperty(controllableProperty);

		List<AggregatedDevice> aggregatedDeviceList = magicInfoCommunicator.retrieveMultipleStatistics();
		Optional<AdvancedControllableProperty> advancedControllableProperty = aggregatedDeviceList.get(1).getControllableProperties().stream().filter(item ->
				property.equals(item.getName())).findFirst();
		Assert.assertEquals(value,advancedControllableProperty.get().getValue());
	}

	/**
	 * Test case for performing a text operation using MagicInfo Communicator.
	 *
	 * @throws Exception If an exception occurs during the test.
	 */
	@Test
	void testPower() throws Exception {
		magicInfoCommunicator.getMultipleStatistics();
		magicInfoCommunicator.retrieveMultipleStatistics();
		Thread.sleep(20000);
		magicInfoCommunicator.retrieveMultipleStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();
		String property = MagicInfoConstant.SOUND.concat("Power");
		String value = "1";
		String deviceId = "5c-49-7d-17-3c-81";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		controllableProperty.setDeviceId(deviceId);
		magicInfoCommunicator.controlProperty(controllableProperty);

		List<AggregatedDevice> aggregatedDeviceList = magicInfoCommunicator.retrieveMultipleStatistics();
		Optional<AdvancedControllableProperty> advancedControllableProperty = aggregatedDeviceList.get(1).getControllableProperties().stream().filter(item ->
				property.equals(item.getName())).findFirst();
		Assert.assertEquals(value,advancedControllableProperty.get().getValue());
	}

	/**
	 * Test case for controlling a property using MagicInfo Communicator.
	 *
	 * @throws Exception If an exception occurs during the test.
	 */
	@Test
	void testSwitchAutoSourceSwitching() throws Exception {
		magicInfoCommunicator.getMultipleStatistics();
		magicInfoCommunicator.retrieveMultipleStatistics();
		Thread.sleep(30000);
		magicInfoCommunicator.retrieveMultipleStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();
		String property = MagicInfoConstant.ADVANCED_SETTING.concat("AutoSourceSwitching");
		String value = "1";
		String deviceId = "a0-d0-5b-b2-e8-91";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		controllableProperty.setDeviceId(deviceId);
		magicInfoCommunicator.controlProperty(controllableProperty);

		List<AggregatedDevice> aggregatedDeviceList = magicInfoCommunicator.retrieveMultipleStatistics();
		Optional<AdvancedControllableProperty> advancedControllableProperty = aggregatedDeviceList.stream()
				.filter(item -> deviceId.equals(item.getDeviceId()))
				.findFirst().get()
				.getControllableProperties().stream().filter(item ->
						property.equals(item.getName())).findFirst();
		Assert.assertEquals(value,advancedControllableProperty.get().getValue());
	}
}
