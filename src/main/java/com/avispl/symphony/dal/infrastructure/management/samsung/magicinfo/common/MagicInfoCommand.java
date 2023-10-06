/*
 *  Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.samsung.magicinfo.common;

/**
 * A class that provides constants for various MagicInfo REST API commands.
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 9/8/2023
 * @since 1.0.0
 */
public class MagicInfoCommand {
	public static final String AUTH_COMMAND = "MagicInfo/restapi/v2.0/auth";
	public static final String DEVICE_DASHBOARD = "MagicInfo/restapi/v2.0/ems/dashboard/devices";
	public static final String DEVICE_TYPE_COMMAND = "MagicInfo/restapi/v2.0/rms/devices/device-types";
	public static final String FILTERING_COMMAND = "MagicInfo/restapi/v2.0/rms/devices/filter";
	public static final String ALL_DEVICES_COMMAND = "MagicInfo/restapi/v2.0/rms/devices?pageSize=1000&startIndex=1";
	public static final String GENERAL_INFO_COMMAND = "MagicInfo/restapi/v2.0/rms/devices/general-info";
	public static final String DISPLAY_INFO_COMMAND = "MagicInfo/restapi/v2.0/rms/devices/display-info";
	public static final String CHECK_CONNECTION_COMMAND = "MagicInfo/restapi/v2.0/rms/devices/connections-checked";
	public static final String UPDATE_DISPLAY_COMMAND = "MagicInfo/restapi/v2.0/rms/devices/current-display-info";
	public static final String QUICK_CONTROL_COMMAND = "MagicInfo/restapi/v2.0/rms/devices/quick-control";
}
