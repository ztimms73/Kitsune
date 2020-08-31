package org.xtimms.kitsune.ui.tools.settings;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import androidx.annotation.Nullable;

import org.xtimms.kitsune.R;
import org.xtimms.kitsune.utils.PreferencesUtils;

@SuppressWarnings("ALL")
public final class NetworkSettingsFragment extends PreferenceFragment {

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.pref_network);
		PreferencesUtils.bindSummaryMultiple(
				this,
				"network.usage.show_thumbnails"
		);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Activity activity = getActivity();
	}
}
