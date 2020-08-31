package org.xtimms.kitsune.core.storage.settings;

import android.content.SharedPreferences;
import android.graphics.Color;
import androidx.annotation.ColorInt;

public final class ReaderSettings {

	private final SharedPreferences mPreferences;

	ReaderSettings(SharedPreferences preferences) {
		mPreferences = preferences;
	}

	public short getDefaultPreset() {
		try {
			return Short.parseShort(mPreferences.getString("reader.default_preset", "0"));
		} catch (Exception e) {
			return 0;
		}
	}

	public int getScaleMode() {
		return Integer.parseInt(this.mPreferences.getString("reader.scale_mode", "0"));
	}

	public int getToWidthScaleMode() {
		return Integer.parseInt(this.mPreferences.getString("reader.scale_mode", "1"));
	}

	public boolean isVolumeKeysEnabled() {
		return mPreferences.getBoolean("reader.volume_keys", true);
	}

	public boolean isWakelockEnabled() {
		return mPreferences.getBoolean("reader.wakelock", true);
	}

	public boolean isStatusBarEnbaled() {
		return mPreferences.getBoolean("reader.statusbar", true);
	}

	public boolean isCustomBackground() {
		return mPreferences.getBoolean("reader.background_apply", false);
	}

	public boolean isBrightnessEnabled() {
		return mPreferences.getBoolean("reader.brightness_adjust", false);
	}

	@ColorInt
	public int getBackgroundColor() {
		return mPreferences.getInt("reader.background", Color.BLACK);
	}
}
