/*
 *  Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.samsung.magicinfo.common.picture;

/**
 * Enum representing different LED picture size settings.
 * Each enum constant has a name and a corresponding numeric value.
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 9/8/2023
 * @since 1.0.0
 */
public enum LEDPictureSizeEnum {
	NORMAL("Original", "0"),
	LOW("Custom", "1"),
	;
	private final String name;
	private final String value;

	/**
	 * Constructor for LEDPictureSizeEnum.
	 *
	 * @param name  The name representing the LED picture size setting.
	 * @param value The numeric value representing the LED picture size setting.
	 */
	LEDPictureSizeEnum(String name, String value) {
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
