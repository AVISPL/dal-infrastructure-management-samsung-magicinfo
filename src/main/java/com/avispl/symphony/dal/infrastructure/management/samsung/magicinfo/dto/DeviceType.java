/*
 *  Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.samsung.magicinfo.dto;

/**
 * A data transfer object (DTO) class representing information about a device type.
 * This class stores attributes related to the device's type, version, and full type.
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 9/14/2023
 * @since 1.0.0
 */
public class DeviceType {
	private String deviceFullType;
	private String deviceTypeVersion;
	private String deviceType;

	/**
	 * Retrieves {@link #deviceFullType}
	 *
	 * @return value of {@link #deviceFullType}
	 */
	public String getDeviceFullType() {
		return deviceFullType;
	}

	/**
	 * Sets {@link #deviceFullType} value
	 *
	 * @param deviceFullType new value of {@link #deviceFullType}
	 */
	public void setDeviceFullType(String deviceFullType) {
		this.deviceFullType = deviceFullType;
	}

	/**
	 * Retrieves {@link #deviceTypeVersion}
	 *
	 * @return value of {@link #deviceTypeVersion}
	 */
	public String getDeviceTypeVersion() {
		return deviceTypeVersion;
	}

	/**
	 * Sets {@link #deviceTypeVersion} value
	 *
	 * @param deviceTypeVersion new value of {@link #deviceTypeVersion}
	 */
	public void setDeviceTypeVersion(String deviceTypeVersion) {
		this.deviceTypeVersion = deviceTypeVersion;
	}

	/**
	 * Retrieves {@link #deviceType}
	 *
	 * @return value of {@link #deviceType}
	 */
	public String getDeviceType() {
		return deviceType;
	}

	/**
	 * Sets {@link #deviceType} value
	 *
	 * @param deviceType new value of {@link #deviceType}
	 */
	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}
}
