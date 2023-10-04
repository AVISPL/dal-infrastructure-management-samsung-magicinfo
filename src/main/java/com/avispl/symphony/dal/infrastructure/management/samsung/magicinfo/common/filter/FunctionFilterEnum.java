/*
 *  Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.samsung.magicinfo.common.filter;

/**
 * Enum representing function filters used for filtering devices.
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 9/28/2023
 * @since 1.0.0
 */
public enum FunctionFilterEnum {
	VIDEO_WALL("Video Wall", "is_videowall"),
			;
	private final String name;
	private final String value;

	/**
	 * Constructor for WebBrowserIntervalEnum.
	 *
	 * @param name  The name representing the interval.
	 * @param value The numeric value representing the interval.
	 */
	FunctionFilterEnum(String name, String value) {
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
