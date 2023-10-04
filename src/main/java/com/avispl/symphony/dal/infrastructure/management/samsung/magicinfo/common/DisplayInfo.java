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
	DISPLAY_PANEL("DisplayPanel", MagicInfoConstant.DISPLAY_CONTROLS_GROUP, "basicPanelStatus",false),
	SOURCE("Source", MagicInfoConstant.DISPLAY_CONTROLS_GROUP, "basicSource",false),
	WEB_BROWSER_INTERVAL("WebBrowserRefreshInterval", MagicInfoConstant.DISPLAY_CONTROLS_GROUP, "webBrowserInterval",true),
	WEB_BROWSER_ZOOM("WebBrowserZoom", MagicInfoConstant.DISPLAY_CONTROLS_GROUP, "webBrowserZoom",true),
	WEB_BROWSER_HOME_PAGE("WebBrowserHomePage", MagicInfoConstant.DISPLAY_CONTROLS_GROUP, "webBrowserHomepage",true),
	WEB_BROWSER_PAGE_URL("WebBrowserPageURL", MagicInfoConstant.DISPLAY_CONTROLS_GROUP, "webBrowserPageurl",true),
	VOLUME("Volume", MagicInfoConstant.DISPLAY_CONTROLS_GROUP, "basicVolume",false),
	MUTE("Mute", MagicInfoConstant.DISPLAY_CONTROLS_GROUP, "basicMute",false),
	POWER("Power", MagicInfoConstant.DISPLAY_CONTROLS_GROUP, "",false),
	RESTART("Restart", MagicInfoConstant.DISPLAY_CONTROLS_GROUP, "",false),
	TEMPERATURE_CONTROL("TemperatureControl(C)", MagicInfoConstant.TEMPERATURE_GROUP, "diagnosisAlarmTemperature",false),
	LAMP_CONTROL("LampControl(%)", MagicInfoConstant.PICTURE_VIDEO, "mntManual",false),
	CONTRAST("Contrast(%)", MagicInfoConstant.PICTURE_VIDEO, "pvContrast",false),
	BRIGHTNESS("Brightness(%)", MagicInfoConstant.PICTURE_VIDEO, "pvBrightness",false),
	SHARPNESS("Sharpness(%)", MagicInfoConstant.PICTURE_VIDEO, "pvSharpness",false),
	COLOR("Color(%)", MagicInfoConstant.PICTURE_VIDEO, "pvColor",false),
	TINT("Tint(G/R)(%)", MagicInfoConstant.PICTURE_VIDEO, "pvTint",false),
	COLOR_TONE("ColorTone", MagicInfoConstant.PICTURE_VIDEO, "pvColortone",false),
	COLOR_TEMPERATURE("ColorTemperature", MagicInfoConstant.PICTURE_VIDEO, "pvColorTemperature",false),
	PICTURE_SIZE("PictureSize", MagicInfoConstant.PICTURE_VIDEO, "pvSize",false),
	DIGITAL_CLEAN_VIEW("DigitalCleanView", MagicInfoConstant.PICTURE_VIDEO, "pvDigitalnr",false),
	FILM_MODE("FilmMode", MagicInfoConstant.PICTURE_VIDEO, "pvFilmmode",false),
	HDMI_BLACK_LEVEL("HDMIBlackLevel", MagicInfoConstant.PICTURE_VIDEO, "pvHdmiBlackLevel",false),
	SOUND_MODE("Mode", MagicInfoConstant.SOUND, "soundMode",false),
	PICTURE_ENHANCER("PictureEnhancer", MagicInfoConstant.ADVANCED_SETTING, "pictureEnhancer",false),
	MAX_POWER_SAVING("MaxPowerSaving", MagicInfoConstant.ADVANCED_SETTING, "maxPowerSaving",false),
	AUTO_POWER_ON("AutoPowerOn", MagicInfoConstant.ADVANCED_SETTING, "advancedAutoPower",false),
	REMOTE_CONFIGURATION("RemoteConfiguration", MagicInfoConstant.ADVANCED_SETTING, "networkStandbyMode",false),
	AUTO_SOURCE_SWITCHING("AutoSourceSwitching", MagicInfoConstant.ADVANCED_SETTING, "autoSourceSwitching",true),
	RESTORE_PRIMARY_SOURCE("RestorePrimarySource", MagicInfoConstant.ADVANCED_SETTING, "autoSourceRecovery",false),
	PRIMARY_SOURCE("PrimarySource", MagicInfoConstant.ADVANCED_SETTING, "autoSourcePrimary",false),
	SECONDARY_SOURCE("SecondarySource", MagicInfoConstant.ADVANCED_SETTING, "autoSourceSecondary",false),
	RESET_SOUND("ResetSound", MagicInfoConstant.SOUND, "advancedReset",false),
	RESET_PICTURE("ResetPicture", MagicInfoConstant.PICTURE_VIDEO, "advancedReset",false),
	SCREEN_LAMP_SCHEDULE("ScreenLampSchedule", MagicInfoConstant.MAINTENANCE_GROUP, "mntAutoIsEnable",true),
	MAX_TIME_HOUR("MaxTime(hour)", MagicInfoConstant.MAINTENANCE_GROUP, "mntAutoMaxTime",true),
	MAX_TIME_MINUTE("MaxTime(minute)", MagicInfoConstant.MAINTENANCE_GROUP, "mntAutoMaxTime",true),
	MAX_VALUE("MaxValue", MagicInfoConstant.MAINTENANCE_GROUP, "mntAutoMaxValue",true),
	MIN_TIME_HOUR("MinTime(hour)", MagicInfoConstant.MAINTENANCE_GROUP, "mntAutoMinTime",true),
	MIN_TIME_MINUTE("MinTime(minute)", MagicInfoConstant.MAINTENANCE_GROUP, "mntAutoMinTime",true),
	MIN_VALUE("MinValue", MagicInfoConstant.MAINTENANCE_GROUP, "mntAutoMinValue",true),
	IMMEDIATE_DISPLAY("ImmediateDisplay", MagicInfoConstant.SCREEN_BURN_PROTECTION_GROUP, "mntSafetyScreenRun",false),
	TIMER("Timer", MagicInfoConstant.SCREEN_BURN_PROTECTION_GROUP, "scrSafeTimer",true),
	PIXEL_SHIFT("PixelShift", MagicInfoConstant.SCREEN_BURN_PROTECTION_GROUP, "pixelShiftEnable",true),
	PIXEL_SHIFT_VERTICAL("PixelShiftVertical", MagicInfoConstant.SCREEN_BURN_PROTECTION_GROUP, "pixelShiftV",true),
	PIXEL_SHIFT_HORIZONTAL("PixelShiftHorizontal", MagicInfoConstant.SCREEN_BURN_PROTECTION_GROUP, "pixelShiftH",true),
	PIXEL_SHIFT_TIME("PixelShiftTime(minute)", MagicInfoConstant.SCREEN_BURN_PROTECTION_GROUP, "pixelShiftTime",true),
	TIMER_MODE("TimerMode", MagicInfoConstant.SCREEN_BURN_PROTECTION_GROUP, "scrSafeMode",false),
	TIMER_PERIOD("TimerPeriod(hour)", MagicInfoConstant.SCREEN_BURN_PROTECTION_GROUP, "scrSafePeriod",true),
	TIMER_TIME("TimerTime(sec)", MagicInfoConstant.SCREEN_BURN_PROTECTION_GROUP, "scrSafeTime",true),
	TIMER_START_TIME_MIN("TimerStartTime(minute)", MagicInfoConstant.SCREEN_BURN_PROTECTION_GROUP, "scrSafeStartTime",true),
	TIMER_START_TIME_HOUR("TimerStartTime(hour)", MagicInfoConstant.SCREEN_BURN_PROTECTION_GROUP, "scrSafeStartTime",true),
	TIMER_END_TIME_MIN("TimerEndTime(minute)", MagicInfoConstant.SCREEN_BURN_PROTECTION_GROUP, "scrSafeEndTime",true),
	TIMER_END_TIME_HOUR("TimerEndTime(hour)", MagicInfoConstant.SCREEN_BURN_PROTECTION_GROUP, "scrSafeEndTime",true),
	;
	private final String name;
	private final String group;
	private final String fieldName;
	private final boolean isObject;

	/**
	 * Constructor for DisplayInfo.
	 *
	 * @param name The name representing the display information setting.
	 * @param group The group to which the setting belongs.
	 * @param fieldName The field name for reference.
	 */
	DisplayInfo(String name, String group, String fieldName, boolean isObject) {
		this.name = name;
		this.group = group;
		this.fieldName = fieldName;
		this.isObject = isObject;
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
	 * Retrieves {@link #isObject}
	 *
	 * @return value of {@link #isObject}
	 */
	public boolean isObject() {
		return isObject;
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
