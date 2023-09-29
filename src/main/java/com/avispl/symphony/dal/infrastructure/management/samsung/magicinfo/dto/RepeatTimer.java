/*
 *  Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.samsung.magicinfo.dto;

/**
 * RepeatTimer
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 9/26/2023
 * @since 1.0.0
 */
public class RepeatTimer {
	private boolean scrSafeChanged;
	private boolean mntAutoChanged;
	private boolean autoSourceChanged;
	private boolean pixelShiftChanged;
	private boolean webBrowserChanged;
	private String scrSafeTimer;
	private String scrSafeMode;
	private String scrSafePeriod;
	private String scrSafeTime;

	public RepeatTimer(boolean scrSafeChanged, boolean mntAutoChanged, boolean autoSourceChanged, boolean pixelShiftChanged, boolean webBrowserChanged, String scrSafeTimer, String scrSafeMode,
			String scrSafePeriod, String scrSafeTime) {
		this.scrSafeChanged = scrSafeChanged;
		this.mntAutoChanged = mntAutoChanged;
		this.autoSourceChanged = autoSourceChanged;
		this.pixelShiftChanged = pixelShiftChanged;
		this.webBrowserChanged = webBrowserChanged;
		this.scrSafeTimer = scrSafeTimer;
		this.scrSafeMode = scrSafeMode;
		this.scrSafePeriod = scrSafePeriod;
		this.scrSafeTime = scrSafeTime;
	}

	public RepeatTimer() {
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
	 * Retrieves {@link #scrSafePeriod}
	 *
	 * @return value of {@link #scrSafePeriod}
	 */
	public String getScrSafePeriod() {
		return scrSafePeriod;
	}

	/**
	 * Sets {@link #scrSafePeriod} value
	 *
	 * @param scrSafePeriod new value of {@link #scrSafePeriod}
	 */
	public void setScrSafePeriod(String scrSafePeriod) {
		this.scrSafePeriod = scrSafePeriod;
	}

	/**
	 * Retrieves {@link #scrSafeTime}
	 *
	 * @return value of {@link #scrSafeTime}
	 */
	public String getScrSafeTime() {
		return scrSafeTime;
	}

	/**
	 * Sets {@link #scrSafeTime} value
	 *
	 * @param scrSafeTime new value of {@link #scrSafeTime}
	 */
	public void setScrSafeTime(String scrSafeTime) {
		this.scrSafeTime = scrSafeTime;
	}
}
