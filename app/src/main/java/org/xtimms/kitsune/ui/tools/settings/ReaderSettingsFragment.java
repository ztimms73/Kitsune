package org.xtimms.kitsune.ui.tools.settings;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import androidx.annotation.Nullable;

import org.xtimms.kitsune.R;
import org.xtimms.kitsune.utils.PreferencesUtils;

@SuppressWarnings("ALL")
public final class ReaderSettingsFragment extends PreferenceFragment {

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.pref_reader);
		PreferencesUtils.bindSummaryMultiple(
				this,
				"reader.default_preset",
				"reader.scale_mode",
				"reader.background"
		);
	}
}
