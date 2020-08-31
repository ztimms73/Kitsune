package org.xtimms.kitsune.core.storage.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;

public class AppSettings {

	@Nullable
	private static WeakReference<AppSettings> sInstanceReference = null;

	private final SharedPreferences mPreferences;
	public final ReaderSettings readerSettings;
	public final ShelfSettings shelfSettings;

	@SuppressWarnings("deprecation")
	private AppSettings(Context context) {
		mPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
		readerSettings = new ReaderSettings(mPreferences);
		shelfSettings = new ShelfSettings(mPreferences);
	}

	public static AppSettings get(Context context) {
		AppSettings instance = sInstanceReference == null ? null : sInstanceReference.get();
		if (instance == null) {
			instance = new AppSettings(context);
			sInstanceReference = new WeakReference<>(instance);
		}
		return instance;
	}

	public boolean isUseTor() {
		return mPreferences.getBoolean("use_tor", false);
	}

	public String getAppLocale() {
		return mPreferences.getString("lang", "");
	}

	public int getAppTheme() {
		try {
			return Integer.parseInt(mPreferences.getString("theme", "0"));
		} catch (Exception e) {
			return 0;
		}
	}
}
