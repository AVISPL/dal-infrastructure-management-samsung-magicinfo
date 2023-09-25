/*
 *  Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.samsung.magicinfo.common.general;

/**
 * Enum representing different display source options.
 * Each enum constant has a name and a corresponding numeric value.
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 9/8/2023
 * @since 1.0.0
 */
public enum SourceEnum {
	PC("PC", "20"),
	BNC("BNC", "30"),
	DVI("DVI", "24"),
	AV("AV", "12"),
	S_VIDEO("S-Video", "4"),
	COMPONENT("Component", "8"),
	MAGIC_INFO("MagicInfo", "32"),
	MAGIC_INFO_LITE("MagicInfo-Lite/S", "96"),
	PLUG_IN_MODULE("Plug In Module", "80"),
	HDMI1("HDMI1", "33"),
	HDMI2("HDMI2", "35"),
	HDMI3("HDMI3", "49"),
	HDMI4("HDMI4", "51"),
	HDMI1_PC("HDMI1_PC", "34"),
	HDMI2_PC("HDMI2_PC", "36"),
	HDMI3_PC("HDMI3_PC", "50"),
	HDMI4_PC("HDMI4_PC", "52"),
	DISPLAY_PORT("Display_Port", "37"),
	DISPLAY_PORT2("Display_Port2", "38"),
	ATV("ATV", "48"),
	DTV("DTV", "64"),
	DVI_VIDEO("DVI_VIDEO", "31"),
	AV2("AV2", "13"),
	EXT("Ext", "14"),
	HD_BASE_T("HDBaseT", "85"),
	WI_DI("WiDi", "97"),
	WEB_BROWSER("WebBrowser", "101"),
	URL_LAUNCHER("URL Launcher", "99"),
	KIOSK("KIOSK", "103"),
	;
	private final String name;
	private final String value;

	/**
	 * Constructor for SourceEnum.
	 *
	 * @param name  The name representing the source option.
	 * @param value The numeric value representing the source option.
	 */
	SourceEnum(String name, String value) {
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
