/*
 *  Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.samsung.magicinfo.dto;

/**
 * Represents a configuration for Maintenance
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 9/20/2023
 * @since 1.0.0
 */
public class Maintenance {
	private boolean mntAutoChanged;
	private boolean scrSafeChanged;
	private boolean pixelShiftChanged;
	private boolean autoSourceChanged;
	private boolean webBrowserChanged;
	private String mntAutoIsEnable;
	private String mntAutoMaxTime;
	private String mntAutoMaxValue;
	private String mntAutoMinTime;
	private String mntAutoMinValue;

	/**
	 * Constructs a {@code Maintenance} object with specified initial values.
	 *
	 * @param mntAutoChanged Indicates whether maintenance auto settings have changed.
	 * @param scrSafeChanged Indicates whether screen safety settings have changed.
	 * @param pixelShiftChanged Indicates whether pixel shift settings have changed.
	 * @param autoSourceChanged Indicates whether auto source settings have changed.
	 * @param webBrowserChanged Indicates whether web browser settings have changed.
	 * @param mntAutoIsEnable Indicates whether maintenance auto is enabled.
	 * @param mntAutoMaxTime The maximum time for maintenance auto.
	 * @param mntAutoMaxValue The maximum value for maintenance auto.
	 * @param mntAutoMinTime The minimum time for maintenance auto.
	 * @param mntAutoMinValue The minimum value for maintenance auto.
	 */
	public Maintenance(boolean mntAutoChanged, boolean scrSafeChanged, boolean pixelShiftChanged, boolean autoSourceChanged, boolean webBrowserChanged, String mntAutoIsEnable, String mntAutoMaxTime,
			String mntAutoMaxValue, String mntAutoMinTime, String mntAutoMinValue) {
		this.mntAutoChanged = mntAutoChanged;
		this.scrSafeChanged = scrSafeChanged;
		this.pixelShiftChanged = pixelShiftChanged;
		this.autoSourceChanged = autoSourceChanged;
		this.webBrowserChanged = webBrowserChanged;
		this.mntAutoIsEnable = mntAutoIsEnable;
		this.mntAutoMaxTime = mntAutoMaxTime;
		this.mntAutoMaxValue = mntAutoMaxValue;
		this.mntAutoMinTime = mntAutoMinTime;
		this.mntAutoMinValue = mntAutoMinValue;
	}

	/**
	 * Constructs an empty {@code Maintenance} object with default values.
	 * Use setters to configure individual maintenance-related properties as needed.
	 */
	public Maintenance() {
	}

	/**
	 * Retrieves {@link #mntAutoChanged}
	 *
	 * @return value of {@link #mntAutoChanged}
	 */
	public boolean isMntAutoChanged() {
		return mntAutoChanged;
	}

	/**
	 * Sets {@link #mntAutoChanged} value
	 *
	 * @param mntAutoChanged new value of {@link #mntAutoChanged}
	 */
	public void setMntAutoChanged(boolean mntAutoChanged) {
		this.mntAutoChanged = mntAutoChanged;
	}

	/**
	 * Retrieves {@link #scrSafeChanged}
	 *
	 * @return value of {@link #scrSafeChanged}
	 */
	public boolean isScrSafeChanged() {
		return scrSafeChanged;
	}

	/**
	 * Sets {@link #scrSafeChanged} value
	 *
	 * @param scrSafeChanged new value of {@link #scrSafeChanged}
	 */
	public void setScrSafeChanged(boolean scrSafeChanged) {
		this.scrSafeChanged = scrSafeChanged;
	}

	/**
	 * Retrieves {@link #pixelShiftChanged}
	 *
	 * @return value of {@link #pixelShiftChanged}
	 */
	public boolean isPixelShiftChanged() {
		return pixelShiftChanged;
	}

	/**
	 * Sets {@link #pixelShiftChanged} value
	 *
	 * @param pixelShiftChanged new value of {@link #pixelShiftChanged}
	 */
	public void setPixelShiftChanged(boolean pixelShiftChanged) {
		this.pixelShiftChanged = pixelShiftChanged;
	}

	/**
	 * Retrieves {@link #autoSourceChanged}
	 *
	 * @return value of {@link #autoSourceChanged}
	 */
	public boolean isAutoSourceChanged() {
		return autoSourceChanged;
	}

	/**
	 * Sets {@link #autoSourceChanged} value
	 *
	 * @param autoSourceChanged new value of {@link #autoSourceChanged}
	 */
	public void setAutoSourceChanged(boolean autoSourceChanged) {
		this.autoSourceChanged = autoSourceChanged;
	}

	/**
	 * Retrieves {@link #webBrowserChanged}
	 *
	 * @return value of {@link #webBrowserChanged}
	 */
	public boolean isWebBrowserChanged() {
		return webBrowserChanged;
	}

	/**
	 * Sets {@link #webBrowserChanged} value
	 *
	 * @param webBrowserChanged new value of {@link #webBrowserChanged}
	 */
	public void setWebBrowserChanged(boolean webBrowserChanged) {
		this.webBrowserChanged = webBrowserChanged;
	}

	/**
	 * Retrieves {@link #mntAutoIsEnable}
	 *
	 * @return value of {@link #mntAutoIsEnable}
	 */
	public String getMntAutoIsEnable() {
		return mntAutoIsEnable;
	}

	/**
	 * Sets {@link #mntAutoIsEnable} value
	 *
	 * @param mntAutoIsEnable new value of {@link #mntAutoIsEnable}
	 */
	public void setMntAutoIsEnable(String mntAutoIsEnable) {
		this.mntAutoIsEnable = mntAutoIsEnable;
	}

	/**
	 * Retrieves {@link #mntAutoMaxTime}
	 *
	 * @return value of {@link #mntAutoMaxTime}
	 */
	public String getMntAutoMaxTime() {
		return mntAutoMaxTime;
	}

	/**
	 * Sets {@link #mntAutoMaxTime} value
	 *
	 * @param mntAutoMaxTime new value of {@link #mntAutoMaxTime}
	 */
	public void setMntAutoMaxTime(String mntAutoMaxTime) {
		this.mntAutoMaxTime = mntAutoMaxTime;
	}

	/**
	 * Retrieves {@link #mntAutoMaxValue}
	 *
	 * @return value of {@link #mntAutoMaxValue}
	 */
	public String getMntAutoMaxValue() {
		return mntAutoMaxValue;
	}

	/**
	 * Sets {@link #mntAutoMaxValue} value
	 *
	 * @param mntAutoMaxValue new value of {@link #mntAutoMaxValue}
	 */
	public void setMntAutoMaxValue(String mntAutoMaxValue) {
		this.mntAutoMaxValue = mntAutoMaxValue;
	}

	/**
	 * Retrieves {@link #mntAutoMinTime}
	 *
	 * @return value of {@link #mntAutoMinTime}
	 */
	public String getMntAutoMinTime() {
		return mntAutoMinTime;
	}

	/**
	 * Sets {@link #mntAutoMinTime} value
	 *
	 * @param mntAutoMinTime new value of {@link #mntAutoMinTime}
	 */
	public void setMntAutoMinTime(String mntAutoMinTime) {
		this.mntAutoMinTime = mntAutoMinTime;
	}

	/**
	 * Retrieves {@link #mntAutoMinValue}
	 *
	 * @return value of {@link #mntAutoMinValue}
	 */
	public String getMntAutoMinValue() {
		return mntAutoMinValue;
	}

	/**
	 * Sets {@link #mntAutoMinValue} value
	 *
	 * @param mntAutoMinValue new value of {@link #mntAutoMinValue}
	 */
	public void setMntAutoMinValue(String mntAutoMinValue) {
		this.mntAutoMinValue = mntAutoMinValue;
	}
}
