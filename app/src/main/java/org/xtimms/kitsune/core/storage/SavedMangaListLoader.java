package org.xtimms.kitsune.core.storage;

import android.content.AsyncTaskLoader;
import android.content.Context;
import androidx.annotation.NonNull;

import org.xtimms.kitsune.core.ListWrapper;
import org.xtimms.kitsune.core.models.SavedManga;
import org.xtimms.kitsune.core.storage.db.SavedChaptersRepository;
import org.xtimms.kitsune.core.storage.db.SavedMangaRepository;
import org.xtimms.kitsune.core.storage.db.SavedMangaSpecification;

import java.util.ArrayList;

@SuppressWarnings("ALL")
final class SavedMangaListLoader extends AsyncTaskLoader<ListWrapper<SavedMangaSummary>> {

	private final SavedMangaSpecification mSpec;

	SavedMangaListLoader(Context context, SavedMangaSpecification specification) {
		super(context);
		mSpec = specification;
	}

	@Override
	@NonNull
	public ListWrapper<SavedMangaSummary> loadInBackground() {
		try {
			ArrayList<SavedManga> list = SavedMangaRepository.get(getContext()).query(mSpec);
			if (list == null) {
				return ListWrapper.badList();
			}
			final SavedChaptersRepository chaptersRepository = SavedChaptersRepository.get(getContext());
			final ArrayList<SavedMangaSummary> result = new ArrayList<>(list.size());
			for (SavedManga o : list) {
				result.add(new SavedMangaSummary(o, chaptersRepository.count(o)));
			}
			return new ListWrapper<>(result);
		} catch (Exception e) {
			return new ListWrapper<>(e);
		}
	}
}
