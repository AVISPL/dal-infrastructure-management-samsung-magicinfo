/*
 *  Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.samsung.magicinfo.common.sound;

/**
 * Enum representing different sound modes.
 * Each enum constant has a name and a corresponding numeric value.
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 9/8/2023
 * @since 1.0.0
 */public enum SoundModeEnum {
	STANDARD("Standard", "0"),
	MUSIC("Music", "1"),
	MOVIE("Movie", "2"),
	CLEAR_VOICE("Clear Voice", "3"),
	CUSTOM("Custom", "4"),
	AMPLIFY("Amplify", "5"),
	ADAPTIVE_SOUND("Adaptive Sound", "7"),
	;
	private final String name;
	private final String value;

	/**
	 * Constructor for SoundModeEnum.
	 *
	 * @param name  The name representing the sound mode.
	 * @param value The numeric value representing the sound mode.
	 */
	SoundModeEnum(String name, String value) {
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
