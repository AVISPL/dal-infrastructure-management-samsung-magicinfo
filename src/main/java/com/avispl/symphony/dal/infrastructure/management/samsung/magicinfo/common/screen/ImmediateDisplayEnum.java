/*
 *  Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.samsung.magicinfo.common.screen;

/**
 * Enum representing different immediate display settings.
 * Each enum constant has a name and a corresponding numeric value.
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 9/8/2023
 * @since 1.0.0
 */
public enum ImmediateDisplayEnum {
	OFF("Off", "0"),
	SIGNAL_PATTERN("Signal Pattern", "1"),
	ALL_WHITE("All White", "2"),
	SCROLL("Scroll", "3"),
	BAR("Bar", "4"),
	ERASER("Eraser", "6"),
	PIXEL("Pixel", "7"),
	ROLLING_BAR("Rolling Bar", "16"),
	FADING_SCREEN("Fading Screen", "17"),
	;
	private final String name;
	private final String value;

	/**
	 * Constructor for ImmediateDisplayEnum.
	 *
	 * @param name  The name representing the immediate display setting.
	 * @param value The numeric value representing the immediate display setting.
	 */
	ImmediateDisplayEnum(String name, String value) {
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
