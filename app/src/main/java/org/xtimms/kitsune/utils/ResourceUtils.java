package org.xtimms.kitsune.utils;

import android.app.ActivityManager;
import android.app.Fragment;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;

import androidx.annotation.NonNull;
import androidx.annotation.RawRes;

import android.text.format.DateUtils;
import android.util.DisplayMetrics;
import android.view.View;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

@SuppressWarnings("deprecation")
public abstract class ResourceUtils {

	public static String getRawString(Resources resources, @RawRes int resId) {
		InputStream is = null;
		try {
			is = resources.openRawResource(resId);
			String myText;
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			int i = is.read();
			while (i != -1) {
				output.write(i);
				i = is.read();
			}
			myText = output.toString();
			return myText;
		} catch (IOException e) {
			return e.getMessage();
		} finally {
			try {
				if (is != null) {
					is.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static int dpToPx(Resources resources, float dp) {
		float density = resources.getDisplayMetrics().density;
		return (int) (dp * density);
	}

	public static boolean isTablet(Configuration configuration) {
		return configuration.smallestScreenWidthDp >= 600;
	}

	public static boolean isLandscape(Resources resources) {
		return resources.getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
	}

	public static void setLocale(Resources resources, String locale) {
		DisplayMetrics dm = resources.getDisplayMetrics();
		Configuration conf = resources.getConfiguration();
		conf.locale = android.text.TextUtils.isEmpty(locale) ? Locale.getDefault() : new Locale(locale);
		resources.updateConfiguration(conf, dm);
	}

	/**
	 * https://stackoverflow.com/questions/18635135/android-shortcut-bitmap-launcher-icon-size/19003905#19003905
	 */
	public static int getLauncherIconSize(Context context) {
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		assert activityManager != null;
		int size2 = activityManager.getLauncherLargeIconSize();
		int size1 = (int) context.getResources().getDimension(android.R.dimen.app_icon_size);
		return Math.max(size2, size1);
	}

	@NonNull
	public static String formatDateTime(Context context, long time) {
		Date date = new Date(time);
		DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(context.getApplicationContext());
		return dateFormat.format(date);
	}

	@NonNull
	public static String formatTimeRelative(long time) {
		return DateUtils.getRelativeTimeSpanString(time, System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
	}

	public static float obtainDensity(Context context) {
		return obtainDensity(context.getResources());
	}

	public static float obtainDensity(Resources resources) {
		return resources.getDisplayMetrics().density;
	}

	public static float obtainDensity(View view) {
		return obtainDensity(view.getResources());
	}

	public static float obtainDensity(Fragment fragment) {
		return obtainDensity(fragment.getResources());
	}

}
