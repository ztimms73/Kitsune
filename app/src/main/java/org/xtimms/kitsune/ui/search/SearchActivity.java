package org.xtimms.kitsune.ui.search;

import android.app.LoaderManager;
import android.content.Loader;
import android.content.res.Configuration;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;
import org.xtimms.kitsune.core.common.ThumbSize;
import org.xtimms.kitsune.core.common.base.AppBaseActivity;
import org.xtimms.kitsune.R;
import org.xtimms.kitsune.core.common.views.InfiniteRecyclerView;
import org.xtimms.kitsune.core.ListWrapper;
import org.xtimms.kitsune.core.helpers.ListModeHelper;
import org.xtimms.kitsune.core.models.ProviderHeader;
import org.xtimms.kitsune.core.storage.ProvidersStore;
import org.xtimms.kitsune.utils.LayoutUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Stack;

@SuppressWarnings("ALL")
public final class SearchActivity extends AppBaseActivity implements LoaderManager.LoaderCallbacks<ListWrapper>,
		InfiniteRecyclerView.OnLoadMoreListener, View.OnClickListener, ListModeHelper.OnListModeListener {

	private InfiniteRecyclerView mRecyclerView;
	private ProgressBar mProgressBar;
	private TextView mTextViewHolder;

	private final Stack<ProviderHeader> mProviders = new Stack<>();
	private SearchQueryArguments mQueryArguments;
	private ListModeHelper mListModeHelper;

	private final ArrayList<Object> mDataset = new ArrayList<>();
	private SearchResultsAdapter mAdapter;

	//data
	private String mQuery;


	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		setSupportActionBar(R.id.toolbar);
		enableHomeAsUp();

		Bundle extras = getIntent().getExtras();
		assert extras != null;
		mQuery = extras.getString("query");
		String mTitle = extras.getString("title");

		if (mTitle != null) {
			setTitle(mQuery);
		}

		setSubtitle(mTitle == null ? mQuery : mTitle);

		mRecyclerView = findViewById(R.id.recyclerView);
		mProgressBar = findViewById(R.id.progressBar);
		mTextViewHolder = findViewById(R.id.textView_holder);

		mAdapter = new SearchResultsAdapter(mDataset, mRecyclerView);

		mRecyclerView.setLayoutManager(new GridLayoutManager(this, 1));
		mRecyclerView.setOnLoadMoreListener(this);

		mListModeHelper = new ListModeHelper(this, this);
		mListModeHelper.applyCurrent();
		mListModeHelper.enable();

		mRecyclerView.setAdapter(mAdapter);

		beginSearch(mQuery);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.options_search, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		mListModeHelper.onPrepareOptionsMenu(menu);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return mListModeHelper.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
	}

	private void beginSearch(@NonNull String query) {
		mProgressBar.setVisibility(View.VISIBLE);
		mTextViewHolder.setVisibility(View.GONE);
		setSubtitle(mQuery);
		SearchSuggestionsProvider.getSuggestions(this).saveRecentQuery(query, null);
		mProviders.clear();
		mProviders.addAll(new ProvidersStore(this).getUserProviders());
		mQueryArguments = new SearchQueryArguments(mQuery, mProviders.pop().cName);
		mRecyclerView.onLoadingStarted();
		mDataset.clear();
		getLoaderManager().initLoader(0, mQueryArguments.toBundle(), this).forceLoad();
	}

	@Override
	public Loader<ListWrapper> onCreateLoader(int id, Bundle args) {
		return new SearchLoader(this, SearchQueryArguments.from(args));
	}

	@Override
	public void onLoadFinished(Loader<ListWrapper> loader, @NonNull ListWrapper result) {
		if (result.isFailed() || result.isEmpty()) {
			//next provider
			if (mProviders.empty()) {
				mProgressBar.setVisibility(View.GONE);
				mRecyclerView.onLoadingFinished(false);
				if (mDataset.isEmpty()) {
					mTextViewHolder.setVisibility(View.VISIBLE);
					return;
				}
			} else {
				mQueryArguments.page = 0;
				mQueryArguments.providerCName = mProviders.pop().cName;
				mRecyclerView.onLoadingFinished(true);
			}
			return;
		}
		mProgressBar.setVisibility(View.GONE);
		int i = mDataset.size();
		mDataset.addAll((Collection)result.get());
		if (i == 0) {
			mAdapter.notifyDataSetChanged();
		} else {
			mAdapter.notifyItemRangeInserted(i, result.size());
		}
		SearchQueryArguments searchQueryArguments = mQueryArguments;
		mQueryArguments.page++;
		mRecyclerView.onLoadingFinished(true);
	}

	@Override
	public void onLoaderReset(Loader<ListWrapper> loader) {

	}

	@Override
	public boolean onLoadMore() {
		mRecyclerView.onLoadingStarted();
		getLoaderManager().restartLoader(0, mQueryArguments.toBundle(), this).forceLoad();
		return true;
	}

	@Override
	public void onClick(View v) {
		if (mDataset.isEmpty()) {
			mProgressBar.setVisibility(View.VISIBLE);
		}
		mTextViewHolder.setVisibility(View.GONE);
		onLoadMore();
	}

	public void updateLayout(boolean grid, int spanCount, ThumbSize thumbSize) {
		GridLayoutManager layoutManager = (GridLayoutManager) mRecyclerView.getLayoutManager();
		assert layoutManager != null;
		int position = layoutManager.findFirstCompletelyVisibleItemPosition();
		layoutManager.setSpanCount(spanCount);
		//layoutManager.setSpanSizeLookup(new MangaListAdapter.AutoSpanSizeLookup(spanCount));
		mAdapter.setThumbnailsSize(thumbSize);
		if (mAdapter.setGrid(grid)) {
			mRecyclerView.setAdapter(mAdapter);
		}
		mRecyclerView.scrollToPosition(position);
	}

	@Override
	public void onListModeChanged(boolean grid, int sizeMode) {
		int spans;
		ThumbSize thumbSize;
		switch (sizeMode) {
			case -1:
				spans = LayoutUtils.isTabletLandscape(this) ? 2 : 1;
				thumbSize = ThumbSize.THUMB_SIZE_LIST;
				break;
			case 0:
				spans = LayoutUtils.getOptimalColumnsCount(getResources(), thumbSize = ThumbSize.THUMB_SIZE_SMALL);
				break;
			case 1:
				spans = LayoutUtils.getOptimalColumnsCount(getResources(), thumbSize = ThumbSize.THUMB_SIZE_MEDIUM);
				break;
			case 2:
				spans = LayoutUtils.getOptimalColumnsCount(getResources(), thumbSize = ThumbSize.THUMB_SIZE_LARGE);
				break;
			default:
				return;
		}
		updateLayout(grid, spans, thumbSize);
	}

	@Override
	public void onConfigurationChanged(@NotNull Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mListModeHelper.applyCurrent();
	}

	@Override
	protected void onDestroy() {
		mListModeHelper.disable();
		super.onDestroy();
	}
}