package org.xtimms.kitsune.ui.mangalist.history;

import android.content.AsyncTaskLoader;
import android.content.Context;

import org.xtimms.kitsune.core.ListWrapper;
import org.xtimms.kitsune.core.models.MangaHistory;
import org.xtimms.kitsune.core.storage.db.HistoryRepository;
import org.xtimms.kitsune.core.storage.db.HistorySpecification;

@SuppressWarnings("ALL")
final class HistoryLoader extends AsyncTaskLoader<ListWrapper<MangaHistory>> {

	private final HistorySpecification mSpec;

	public HistoryLoader(Context context, HistorySpecification specification) {
		super(context);
		mSpec = specification;
	}

	@Override
	public ListWrapper<MangaHistory> loadInBackground() {
		try {
			return new ListWrapper<>(HistoryRepository.get(getContext()).query(mSpec));
		} catch (Exception e) {
			e.printStackTrace();
			return new ListWrapper<>(e);
		}
	}
}