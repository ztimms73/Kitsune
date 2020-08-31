package org.xtimms.kitsune.ui.tools.settings;

import android.app.Activity;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import androidx.annotation.Nullable;

import org.xtimms.kitsune.R;

@SuppressWarnings("ALL")
public class AdvancedSettingsFragment extends PreferenceFragment {

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Activity activity = getActivity();
        Preference.OnPreferenceClickListener onPreferenceClickListener = (Preference.OnPreferenceClickListener) activity;
        Preference b = findPreference("bugreport");
        //Preference h = findPreference("csearchhist");
        b.setOnPreferenceClickListener(onPreferenceClickListener);
        //h.setSummary(getString(R.string.items_, SearchHistoryAdapter.getHistorySize(activity)));
        //h.setOnPreferenceClickListener(onPreferenceClickListener);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_advanced);
    }
}
