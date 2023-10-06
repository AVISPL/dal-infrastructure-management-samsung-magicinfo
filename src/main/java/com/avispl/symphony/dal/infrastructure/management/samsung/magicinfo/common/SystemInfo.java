/*
 *  Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.samsung.magicinfo.common;

/**
 * Enum representing various system information categories in the context of device status.
 * Each enum constant has a name and a corresponding value.
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 9/8/2023
 * @since 1.0.0
 */
public enum SystemInfo {
	CONNECTED("DevicesConnected", "connected"),
	DIS_CONNECTED("DevicesDisconnected", "disConnected"),
	WARNING("DevicesWithWarnings", "warning"),
	ERROR("DevicesInError", "error"),
	;
	private final String name;
	private final String value;

	/**
	 * Constructor for SystemInfo.
	 *
	 * @param name The name representing the system information category.
	 * @param value The corresponding value associated with the category.
	 */
	SystemInfo(String name, String value) {
		this.name = name;
		this.value = value;
	}

	/**
	 * Retrieves {@link #name}
	 *
	 * @return value of {@link #name}
	 */
	public String getName() {
		return name;
	}

	/**
	 * Retrieves {@link #value}
	 *
	 * @return value of {@link #value}
	 */
	public String getValue() {
		return value;
	}
}
