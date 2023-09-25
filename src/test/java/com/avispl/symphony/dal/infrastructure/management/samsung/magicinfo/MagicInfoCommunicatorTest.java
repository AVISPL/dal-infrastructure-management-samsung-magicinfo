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
		Assert.assertEquals(1, aggregatedDeviceList.size());
	}

	@Test
	void testFiltering() throws Exception {
		magicInfoCommunicator.setFilterDeviceType("S6PLAYER");
		magicInfoCommunicator.setFilterSource("HDMI1,AAA");
		magicInfoCommunicator.getMultipleStatistics();
		magicInfoCommunicator.retrieveMultipleStatistics();
		Thread.sleep(30000);
		List<AggregatedDevice> aggregatedDeviceList = magicInfoCommunicator.retrieveMultipleStatistics();
		Assert.assertEquals(0, aggregatedDeviceList.size());
	}
}
