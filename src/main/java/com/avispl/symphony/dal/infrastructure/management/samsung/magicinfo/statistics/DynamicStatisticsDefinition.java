/*
 * Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.samsung.magicinfo.statistics;

import com.avispl.symphony.dal.infrastructure.management.samsung.magicinfo.common.MagicInfoConstant;

/**
 * DynamicStatisticsDefinition is Enum representing dynamic statistics definitions.
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 9/8/2023
 * @since 1.0.0
 */
public enum DynamicStatisticsDefinition {
	TEMPERATURE("Temperature", MagicInfoConstant.EMPTY),
	;

	private final String name;
	private final String group;

	/**
	 * Constructs a DynamicStatisticsDefinition enum with the specified name.
	 *
	 * @param name The name of the dynamic statistic definition.
	 */
	DynamicStatisticsDefinition(final String name, final String group) {
		this.name = name;
		this.group = group;
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
}
