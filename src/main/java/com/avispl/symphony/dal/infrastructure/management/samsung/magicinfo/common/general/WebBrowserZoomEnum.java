/*
 *  Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.samsung.magicinfo.common.general;

/**
 * Enum representing different intervals for web browser actions.
 * Each enum constant has a name and a corresponding numeric value.
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 9/8/2023
 * @since 1.0.0
 */
public enum WebBrowserZoomEnum {
	FIFTY("50 %", "50"),
	SEVENTY_FIVE("75 %", "75"),
	ONE_HUNDRED("100 %", "100"),
	ONE_HUNDRED_TWENTY_FIVE("125 %", "125"),
	ONE_HUNDRED_FIFTY("150 %", "150"),
	TWO_HUNDRED("200 %", "200"),
	THREE_HUNDRED("300 %", "300"),
	;
	private final String name;
	private final String value;

	/**
	 * Constructor for WebBrowserIntervalEnum.
	 *
	 * @param name  The name representing the interval.
	 * @param value The numeric value representing the interval.
	 */
	WebBrowserZoomEnum(String name, String value) {
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
