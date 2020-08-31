package org.xtimms.kitsune.ui.tools.settings;

import android.app.Activity;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import androidx.annotation.Nullable;

import org.xtimms.kitsune.R;

@SuppressWarnings("ALL")
public final class BackupSettingsFragment extends PreferenceFragment {

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.pref_backup);
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Activity activity = getActivity();
		Preference preference = findPreference("backup");
		Preference.OnPreferenceClickListener onPreferenceClickListener = (Preference.OnPreferenceClickListener) activity;
		preference.setOnPreferenceClickListener(onPreferenceClickListener);
		findPreference("restore").setOnPreferenceClickListener(onPreferenceClickListener);
	}

}
