/*
 *  Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.samsung.magicinfo.common.screen;

/**
 * Enum representing different interval modes for display settings.
 * Each enum constant has a name and a corresponding numeric value.
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 9/8/2023
 * @since 1.0.0
 */
public enum IntervalModeEnum {
	SCROLL("Scroll", "131"),
	BAR("Bar", "133"),
	ERASER("Eraser", "134"),
	PIXEL("Pixel", "132"),
	ROLLING_BAR("Rolling Bar", "144"),
	FADING_SCREEN("Fading Screen", "145"),
	;
	private final String name;
	private final String value;

	/**
	 * Constructor for IntervalModeEnum.
	 *
	 * @param name  The name representing the interval mode setting.
	 * @param value The numeric value representing the interval mode setting.
	 */
	IntervalModeEnum(String name, String value) {
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
