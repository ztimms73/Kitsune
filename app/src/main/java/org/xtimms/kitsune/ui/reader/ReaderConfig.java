package org.xtimms.kitsune.ui.reader;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

@SuppressWarnings("deprecation")
public class ReaderConfig {

    public final int brightnessValue;

    private ReaderConfig(SharedPreferences prefs) {
        brightnessValue = prefs.getInt("reader.brightness_value", 20);
    }

    public static ReaderConfig load(Context context) {
        return new ReaderConfig(
                PreferenceManager.getDefaultSharedPreferences(context)
        );
    }

}
