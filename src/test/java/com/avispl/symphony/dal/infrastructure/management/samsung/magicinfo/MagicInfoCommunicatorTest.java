/*
 *  Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.samsung.magicinfo;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
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
		magicInfoCommunicator.setLogin("");
		magicInfoCommunicator.setPassword("");
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
	 * Unit test for the {@code getAggregatorData()} method.
	 * It asserts the size of the statistics map to be 7.
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

	@Test
	void testGetMultipleStatistics() throws Exception {
		magicInfoCommunicator.getMultipleStatistics();
		magicInfoCommunicator.retrieveMultipleStatistics();
		Thread.sleep(30000);
		List<AggregatedDevice> aggregatedDeviceList = magicInfoCommunicator.retrieveMultipleStatistics();
		Assert.assertEquals(2, aggregatedDeviceList.size());
	}

	@Test
	void testFiltering() throws Exception {
		magicInfoCommunicator.setFilterDeviceType("S6PLAYER,S10PLAYER");
		magicInfoCommunicator.setFilterSource("");
		magicInfoCommunicator.setFilterFunction("");
		magicInfoCommunicator.getMultipleStatistics();
		magicInfoCommunicator.retrieveMultipleStatistics();
		Thread.sleep(30000);
		List<AggregatedDevice> aggregatedDeviceList = magicInfoCommunicator.retrieveMultipleStatistics();
		Assert.assertEquals(2, aggregatedDeviceList.size());
	}

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

	@Test
	void testTextControl() throws Exception {
		magicInfoCommunicator.getMultipleStatistics();
		magicInfoCommunicator.retrieveMultipleStatistics();
		Thread.sleep(30000);
		magicInfoCommunicator.retrieveMultipleStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();
		String property = MagicInfoConstant.MAINTENANCE_GROUP.concat("MaxTime(minute)");
		String value = "15";
		String deviceId = "a0-d0-5b-b2-e8-91";  //LFD_1
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		controllableProperty.setDeviceId(deviceId);
		magicInfoCommunicator.controlProperty(controllableProperty);
	}

	@Test
	void testText() throws Exception {
		magicInfoCommunicator.getMultipleStatistics();
		magicInfoCommunicator.retrieveMultipleStatistics();
		Thread.sleep(30000);
		magicInfoCommunicator.retrieveMultipleStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();
		String property = MagicInfoConstant.SCREEN_BURN_PROTECTION_GROUP.concat("Timer");
		String value = "Repeat";
		String deviceId = "5c-49-7d-17-3c-81";
		controllableProperty.setProperty(property);
		controllableProperty.setValue(value);
		controllableProperty.setDeviceId(deviceId);
		magicInfoCommunicator.controlProperty(controllableProperty);
	}

	@Test
	void testReset() throws Exception {
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
	}
}
