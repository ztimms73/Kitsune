package org.xtimms.kitsune.ui.tools.settings;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import androidx.annotation.Nullable;

import org.xtimms.kitsune.R;
import org.xtimms.kitsune.utils.PreferencesUtils;

@SuppressWarnings("ALL")
public final class ShelfSettingsFragment extends PreferenceFragment {

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.pref_shelf);
		PreferencesUtils.bindSummaryMultiple(
				this,
				"shelf.history_rows",
				"shelf.favourites_cat_rows",
				"shelf.favourites_categories",
				"shelf.savedManga_rows"
		);
	}
}
