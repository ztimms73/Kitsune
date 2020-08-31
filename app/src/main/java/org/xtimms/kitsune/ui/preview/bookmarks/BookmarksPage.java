package org.xtimms.kitsune.ui.preview.bookmarks;

import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.snackbar.Snackbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.xtimms.kitsune.R;
import org.xtimms.kitsune.utils.MetricsUtils;
import org.xtimms.kitsune.utils.ResourceUtils;
import org.xtimms.kitsune.core.common.views.recyclerview.SpaceItemDecoration;
import org.xtimms.kitsune.core.ListWrapper;
import org.xtimms.kitsune.core.models.MangaBookmark;
import org.xtimms.kitsune.core.storage.db.BookmarkSpecification;
import org.xtimms.kitsune.core.storage.files.ThumbnailsStorage;
import org.xtimms.kitsune.ui.discover.bookmarks.BookmarkRemoveTask;
import org.xtimms.kitsune.ui.discover.bookmarks.BookmarksLoader;
import org.xtimms.kitsune.ui.preview.PageHolder;

import java.util.ArrayList;

@SuppressWarnings("ALL")
public final class BookmarksPage extends PageHolder implements LoaderManager.LoaderCallbacks<ListWrapper<MangaBookmark>>,
		BookmarkRemoveTask.OnBookmarkRemovedListener {

	public RecyclerView mRecyclerView;
	private TextView mTextViewHolder;
	private final ArrayList<MangaBookmark> mDataset = new ArrayList<>();
	private final BookmarksAdapter mAdapter;


	public BookmarksPage(@NonNull ViewGroup parent) {
		super(parent, R.layout.page_manga_bookmarks);
		mAdapter = new BookmarksAdapter(mDataset, new ThumbnailsStorage(parent.getContext()));
		mRecyclerView.setAdapter(mAdapter);
	}

	@Override
	protected void onViewCreated(@NonNull View view) {
		final int spans = MetricsUtils.getPreferredColumnsCountMedium(view.getResources());
		mRecyclerView = view.findViewById(R.id.recyclerView);
		mRecyclerView.addItemDecoration(new SpaceItemDecoration(ResourceUtils.dpToPx(view.getResources(), 1)));
		mRecyclerView.setLayoutManager(new GridLayoutManager(view.getContext(), 2));
		mTextViewHolder = view.findViewById(R.id.textView_holder);
	}

	@SuppressWarnings("deprecation")
	@Override
	public Loader<ListWrapper<MangaBookmark>> onCreateLoader(int id, Bundle args) {
		return new BookmarksLoader(getView().getContext(), BookmarkSpecification.from(args));
	}

	@Override
	public void onLoadFinished(Loader<ListWrapper<MangaBookmark>> loader, ListWrapper<MangaBookmark> data) {
		if (data.isSuccess()) {
			mDataset.clear();
			mDataset.addAll(data.get());
			mTextViewHolder.setVisibility(data.isEmpty() ? View.VISIBLE : View.GONE);
		}
	}

	@Override
	public void onLoaderReset(Loader<ListWrapper<MangaBookmark>> loader) {

	}

	@Override
	public void onBookmarkRemoved(@NonNull MangaBookmark bookmark) {
		mDataset.remove(bookmark);
		mAdapter.notifyDataSetChanged();
		Snackbar.make(mRecyclerView, R.string.bookmark_removed, Snackbar.LENGTH_SHORT).show();
	}
}
