package org.xtimms.kitsune.ui.tools.settings;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.xtimms.kitsune.R;
import org.xtimms.kitsune.core.common.WeakAsyncTask;
import org.xtimms.kitsune.core.models.MangaFavourite;
import org.xtimms.kitsune.core.storage.db.FavouritesRepository;
import org.xtimms.kitsune.core.storage.db.FavouritesSpecification;
import org.xtimms.kitsune.core.storage.db.SavedMangaRepository;
import org.xtimms.kitsune.utils.CollectionsUtils;
import org.xtimms.kitsune.utils.PreferencesUtils;
import org.xtimms.kitsune.utils.ResourceUtils;

import java.util.ArrayList;
import java.util.Objects;

@SuppressWarnings("ALL")
public final class GeneralSettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.pref_general);
		PreferencesUtils.bindSummaryMultiple(
				this,
				"mangaupdates.interval",
				"mangaupdates.networktype",
				"theme"
		);
		findPreference("mangaupdates.check_now")
				.setSummary(
						formatDateSummary(
								getPreferenceManager().getSharedPreferences().getLong("mangaupdates.last_check", 0)
						));
	}

	@Override
	public void onStart() {
		super.onStart();
		getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onStop() {
		getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
		super.onStop();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		final Activity activity = getActivity();
		Preference.OnPreferenceClickListener onPreferenceClickListener = (Preference.OnPreferenceClickListener) activity;
		final PreferenceCategory category = (PreferenceCategory) findPreference("mangaupdates.tracked");
		new GeneralSettingsFragment.LoadTrackedTask(category).start();
		if (activity != null) {
			findPreference("mangaupdates.check_now")
					.setOnPreferenceClickListener((Preference.OnPreferenceClickListener) activity);
		}
		Preference p = findPreference("mangadir");
		p.setOnPreferenceClickListener(onPreferenceClickListener);
		try {
			p.setSummary(SavedMangaRepository.getMangasDir(activity).getPath());
		} catch (Exception e) {
			p.setSummary(R.string.unknown);
		}
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		if ("mangaupdates.last_check".equals(key)) {
			findPreference("mangaupdates.check_now")
					.setSummary(
							formatDateSummary(
									sharedPreferences.getLong(key, 0)
							));
			final PreferenceCategory category = (PreferenceCategory) findPreference("mangaupdates.tracked");
			new LoadTrackedTask(category).start();
		}
	}

	@SuppressLint("StringFormatInvalid")
	private String formatDateSummary(long timeMs) {
		final String s = timeMs == 0 ? getString(R.string.never) : ResourceUtils.formatTimeRelative(timeMs);
		return getString(R.string.last_update_check, s);
	}

	private static class LoadTrackedTask extends WeakAsyncTask<PreferenceCategory, Void, Void, ArrayList<MangaFavourite>> {

		LoadTrackedTask(PreferenceCategory preferenceCategory) {
			super(preferenceCategory);
		}

		@Override
		protected ArrayList<MangaFavourite> doInBackground(Void... voids) {
			try {
				return FavouritesRepository.get(Objects.requireNonNull(getObject()).getContext())
						.query(new FavouritesSpecification());
			} catch (Exception e) {
				return CollectionsUtils.empty();
			}
		}

		@Override
		@SuppressLint("DefaultLocale")
		protected void onPostExecute(@NonNull PreferenceCategory category, ArrayList<MangaFavourite> headers) {
			final Context context = category.getContext();
			category.removeAll();
			for (MangaFavourite o : headers) {
				final Preference p = new Preference(context);
				p.setKey("manga_" + o.id);
				p.setTitle(o.name);
				p.setSummary(String.format("%s %d (+%d)", context.getString(R.string.chapters_count_), o.totalChapters, o.newChapters));
				category.addPreference(p);
			}
		}
	}

}
