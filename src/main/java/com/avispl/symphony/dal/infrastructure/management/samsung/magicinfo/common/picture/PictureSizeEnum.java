/*
 *  Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.samsung.magicinfo.common.picture;

/**
 * Enum representing different picture size settings.
 * Each enum constant has a name and a corresponding numeric value.
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 9/8/2023
 * @since 1.0.0
 */
public enum PictureSizeEnum {
	STANDARD("16:9 Standard", "1"),
	ZOOM("Zoom", "4"),
	ZOOM1("Zoom 1", "5"),
	ZOOM2("Zoom 2", "6"),
	FILL_TO_SCREEN("Fill to screen", "9"),
	NATURAL("4:3", "11"),
	WIDE_FIT("Wide Fit", "12"),
	WIDE_ZOOM("Wide Zoom", "49"),
	CUSTOM("Custom", "13"),
	SMART_VIEW_1("Smart View 1", "14"),
	SMART_VIEW_2("Smart View 2", "15"),
	ORIGINAL_RATIO("Original Ratio", "32"),
	;
	private final String name;
	private final String value;

	/**
	 * Constructor for PictureSizeEnum.
	 *
	 * @param name  The name representing the picture size setting.
	 * @param value The numeric value representing the picture size setting.
	 */
	PictureSizeEnum(String name, String value) {
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
