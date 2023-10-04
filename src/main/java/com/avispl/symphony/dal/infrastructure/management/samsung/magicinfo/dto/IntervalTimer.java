/*
 *  Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.samsung.magicinfo.dto;

/**
 * Represents a configuration for Interval Timer.
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 9/26/2023
 * @since 1.0.0
 */
public class IntervalTimer {
	private boolean scrSafeChanged;
	private boolean mntAutoChanged;
	private boolean autoSourceChanged;
	private boolean pixelShiftChanged;
	private boolean webBrowserChanged;
	private String scrSafeTimer;
	private String scrSafeMode;
	private String scrSafeStartTime;
	private String scrSafeEndTime;

	public IntervalTimer(boolean scrSafeChanged, boolean mntAutoChanged, boolean autoSourceChanged, boolean pixelShiftChanged, boolean webBrowserChanged, String scrSafeTimer, String scrSafeMode,
			String scrSafeStartTime, String scrSafeEndTime) {
		this.scrSafeChanged = scrSafeChanged;
		this.mntAutoChanged = mntAutoChanged;
		this.autoSourceChanged = autoSourceChanged;
		this.pixelShiftChanged = pixelShiftChanged;
		this.webBrowserChanged = webBrowserChanged;
		this.scrSafeTimer = scrSafeTimer;
		this.scrSafeMode = scrSafeMode;
		this.scrSafeStartTime = scrSafeStartTime;
		this.scrSafeEndTime = scrSafeEndTime;
	}

	public IntervalTimer() {
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
	 * Retrieves {@link #scrSafeTimer}
	 *
	 * @return value of {@link #scrSafeTimer}
	 */
	public String getScrSafeTimer() {
		return scrSafeTimer;
	}

	/**
	 * Sets {@link #scrSafeTimer} value
	 *
	 * @param scrSafeTimer new value of {@link #scrSafeTimer}
	 */
	public void setScrSafeTimer(String scrSafeTimer) {
		this.scrSafeTimer = scrSafeTimer;
	}

	/**
	 * Retrieves {@link #scrSafeMode}
	 *
	 * @return value of {@link #scrSafeMode}
	 */
	public String getScrSafeMode() {
		return scrSafeMode;
	}

	/**
	 * Sets {@link #scrSafeMode} value
	 *
	 * @param scrSafeMode new value of {@link #scrSafeMode}
	 */
	public void setScrSafeMode(String scrSafeMode) {
		this.scrSafeMode = scrSafeMode;
	}

	/**
	 * Retrieves {@link #scrSafeStartTime}
	 *
	 * @return value of {@link #scrSafeStartTime}
	 */
	public String getScrSafeStartTime() {
		return scrSafeStartTime;
	}

	/**
	 * Sets {@link #scrSafeStartTime} value
	 *
	 * @param scrSafeStartTime new value of {@link #scrSafeStartTime}
	 */
	public void setScrSafeStartTime(String scrSafeStartTime) {
		this.scrSafeStartTime = scrSafeStartTime;
	}

	/**
	 * Retrieves {@link #scrSafeEndTime}
	 *
	 * @return value of {@link #scrSafeEndTime}
	 */
	public String getScrSafeEndTime() {
		return scrSafeEndTime;
	}

	/**
	 * Sets {@link #scrSafeEndTime} value
	 *
	 * @param scrSafeEndTime new value of {@link #scrSafeEndTime}
	 */
	public void setScrSafeEndTime(String scrSafeEndTime) {
		this.scrSafeEndTime = scrSafeEndTime;
	}
}
