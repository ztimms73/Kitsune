package org.xtimms.kitsune;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;

import com.yandex.metrica.YandexMetrica;
import com.yandex.metrica.YandexMetricaConfig;

import org.xtimms.kitsune.core.common.CrashHandler;
import org.xtimms.kitsune.core.common.ThumbSize;
import org.xtimms.kitsune.core.updchecker.JobSetupReceiver;
import org.xtimms.kitsune.utils.FileLogger;
import org.xtimms.kitsune.utils.ImageUtils;
import org.xtimms.kitsune.utils.ResourceUtils;
import org.xtimms.kitsune.utils.network.CookieStore;
import org.xtimms.kitsune.utils.network.NetworkUtils;
import org.xtimms.kitsune.core.storage.settings.AppSettings;

public final class KitsuneApp extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		// Creating an extended library configuration.
		String API_key = "82f8508e-e77e-4615-a461-76a9515ae2ad";
		YandexMetricaConfig config = YandexMetricaConfig.newConfigBuilder(API_key).build();
		// Initializing the AppMetrica SDK.
		YandexMetrica.activate(getApplicationContext(), config);
		// Automatic tracking of user activity.
		YandexMetrica.enableActivityAutoTracking(this);

		Resources resources = getResources();
		final float aspectRatio = 18f / 13f;
		ThumbSize.THUMB_SIZE_LIST = new ThumbSize(
				resources.getDimensionPixelSize(R.dimen.thumb_width_list),
				resources.getDimensionPixelSize(R.dimen.thumb_height_list)
		);
		ThumbSize.THUMB_SIZE_SMALL = new ThumbSize(
				resources.getDimensionPixelSize(R.dimen.thumb_width_small),
				aspectRatio
		);
		ThumbSize.THUMB_SIZE_MEDIUM = new ThumbSize(
				resources.getDimensionPixelSize(R.dimen.thumb_width_medium),
				aspectRatio
		);
		ThumbSize.THUMB_SIZE_LARGE = new ThumbSize(
				resources.getDimensionPixelSize(R.dimen.thumb_width_large),
				aspectRatio
		);

		CrashHandler mCrashHandler = new CrashHandler(this);
		Thread.setDefaultUncaughtExceptionHandler(mCrashHandler);
		final AppSettings settings = AppSettings.get(this);
		ImageUtils.init(this);
		FileLogger.init(this);
		CookieStore.getInstance().init(this);
		NetworkUtils.init(this, settings.isUseTor());
		ResourceUtils.setLocale(getResources(), settings.getAppLocale());
		@SuppressWarnings("deprecation") SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		// noinspection deprecation
		if (!prefs.getBoolean(PreferenceManager.KEY_HAS_SET_DEFAULT_VALUES, false)) {
			// noinspection deprecation
			PreferenceManager.setDefaultValues(this, R.xml.pref_general, true);
			// noinspection deprecation
			PreferenceManager.setDefaultValues(this, R.xml.pref_network, true);
			// noinspection deprecation
			PreferenceManager.setDefaultValues(this, R.xml.pref_reader, true);
			// noinspection deprecation
			PreferenceManager.setDefaultValues(this, R.xml.pref_backup, true);
			// noinspection deprecation
			PreferenceManager.setDefaultValues(this, R.xml.pref_sync, true);
			// noinspection deprecation
			PreferenceManager.setDefaultValues(this, R.xml.pref_advanced, true);
			//TODO other
			JobSetupReceiver.setup(this);
			// noinspection deprecation
			prefs.edit().putBoolean(PreferenceManager.KEY_HAS_SET_DEFAULT_VALUES, true).apply();
		}
	}

	public void restart() {
		final Intent intent = getBaseContext().getPackageManager()
				.getLaunchIntentForPackage(getBaseContext().getPackageName());
		assert intent != null;
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}

	@NonNull
	public static KitsuneApp from(Activity activity) {
		return (KitsuneApp) activity.getApplication();
	}
}
