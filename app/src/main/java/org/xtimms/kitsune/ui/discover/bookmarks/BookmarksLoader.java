package org.xtimms.kitsune.ui.discover.bookmarks;

import android.content.AsyncTaskLoader;
import android.content.Context;

import org.xtimms.kitsune.core.ListWrapper;
import org.xtimms.kitsune.core.models.MangaBookmark;
import org.xtimms.kitsune.core.storage.db.BookmarkSpecification;
import org.xtimms.kitsune.core.storage.db.BookmarksRepository;

import java.util.ArrayList;

@SuppressWarnings("ALL")
public final class BookmarksLoader extends AsyncTaskLoader<ListWrapper<MangaBookmark>> {

	private final BookmarkSpecification mSpec;

	public BookmarksLoader(Context context, BookmarkSpecification specification) {
		super(context);
		mSpec = specification;
	}

	@Override
	public ListWrapper<MangaBookmark> loadInBackground() {
		final BookmarksRepository repo = BookmarksRepository.get(getContext());
		final ArrayList<MangaBookmark> list = repo.query(mSpec);
		return list == null ? ListWrapper.badList() : new ListWrapper<>(list);
	}
}
