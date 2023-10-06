/*
 *  Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.samsung.magicinfo.common.picture;

/**
 * Enum representing different color tone settings.
 * Each enum constant has a name and a corresponding numeric value.
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 9/8/2023
 * @since 1.0.0
 */
public enum ColorToneEnum {
	OFF("Off", "80"),
	COOL("Cool", "1"),
	STANDARD("Standard", "2"),
	WARM1("Warm 1", "3"),
	WARM2("Warm 2", "4"),
	NATURAL("Natural", "5"),
	;
	private final String name;
	private final String value;

	/**
	 * Constructor for ColorToneEnum.
	 *
	 * @param name  The name representing the color tone setting.
	 * @param value The numeric value representing the color tone setting.
	 */
	ColorToneEnum(String name, String value) {
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
