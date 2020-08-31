package org.xtimms.kitsune.ui.discover.bookmarks;

import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.snackbar.Snackbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.xtimms.kitsune.core.common.base.AppBaseActivity;
import org.xtimms.kitsune.R;
import org.xtimms.kitsune.utils.AnimationUtils;
import org.xtimms.kitsune.utils.ErrorUtils;
import org.xtimms.kitsune.utils.MetricsUtils;
import org.xtimms.kitsune.utils.ResourceUtils;
import org.xtimms.kitsune.core.common.views.recyclerview.SpaceItemDecoration;
import org.xtimms.kitsune.core.ListWrapper;
import org.xtimms.kitsune.core.models.MangaBookmark;
import org.xtimms.kitsune.core.models.UniqueObject;
import org.xtimms.kitsune.core.storage.db.BookmarkSpecification;
import org.xtimms.kitsune.core.storage.files.ThumbnailsStorage;

import java.util.ArrayList;

@SuppressWarnings("deprecation")
public final class BookmarksListActivity extends AppBaseActivity implements LoaderManager.LoaderCallbacks<ListWrapper<MangaBookmark>>,
		BookmarkRemoveTask.OnBookmarkRemovedListener {

	private RecyclerView mRecyclerView;
	private ProgressBar mProgressBar;
	private TextView mTextViewHolder;

	private BookmarksListAdapter mAdapter;
	private ArrayList<UniqueObject> mDataset;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bookmarks);
		setSupportActionBar(R.id.toolbar);
		enableHomeAsUp();

		mProgressBar = findViewById(R.id.progressBar);
		mRecyclerView = findViewById(R.id.recyclerView);
		mTextViewHolder = findViewById(R.id.textView_holder);
		mRecyclerView.setHasFixedSize(true);

		final int spans = MetricsUtils.getPreferredColumnsCountMedium(getResources());
		final GridLayoutManager layoutManager = new GridLayoutManager(this, spans);
		mRecyclerView.addItemDecoration(new SpaceItemDecoration(ResourceUtils.dpToPx(getResources(), 1)));
		mRecyclerView.setLayoutManager(layoutManager);

		mDataset = new ArrayList<>();
		mAdapter = new BookmarksListAdapter(mDataset, new ThumbnailsStorage(this));
		mRecyclerView.setAdapter(mAdapter);
		layoutManager.setSpanSizeLookup(new BookmarkSpanSizeLookup(mAdapter, spans));

		getLoaderManager().initLoader(0, new BookmarkSpecification().orderByMangaAndDate(true).toBundle(), this).forceLoad();
	}

	@Override
	public Loader<ListWrapper<MangaBookmark>> onCreateLoader(int id, Bundle args) {
		return new BookmarksLoader(this, BookmarkSpecification.from(args));
	}

	@Override
	public void onLoadFinished(Loader<ListWrapper<MangaBookmark>> loader, ListWrapper<MangaBookmark> result) {
		mProgressBar.setVisibility(View.GONE);
		if (result.isSuccess()) {
			final ArrayList<MangaBookmark> list = result.get();
			mDataset.clear();
			long prevId = 0;
			for (MangaBookmark o : list) {
				if (prevId != o.manga.id) {
					mDataset.add(o.manga);
				}
				prevId = o.manga.id;
				mDataset.add(o);
			}
			mAdapter.notifyDataSetChanged();
			AnimationUtils.setVisibility(mTextViewHolder, mDataset.isEmpty() ? View.VISIBLE : View.GONE);
		} else {
			Snackbar.make(mRecyclerView, ErrorUtils.getErrorMessage(result.getError()), Snackbar.LENGTH_SHORT).show();
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
