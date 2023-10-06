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
	VALUE1("2800K", "253"),
	VALUE2("3000K", "254"),
	VALUE3("3500K", "35"),
	VALUE4("4000K", "255"),
	VALUE5("4500K", "45"),
	VALUE6("5000K", "0"),
	VALUE7("5500K", "55"),
	VALUE8("6000K", "1"),
	VALUE9("6500K", "65"),
	VALUE10("7000K", "2"),
	VALUE11("7500K", "75"),
	VALUE12("8000K", "3"),
	VALUE13("8500K", "85"),
	VALUE14("9000K", "4"),
	VALUE15("9500K", "95"),
	VALUE16("10000K", "5"),
	VALUE17("10500K", "105"),
	VALUE18("11000K", "6"),
	VALUE19("11500K", "115"),
	VALUE20("12000K", "7"),
	VALUE21("12500K", "125"),
	VALUE22("13000K", "8"),
	VALUE23("13500K", "135"),
	VALUE24("14000K", "9"),
	VALUE25("14500K", "145"),
	VALUE26("15000K", "16"),
	VALUE27("15500K", "155"),
	VALUE28("16000K", "160"),
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
