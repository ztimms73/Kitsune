package org.xtimms.kitsune.ui.mangalist.favourites;

import android.content.AsyncTaskLoader;
import android.content.Context;

import org.xtimms.kitsune.core.ListWrapper;
import org.xtimms.kitsune.core.models.MangaFavourite;
import org.xtimms.kitsune.core.storage.db.FavouritesRepository;
import org.xtimms.kitsune.core.storage.db.FavouritesSpecification;

@SuppressWarnings("ALL")
public final class FavouritesLoader extends AsyncTaskLoader<ListWrapper<MangaFavourite>> {

	private final FavouritesSpecification mSpec;

	public FavouritesLoader(Context context, FavouritesSpecification specification) {
		super(context);
		mSpec = specification;
	}

	@Override
	public ListWrapper<MangaFavourite> loadInBackground() {
		try {
			return new ListWrapper<>(FavouritesRepository.get(getContext()).query(mSpec));
		} catch (Exception e) {
			e.printStackTrace();
			return new ListWrapper<>(e);
		}
	}
}
