package com.avispl.symphony.dal.infrastructure.management.samsung.magicinfo.common;

public enum QuickControl {
	POWER("Power", "power"),
	DISPLAY_PANEL("DisplayPanel", ""),
	RESTART("Restart", ""),
	SOURCE("Source", ""),
	VOLUME("Volume", ""),
	MUTE("Mute", ""),
	;
	private final String name;
	private final String fieldName;

	/**
	 * Constructor for DisplayInfo.
	 *
	 * @param name The name representing the display information setting.
	 * @param fieldName The field name for reference.
	 */
	QuickControl(String name, String fieldName) {
		this.name = name;
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
	 * Retrieves {@link #fieldName}
	 *
	 * @return value of {@link #fieldName}
	 */
	public String getFieldName() {
		return fieldName;
	}
}
