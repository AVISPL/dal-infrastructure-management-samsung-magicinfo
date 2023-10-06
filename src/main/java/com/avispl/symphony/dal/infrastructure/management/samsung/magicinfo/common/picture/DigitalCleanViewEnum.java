/*
 *  Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.samsung.magicinfo.common.picture;

/**
 * Enum representing different settings for digital clean view.
 * Each enum constant has a name and a corresponding numeric value.
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 9/8/2023
 * @since 1.0.0
 */
public enum DigitalCleanViewEnum {
	ON("On", "1"),
	OFF("Off", "0"),
	MEDIUM("Medium", "2"),
	HIGH("High", "3"),
	AUTO("Auto", "4"),
	AUTO_VISUALIZATION("Auto Visualization", "5"),
	;
	private final String name;
	private final String value;

	/**
	 * Constructor for DigitalCleanViewEnum.
	 *
	 * @param name  The name representing the digital clean view setting.
	 * @param value The numeric value representing the digital clean view setting.
	 */
	DigitalCleanViewEnum(String name, String value) {
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
