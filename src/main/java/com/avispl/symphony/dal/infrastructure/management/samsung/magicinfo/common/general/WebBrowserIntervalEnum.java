/*
 *  Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.samsung.magicinfo.common.general;

/**
 *  Enum representing different intervals for web browser actions.
 *  Each enum constant has a name and a corresponding numeric value.
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 9/8/2023
 * @since 1.0.0
 */
public enum WebBrowserIntervalEnum {
	OFF("Off", "0"),
	FIVE_MIN("5 min", "300"),
	TEN_MIN("10 min", "600"),
	FIFTEEN_MIN("15 min", "900"),
	THIRTY_MIN("30 min", "1800"),
	;
	private final String name;
	private final String value;

	/**
	 * Constructor for WebBrowserIntervalEnum.
	 *
	 * @param name  The name representing the interval.
	 * @param value The numeric value representing the interval.
	 */
	WebBrowserIntervalEnum(String name, String value) {
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
