/*
 *  Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.samsung.magicinfo.common;

import java.util.Arrays;
import java.util.Optional;

/**
 * Enum representing various display information settings in the context of Samsung MagicInfo.
 * Each enum constant has a name, a group to which it belongs, and a field name for reference.
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 9/8/2023
 * @since 1.0.0
 */
public enum DisplayInfo {
	POWER("Power", MagicInfoConstant.EMPTY, "power"),
	DISPLAY_PANEL("DisplayPanel", MagicInfoConstant.DISPLAY_GENERAL_GROUP, "basicPanelStatus"),
	SOURCE("Source", MagicInfoConstant.DISPLAY_GENERAL_GROUP, "basicSource"),
	WEB_BROWSER_INTERVAL("WebBrowserRefreshInterval", MagicInfoConstant.DISPLAY_GENERAL_GROUP, "webBrowserInterval"),
	WEB_BROWSER_ZOOM("WebBrowserZoom", MagicInfoConstant.DISPLAY_GENERAL_GROUP, "webBrowserZoom"),
	WEB_BROWSER_HOME_PAGE("WebBrowserHomePage", MagicInfoConstant.DISPLAY_GENERAL_GROUP, "webBrowserHomepage"),
	WEB_BROWSER_PAGE_URL("WebBrowserPageURL", MagicInfoConstant.DISPLAY_GENERAL_GROUP, "webBrowserPageurl"),
	VOLUME("Volume", MagicInfoConstant.DISPLAY_GENERAL_GROUP, "basicVolume"),
	MUTE("Mute", MagicInfoConstant.DISPLAY_GENERAL_GROUP, "basicMute"),
	PANEL_ON_TIME("PanelOnTime(hour)", MagicInfoConstant.DISPLAY_GENERAL_GROUP, "diagnosisPanelOnTime"),
	TEMPERATURE_CONTROL("TemperatureControl(C)", MagicInfoConstant.TEMPERATURE_GROUP, "diagnosisAlarmTemperature"),
	LAMP_CONTROL("LampControl(%)", MagicInfoConstant.PICTURE_VIDEO, "mntManual"),
	CONTRAST("Contrast(%)", MagicInfoConstant.PICTURE_VIDEO, "pvContrast"),
	BRIGHTNESS("Brightness(%)", MagicInfoConstant.PICTURE_VIDEO, "pvBrightness"),
	SHARPNESS("Sharpness(%)", MagicInfoConstant.PICTURE_VIDEO, "pvSharpness"),
	COLOR("Color(%)", MagicInfoConstant.PICTURE_VIDEO, "pvColor"),
	TINT("Tint(G/R)(%)", MagicInfoConstant.PICTURE_VIDEO, "pvTint"),
	COLOR_TONE("ColorTone", MagicInfoConstant.PICTURE_VIDEO, "pvColortone"),
	COLOR_TEMPERATURE("ColorTemperature", MagicInfoConstant.PICTURE_VIDEO, "pvColorTemperature"),
	PICTURE_SIZE("PictureSize", MagicInfoConstant.PICTURE_VIDEO, "pvSize"),
	DIGITAL_CLEAN_VIEW("DigitalCleanView", MagicInfoConstant.PICTURE_VIDEO, "pvDigitalnr"),
	FILM_MODE("FilmMode", MagicInfoConstant.PICTURE_VIDEO, "pvFilmmode"),
	HDMI_BLACK_LEVEL("HDMIBlackLevel", MagicInfoConstant.PICTURE_VIDEO, "pvHdmiBlackLevel"),
	LED_PICTURE_SIZE("LEDPictureSize", MagicInfoConstant.PICTURE_PC, "ledPictureSize"),
	LED_HDR("LED HDR", MagicInfoConstant.PICTURE_VIDEO, "ledHdr"),
	SOUND_MODE("Mode", MagicInfoConstant.SOUND, "soundMode"),
	PICTURE_ENHANCER("PictureEnhancer", MagicInfoConstant.ADVANCED_SETTING, "pictureEnhancer"),
	MAX_POWER_SAVING("MaxPowerSaving", MagicInfoConstant.ADVANCED_SETTING, "maxPowerSaving"),
	AUTO_POWER_ON("AutoPowerOn", MagicInfoConstant.ADVANCED_SETTING, "advancedAutoPower"),
	REMOTE_CONFIGURATION("RemoteConfiguration", MagicInfoConstant.ADVANCED_SETTING, "networkStandbyMode"),
	AUTO_SOURCE_SWITCHING("AutoSourceSwitching", MagicInfoConstant.ADVANCED_SETTING, ""),
	RESTORE_PRIMARY_SOURCE("RestorePrimarySource", MagicInfoConstant.ADVANCED_SETTING, ""),
	PRIMARY_SOURCE("PrimarySource", MagicInfoConstant.ADVANCED_SETTING, ""),
	SECONDARY_SOURCE("SecondarySource", MagicInfoConstant.ADVANCED_SETTING, ""),
	SCREEN_LAMP_SCHEDULE("ScreenLampSchedule", MagicInfoConstant.MAINTENANCE_GROUP, "mntAutoIsEnable"),
	MAX_TIME_HOUR("MaxTime(hour)", MagicInfoConstant.MAINTENANCE_GROUP, "mntAutoMaxTime"),
	MAX_TIME_MINUTE("MaxTime(minute)", MagicInfoConstant.MAINTENANCE_GROUP, "mntAutoMaxTime"),
	MAX_VALUE("MaxValue", MagicInfoConstant.MAINTENANCE_GROUP, "mntAutoMaxValue"),
	MIN_TIME_HOUR("MinTime(hour)", MagicInfoConstant.MAINTENANCE_GROUP, "mntAutoMinTime"),
	MIN_TIME_MINUTE("MinTime(minute)", MagicInfoConstant.MAINTENANCE_GROUP, "mntAutoMinTime"),
	MIN_VALUE("MinValue", MagicInfoConstant.MAINTENANCE_GROUP, "mntAutoMinValue"),
	IMMEDIATE_DISPLAY("ImmediateDisplay", MagicInfoConstant.SCREEN_BURN_PROTECTION_GROUP, "mntSafetyScreenRun"),
	TIMER("Timer", MagicInfoConstant.SCREEN_BURN_PROTECTION_GROUP, "scrSafeTimer"),
	PIXEL_SHIFT("PixelShift", MagicInfoConstant.SCREEN_BURN_PROTECTION_GROUP, "pixelShiftEnable"),
	PIXEL_SHIFT_VERTICAL("PixelShiftVertical", MagicInfoConstant.SCREEN_BURN_PROTECTION_GROUP, "pixelShiftV"),
	PIXEL_SHIFT_HORIZONTAL("PixelShiftHorizontal", MagicInfoConstant.SCREEN_BURN_PROTECTION_GROUP, "pixelShiftH"),
	PIXEL_SHIFT_TIME("PixelShiftTime(minute)", MagicInfoConstant.SCREEN_BURN_PROTECTION_GROUP, "pixelShiftTime"),
	TIMER_MODE("TimerMode", MagicInfoConstant.SCREEN_BURN_PROTECTION_GROUP, "scrSafeMode"),
	TIMER_PERIOD("TimerPeriod(hour)", MagicInfoConstant.SCREEN_BURN_PROTECTION_GROUP, "scrSafePeriod"),
	TIMER_TIME("TimerTime(sec)", MagicInfoConstant.SCREEN_BURN_PROTECTION_GROUP, "scrSafePeriod"),
	TIMER_START_TIME_MIN("TimerStartTime(minute)", MagicInfoConstant.SCREEN_BURN_PROTECTION_GROUP, "scrSafeStartTime"),
	TIMER_START_TIME_HOUR("TimerStartTime(hour)", MagicInfoConstant.SCREEN_BURN_PROTECTION_GROUP, "scrSafeStartTime"),
	TIMER_END_TIME_MIN("TimerEndTime(minute)", MagicInfoConstant.SCREEN_BURN_PROTECTION_GROUP, "scrSafeEndTime"),
	TIMER_END_TIME_HOUR("TimerEndTime(hour)", MagicInfoConstant.SCREEN_BURN_PROTECTION_GROUP, "scrSafeEndTime"),
	;
	private final String name;
	private final String group;
	private final String fieldName;

	/**
	 * Constructor for DisplayInfo.
	 *
	 * @param name The name representing the display information setting.
	 * @param group The group to which the setting belongs.
	 * @param fieldName The field name for reference.
	 */
	DisplayInfo(String name, String group, String fieldName) {
		this.name = name;
		this.group = group;
		this.fieldName = fieldName;
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
	 * Retrieves {@link #group}
	 *
	 * @return value of {@link #group}
	 */
	public String getGroup() {
		return group;
	}

	/**
	 * Retrieves {@link #fieldName}
	 *
	 * @return value of {@link #fieldName}
	 */
	public String getFieldName() {
		return fieldName;
	}

	/**
	 * Returns the {@link DisplayInfo} enum constant with the specified name.
	 *
	 * @param name the name of the DisplayInfo constant to retrieve
	 * @return the DisplayInfo constant with the specified name
	 * @throws IllegalStateException if no constant with the specified name is found
	 */
	public static DisplayInfo getByName(String name) {
		Optional<DisplayInfo> property = Arrays.stream(DisplayInfo.values()).filter(group -> group.getName().equals(name)).findFirst();
		if (property.isPresent()) {
			return property.get();
		} else {
			throw new IllegalStateException(String.format("control group %s is not supported.", name));
		}
	}
}
