/*
 *  Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.samsung.magicinfo.common.picture;

/**
 * Enum representing different film mode settings.
 * Each enum constant has a name and a corresponding numeric value.
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 9/8/2023
 * @since 1.0.0
 */
public enum FilmModeEnum {
	OFF("Off", "0"),
	AUTO1("Auto1", "1"),
	AUTO2("Auto2", "2"),
	CINEMA_SMOOTH("Cinema Smooth", "3"),
	;
	private final String name;
	private final String value;

	/**
	 * Constructor for FilmModeEnum.
	 *
	 * @param name  The name representing the film mode setting.
	 * @param value The numeric value representing the film mode setting.
	 */
	FilmModeEnum(String name, String value) {
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
