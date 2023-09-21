/*
 *  Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.samsung.magicinfo.common;

/**
 * Enum representing various general information settings for a device.
 * Each enum constant has a descriptive name.
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 9/9/2023
 * @since 1.0.0
 */
public enum GeneralInfo {
	DEVICE_NAME("DeviceName"),
	MAC_ADDRESS("MACAddress"),
	IP("IP"),
	LOCATION("Location"),
	DEVICE_TYPE("DeviceType"),
	DEVICE_TYPE_VERSION("DeviceTypeVersion"),
	MAP_LOCATION("MapLocation"),
	LAST_CONNECTION_TIME("LastConnectionTime"),
	FIRMWARE_VERSION("FirmwareVersion"),
	OS_IMAGE_VERSION("OSImageVersion"),
	PLAYER_VERSION("PlayerVersion"),
	CPU("CPU"),
	MEMORY_SIZE("MemorySize(byte)"),
	STORAGE_SIZE("StorageSize(byte)"),
	VIDEO_CARD("VideoCard"),
	VIDEO_MEMORY("VideoMemory"),
	VIDEO_DRIVER("VideoDriver"),
	DISK_SPACE_USAGE("DiskSpaceUsage"),
	AVAILABLE_CAPACITY("AvailableCapacity"),
	APPROVAL_DATE("ApprovalDate"),
	SCREEN_SIZE("ScreenSize"),
	RESOLUTION("Resolution"),
	CODE("Code"),
	SERIAL_KEY("SerialKey"),
	;
	private final String name;

	/**
	 * Constructor for GeneralInfo.
	 *
	 * @param name The descriptive name representing the general information setting.
	 */
	GeneralInfo(String name) {
		this.name = name;
	}

	/**
	 * Retrieves {@link #name}
	 *
	 * @return value of {@link #name}
	 */
	public String getName() {
		return name;
	}
}
