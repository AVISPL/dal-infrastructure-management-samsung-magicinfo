/*
 *  Copyright (c) 2023 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.samsung.magicinfo.dto;

/**
 * Represents a configuration for WebBrowserUrl
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 9/19/2023
 * @since 1.0.0
 */
public class WebBrowserUrl {
	private boolean mntAutoChanged;
	private boolean scrSafeChanged;
	private boolean pixelShiftChanged;
	private boolean autoSourceChanged;
	private String webBrowserInterval;
	private String webBrowserZoom;
	private String webBrowserHomepage;
	private String webBrowserPageurl;
	private boolean webBrowserChanged;

	/**
	 * Constructs a {@code WebBrowserUrl} object with specified initial values.
	 *
	 * @param mntAutoChanged Indicates whether maintenance auto settings have changed.
	 * @param scrSafeChanged Indicates whether screen safety settings have changed.
	 * @param pixelShiftChanged Indicates whether pixel shift settings have changed.
	 * @param autoSourceChanged Indicates whether auto source settings have changed.
	 * @param webBrowserInterval The interval setting for web browsing.
	 * @param webBrowserZoom The zoom setting for web browsing.
	 * @param webBrowserHomepage The homepage URL for the web browser.
	 * @param webBrowserPageurl The page URL for the web browser.
	 * @param webBrowserChanged Indicates whether web browser settings have changed.
	 */
	public WebBrowserUrl(boolean mntAutoChanged, boolean scrSafeChanged, boolean pixelShiftChanged, boolean autoSourceChanged, String webBrowserInterval, String webBrowserZoom,
			String webBrowserHomepage,
			String webBrowserPageurl, boolean webBrowserChanged) {
		this.mntAutoChanged = mntAutoChanged;
		this.scrSafeChanged = scrSafeChanged;
		this.pixelShiftChanged = pixelShiftChanged;
		this.autoSourceChanged = autoSourceChanged;
		this.webBrowserInterval = webBrowserInterval;
		this.webBrowserZoom = webBrowserZoom;
		this.webBrowserHomepage = webBrowserHomepage;
		this.webBrowserPageurl = webBrowserPageurl;
		this.webBrowserChanged = webBrowserChanged;
	}

	/**
	 * Constructs an empty {@code WebBrowserUrl} object with default values.
	 * Use setters to configure individual web browser-related properties as needed.
	 */
	public WebBrowserUrl() {
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
	 * Retrieves {@link #webBrowserInterval}
	 *
	 * @return value of {@link #webBrowserInterval}
	 */
	public String getWebBrowserInterval() {
		return webBrowserInterval;
	}

	/**
	 * Sets {@link #webBrowserInterval} value
	 *
	 * @param webBrowserInterval new value of {@link #webBrowserInterval}
	 */
	public void setWebBrowserInterval(String webBrowserInterval) {
		this.webBrowserInterval = webBrowserInterval;
	}

	/**
	 * Retrieves {@link #webBrowserZoom}
	 *
	 * @return value of {@link #webBrowserZoom}
	 */
	public String getWebBrowserZoom() {
		return webBrowserZoom;
	}

	/**
	 * Sets {@link #webBrowserZoom} value
	 *
	 * @param webBrowserZoom new value of {@link #webBrowserZoom}
	 */
	public void setWebBrowserZoom(String webBrowserZoom) {
		this.webBrowserZoom = webBrowserZoom;
	}

	/**
	 * Retrieves {@link #webBrowserHomepage}
	 *
	 * @return value of {@link #webBrowserHomepage}
	 */
	public String getWebBrowserHomepage() {
		return webBrowserHomepage;
	}

	/**
	 * Sets {@link #webBrowserHomepage} value
	 *
	 * @param webBrowserHomepage new value of {@link #webBrowserHomepage}
	 */
	public void setWebBrowserHomepage(String webBrowserHomepage) {
		this.webBrowserHomepage = webBrowserHomepage;
	}

	/**
	 * Retrieves {@link #webBrowserPageurl}
	 *
	 * @return value of {@link #webBrowserPageurl}
	 */
	public String getWebBrowserPageurl() {
		return webBrowserPageurl;
	}

	/**
	 * Sets {@link #webBrowserPageurl} value
	 *
	 * @param webBrowserPageurl new value of {@link #webBrowserPageurl}
	 */
	public void setWebBrowserPageurl(String webBrowserPageurl) {
		this.webBrowserPageurl = webBrowserPageurl;
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
}
