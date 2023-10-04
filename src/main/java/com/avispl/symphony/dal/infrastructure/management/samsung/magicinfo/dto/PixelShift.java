/*
 *  Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.samsung.magicinfo.dto;

/**
 * Represents a configuration for PixelShift
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 9/22/2023
 * @since 1.0.0
 */
public class PixelShift {
	private boolean pixelShiftChanged;
	private boolean autoSourceChanged;
	private boolean mntAutoChanged;
	private boolean scrSafeChanged;
	private boolean webBrowserChanged;
	private String pixelShiftEnable;
	private String pixelShiftH;
	private String pixelShiftV;
	private String pixelShiftTime;

	public PixelShift(boolean pixelShiftChanged, boolean autoSourceChanged, boolean mntAutoChanged, boolean scrSafeChanged, boolean webBrowserChanged, String pixelShiftEnable, String pixelShiftH,
			String pixelShiftV, String pixelShiftTime) {
		this.pixelShiftChanged = pixelShiftChanged;
		this.autoSourceChanged = autoSourceChanged;
		this.mntAutoChanged = mntAutoChanged;
		this.scrSafeChanged = scrSafeChanged;
		this.webBrowserChanged = webBrowserChanged;
		this.pixelShiftEnable = pixelShiftEnable;
		this.pixelShiftH = pixelShiftH;
		this.pixelShiftV = pixelShiftV;
		this.pixelShiftTime = pixelShiftTime;
	}

	public PixelShift() {
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
	 * Retrieves {@link #pixelShiftEnable}
	 *
	 * @return value of {@link #pixelShiftEnable}
	 */
	public String getPixelShiftEnable() {
		return pixelShiftEnable;
	}

	/**
	 * Sets {@link #pixelShiftEnable} value
	 *
	 * @param pixelShiftEnable new value of {@link #pixelShiftEnable}
	 */
	public void setPixelShiftEnable(String pixelShiftEnable) {
		this.pixelShiftEnable = pixelShiftEnable;
	}

	/**
	 * Retrieves {@link #pixelShiftH}
	 *
	 * @return value of {@link #pixelShiftH}
	 */
	public String getPixelShiftH() {
		return pixelShiftH;
	}

	/**
	 * Sets {@link #pixelShiftH} value
	 *
	 * @param pixelShiftH new value of {@link #pixelShiftH}
	 */
	public void setPixelShiftH(String pixelShiftH) {
		this.pixelShiftH = pixelShiftH;
	}

	/**
	 * Retrieves {@link #pixelShiftV}
	 *
	 * @return value of {@link #pixelShiftV}
	 */
	public String getPixelShiftV() {
		return pixelShiftV;
	}

	/**
	 * Sets {@link #pixelShiftV} value
	 *
	 * @param pixelShiftV new value of {@link #pixelShiftV}
	 */
	public void setPixelShiftV(String pixelShiftV) {
		this.pixelShiftV = pixelShiftV;
	}

	/**
	 * Retrieves {@link #pixelShiftTime}
	 *
	 * @return value of {@link #pixelShiftTime}
	 */
	public String getPixelShiftTime() {
		return pixelShiftTime;
	}

	/**
	 * Sets {@link #pixelShiftTime} value
	 *
	 * @param pixelShiftTime new value of {@link #pixelShiftTime}
	 */
	public void setPixelShiftTime(String pixelShiftTime) {
		this.pixelShiftTime = pixelShiftTime;
	}
}
