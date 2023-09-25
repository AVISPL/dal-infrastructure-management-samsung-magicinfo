/*
 *  Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.samsung.magicinfo.common.picture;

/**
 * Enum representing different HDMI black level settings.
 * Each enum constant has a name and a corresponding numeric value.
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 9/12/2023
 * @since 1.0.0
 */
public enum HDMIBlackLevelEnum {
	NORMAL("Normal", "0"),
	LOW("Low", "1"),
	AUTO("Auto", "2"),
			;
	private final String name;
	private final String value;

	/**
	 * Constructor for HDMIBlackLevelEnum.
	 *
	 * @param name  The name representing the HDMI black level setting.
	 * @param value The numeric value representing the HDMI black level setting.
	 */
	HDMIBlackLevelEnum(String name, String value) {
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
