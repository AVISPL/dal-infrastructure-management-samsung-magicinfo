package com.avispl.symphony.dal.infrastructure.management.samsung.magicinfo.dto;

/**
 * AutoSourceSwitching
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 9/21/2023
 * @since 1.0.0
 */
public class AutoSourceSwitching {
	private boolean autoSourceChanged;
	private boolean mntAutoChanged;
	private boolean scrSafeChanged;
	private boolean pixelShiftChanged;
	private boolean webBrowserChanged;
	private String autoSourceSwitching;
	private String autoSourceRecovery;
	private String autoSourcePrimary;
	private String autoSourceSecondary;

	public AutoSourceSwitching(boolean autoSourceChanged, boolean mntAutoChanged, boolean scrSafeChanged, boolean pixelShiftChanged, boolean webBrowserChanged, String autoSourceSwitching,
			String autoSourceRecovery, String autoSourcePrimary, String autoSourceSecondary) {
		this.autoSourceChanged = autoSourceChanged;
		this.mntAutoChanged = mntAutoChanged;
		this.scrSafeChanged = scrSafeChanged;
		this.pixelShiftChanged = pixelShiftChanged;
		this.webBrowserChanged = webBrowserChanged;
		this.autoSourceSwitching = autoSourceSwitching;
		this.autoSourceRecovery = autoSourceRecovery;
		this.autoSourcePrimary = autoSourcePrimary;
		this.autoSourceSecondary = autoSourceSecondary;
	}

	public AutoSourceSwitching() {
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
	 * Retrieves {@link #autoSourceSwitching}
	 *
	 * @return value of {@link #autoSourceSwitching}
	 */
	public String getAutoSourceSwitching() {
		return autoSourceSwitching;
	}

	/**
	 * Sets {@link #autoSourceSwitching} value
	 *
	 * @param autoSourceSwitching new value of {@link #autoSourceSwitching}
	 */
	public void setAutoSourceSwitching(String autoSourceSwitching) {
		this.autoSourceSwitching = autoSourceSwitching;
	}

	/**
	 * Retrieves {@link #autoSourceRecovery}
	 *
	 * @return value of {@link #autoSourceRecovery}
	 */
	public String getAutoSourceRecovery() {
		return autoSourceRecovery;
	}

	/**
	 * Sets {@link #autoSourceRecovery} value
	 *
	 * @param autoSourceRecovery new value of {@link #autoSourceRecovery}
	 */
	public void setAutoSourceRecovery(String autoSourceRecovery) {
		this.autoSourceRecovery = autoSourceRecovery;
	}

	/**
	 * Retrieves {@link #autoSourcePrimary}
	 *
	 * @return value of {@link #autoSourcePrimary}
	 */
	public String getAutoSourcePrimary() {
		return autoSourcePrimary;
	}

	/**
	 * Sets {@link #autoSourcePrimary} value
	 *
	 * @param autoSourcePrimary new value of {@link #autoSourcePrimary}
	 */
	public void setAutoSourcePrimary(String autoSourcePrimary) {
		this.autoSourcePrimary = autoSourcePrimary;
	}

	/**
	 * Retrieves {@link #autoSourceSecondary}
	 *
	 * @return value of {@link #autoSourceSecondary}
	 */
	public String getAutoSourceSecondary() {
		return autoSourceSecondary;
	}

	/**
	 * Sets {@link #autoSourceSecondary} value
	 *
	 * @param autoSourceSecondary new value of {@link #autoSourceSecondary}
	 */
	public void setAutoSourceSecondary(String autoSourceSecondary) {
		this.autoSourceSecondary = autoSourceSecondary;
	}
}
