package org.xtimms.kitsune.ui.recommendations;

import android.content.AsyncTaskLoader;
import android.content.Context;

import org.xtimms.kitsune.core.ListWrapper;
import org.xtimms.kitsune.core.models.MangaRecommendation;
import org.xtimms.kitsune.core.storage.db.RecommendationsRepository;
import org.xtimms.kitsune.core.storage.db.RecommendationsSpecifications;

import java.util.ArrayList;

@SuppressWarnings("ALL")
final class RecommendationsLoader extends AsyncTaskLoader<ListWrapper<MangaRecommendation>> {

	private final RecommendationsSpecifications mSpecifications;

	public RecommendationsLoader(Context context, RecommendationsSpecifications specifications) {
		super(context);
		mSpecifications = specifications;
	}

	@Override
	public ListWrapper<MangaRecommendation> loadInBackground() {
		try {
			final RecommendationsRepository repository = RecommendationsRepository.get(getContext());
			final ArrayList<MangaRecommendation> list = repository.query(mSpecifications);
			return list == null ? ListWrapper.badList() : new ListWrapper<>(list);
		} catch (Exception e) {
			e.printStackTrace();
			return new ListWrapper<>(e);
		}
	}
}
