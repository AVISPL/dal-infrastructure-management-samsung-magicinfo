/*
 *  Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.samsung.magicinfo.common.picture;

/**
 * Enum representing different zoom levels for a web browser.
 * Each enum constant has a name and a corresponding numeric value.
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 9/8/2023
 * @since 1.0.0
 */
public enum ColorTemperatureEnum {
	VALUE1("2800", "253"),
	VALUE2("3000", "254"),
	VALUE3("3500", "35"),
	VALUE4("4000", "255"),
	VALUE5("4500", "45"),
	VALUE6("5000", "0"),
	VALUE7("5500", "55"),
	VALUE8("6000", "1"),
	VALUE9("6500", "65"),
	VALUE10("7000", "2"),
	VALUE11("7500", "75"),
	VALUE12("8000", "3"),
	VALUE13("8500", "85"),
	VALUE14("9000", "4"),
	VALUE15("9500", "95"),
	VALUE16("10000", "5"),
	VALUE17("10500", "105"),
	VALUE18("11000", "6"),
	VALUE19("11500", "115"),
	VALUE20("12000", "7"),
	VALUE21("12500", "125"),
	VALUE22("13000", "8"),
	VALUE23("13500", "135"),
	VALUE24("14000", "9"),
	VALUE25("14500", "145"),
	VALUE26("15000", "16"),
	VALUE27("15500", "155"),
	VALUE28("16000", "160"),
	;
	private final String name;
	private final String value;

	/**
	 * Constructor for WebBrowserZoomEnum.
	 *
	 * @param name  The name representing the zoom level.
	 * @param value The numeric value representing the zoom level.
	 */
	ColorTemperatureEnum(String name, String value) {
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
