package org.xtimms.kitsune.ui.mangalist;

import android.app.LoaderManager;
import android.content.Loader;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.appcompat.widget.SearchView;

import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.xtimms.kitsune.core.adapters.InfiniteAdapter;
import org.xtimms.kitsune.core.common.PagedList;
import org.xtimms.kitsune.core.common.ThumbSize;
import org.xtimms.kitsune.core.common.base.AppBaseActivity;
import org.xtimms.kitsune.R;
import org.xtimms.kitsune.core.helpers.ListModeHelper;
import org.xtimms.kitsune.core.models.MangaType;
import org.xtimms.kitsune.utils.ErrorUtils;
import org.xtimms.kitsune.core.common.views.InfiniteRecyclerView;
import org.xtimms.kitsune.core.ListWrapper;
import org.xtimms.kitsune.core.models.MangaGenre;
import org.xtimms.kitsune.core.models.MangaHeader;
import org.xtimms.kitsune.source.MangaProvider;
import org.xtimms.kitsune.ui.tools.settings.AuthorizationDialog;
import org.xtimms.kitsune.utils.KaomojiUtils;
import org.xtimms.kitsune.utils.LayoutUtils;

import java.util.ArrayList;
import java.util.Arrays;

@SuppressWarnings("ALL")
public final class MangaListActivity extends AppBaseActivity implements LoaderManager.LoaderCallbacks<ListWrapper<MangaHeader>>,
		View.OnClickListener, FilterCallback, SearchView.OnQueryTextListener, InfiniteRecyclerView.OnLoadMoreListener,
		AuthorizationDialog.Callback, ListModeHelper.OnListModeListener, InfiniteAdapter.OnLoadMoreListener {

	private InfiniteRecyclerView mRecyclerView;
	private ProgressBar mProgressBar;
	private MenuItem mMenuItemSearch;
	private TextView mTextViewError;
	private TextView mTextViewFace;
	private View mErrorView;

	private final PagedList<MangaHeader> mDataset = new PagedList<>();
	private MangaListAdapter mAdapter;
	private MangaProvider mProvider;
	private ListModeHelper mListModeHelper;
	private final MangaQueryArguments mArguments = new MangaQueryArguments();

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mangalist);
		setSupportActionBar(R.id.toolbar);
		enableHomeAsUp();

		FloatingActionButton mFabFilter = findViewById(R.id.fabFilter);
		mProgressBar = findViewById(R.id.progressBar);
		mRecyclerView = findViewById(R.id.recyclerView);
		mErrorView = findViewById(R.id.stub_error);

		mAdapter = new MangaListAdapter(mDataset, mRecyclerView);
		mFabFilter.setOnClickListener(this);
		mAdapter.notifyDataSetChanged();
		mAdapter.setOnLoadMoreListener(this);
		mRecyclerView.setHasFixedSize(true);
		mRecyclerView.setLayoutManager(new GridLayoutManager(this, 1));
		mRecyclerView.scheduleLayoutAnimation();
		mRecyclerView.setOnLoadMoreListener(this);

		mListModeHelper = new ListModeHelper(this, this);
		mListModeHelper.applyCurrent();
		mListModeHelper.enable();

		final String cname = getIntent().getStringExtra("provider.cname");
		assert cname != null;
		mProvider = MangaProvider.get(this, cname);
		setTitle(mProvider.getName());
		if (mProvider.getAvailableGenres().length == 0 && mProvider.getAvailableTypes().length == 0 && mProvider.getAvailableSortOrders().length == 0 && mProvider.getAvailableAdditionalSortOrders().length == 0) {
			mFabFilter.hide();
		}

		mRecyclerView.setAdapter(mAdapter);

		load();
	}

	@Override
	protected void onDestroy() {
		mListModeHelper.disable();
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.options_mangalist, menu);
		mMenuItemSearch = menu.findItem(R.id.action_search);
		SearchView mSearchView = (SearchView) mMenuItemSearch.getActionView();
		mSearchView.setOnQueryTextListener(this);
		mSearchView.setOnSearchClickListener(v -> mSearchView.setQuery(mArguments.query, false));
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.findItem(R.id.action_search).setVisible(mProvider.isSearchSupported());
		menu.findItem(R.id.action_authorize).setVisible(mProvider.isAuthorizationSupported() && !mProvider.isAuthorized());
		/*menu.findItem(R.id.option_details).setChecked(mAdapter.isDetailed());*/
		mListModeHelper.onPrepareOptionsMenu(menu);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_authorize) {
			final AuthorizationDialog dialog = new AuthorizationDialog();
			final Bundle args = new Bundle(1);
			args.putString("provider", mProvider.getCName());
			dialog.setArguments(args);
			dialog.show(getSupportFragmentManager(), "auth");
			return true;
		}
		return mListModeHelper.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
	}

	@Override
	public void onConfigurationChanged(@NonNull Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mListModeHelper.applyCurrent();
	}

	@Override
	public Loader<ListWrapper<MangaHeader>> onCreateLoader(int i, Bundle bundle) {
		final MangaQueryArguments queryArgs = MangaQueryArguments.from(bundle);

		setSubtitle(!TextUtils.isEmpty(queryArgs.query)
				? queryArgs.query
				: queryArgs.genres.length != 0
				? MangaGenre.joinNames(this, queryArgs.genres, ", ")
				: null);

		return new MangaListLoader(this, mProvider, queryArgs);
	}

	@Override
	public void onLoadFinished(Loader<ListWrapper<MangaHeader>> loader, ListWrapper<MangaHeader> result) {
		mProgressBar.setVisibility(View.GONE);
		if (result.isSuccess()) {
			final ArrayList<MangaHeader> list = result.get();
			int firstPos = mDataset.size();
			mDataset.addAll(list);
			if (firstPos == 0) {
				mAdapter.notifyDataSetChanged();
			} else {
				mAdapter.notifyItemRangeInserted(firstPos, list.size());
			}
			mRecyclerView.onLoadingFinished(!list.isEmpty());
		} else {
			setError(result.getError());
			mRecyclerView.onLoadingFinished(false);
		}
	}

	@Override
	public void onLoaderReset(Loader<ListWrapper<MangaHeader>> loader) {

	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.fabFilter:
				final FilterDialogFragment dialogFragment = new FilterDialogFragment();
				final Bundle filter = new Bundle();
				filter.putIntArray("sorts", mProvider.getAvailableSortOrders());
				filter.putIntArray("additionalSorts", mProvider.getAvailableAdditionalSortOrders());
				filter.putParcelableArray("genres", mProvider.getAvailableGenres());
				filter.putParcelableArray("types", mProvider.getAvailableTypes());
				filter.putBundle("query", mArguments.toBundle());
				dialogFragment.setArguments(filter);
				dialogFragment.show(getSupportFragmentManager(), "");
				break;
			case R.id.button_retry:
				mProgressBar.setVisibility(View.VISIBLE);
			case com.google.android.material.R.id.snackbar_action:
				mArguments.page++;
				load();
		}
	}

	@Override
	public void setFilter(int sort, int additionalSort, MangaGenre[] genres, MangaType[] types) {
		final boolean theSame = sort == mArguments.sort && additionalSort == mArguments.additionalSort && Arrays.equals(mArguments.genres, genres) && Arrays.equals(mArguments.types, types);
		if (theSame) return;
		mArguments.sort = sort;
		mArguments.additionalSort = additionalSort;
		mArguments.genres = genres;
		mArguments.types = types;
		loadFirstPage();
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		mMenuItemSearch.collapseActionView();
		if (TextUtils.equals(query, mArguments.query)) {
			return true;
		}
		mArguments.query = query;
		loadFirstPage();
		return true;
	}

	/**
	 * Сбрасываем на первую страницу
	 */
	private void loadFirstPage() {
		mArguments.page = 0;
		mProgressBar.setVisibility(View.VISIBLE);
		mDataset.clear();
		mAdapter.notifyDataSetChanged();
		load();
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		return false;
	}

	@Override
	public boolean onLoadMore() {
		mArguments.page++;
		load();
		return true;
	}

	@Override
	public void onAuthorized() {
		Snackbar.make(mRecyclerView, R.string.authorization_success, Snackbar.LENGTH_SHORT).show();
		mArguments.page = 0;
		load();
	}

	private void setError(Throwable e) {
		if (mDataset.isEmpty()) {
			if (mErrorView instanceof ViewStub) {
				mErrorView = ((ViewStub) mErrorView).inflate();
				mTextViewError = mErrorView.findViewById(R.id.textView_error);
				mTextViewFace = mErrorView.findViewById(R.id.text_face);
				mErrorView.findViewById(R.id.button_retry).setOnClickListener(this);
			}
			mTextViewFace.setText(KaomojiUtils.getRandomErrorFace());
			mTextViewError.setText(ErrorUtils.getErrorMessage(this, e));
			mErrorView.setVisibility(View.VISIBLE);
		} else {
			Snackbar.make(mRecyclerView, ErrorUtils.getErrorMessage(e), Snackbar.LENGTH_INDEFINITE)
					.setAction(R.string.retry, this)
					.show();
		}
	}

	/**
	 * Лоадер не перезагрузится если
	 * Bundle тот же самый с темиже значениями (вроде)
	 */
	private void load() {
		mRecyclerView.onLoadingStarted();
		mErrorView.setVisibility(View.GONE);
		/*
	  loader's id
	 */
		int LOADER_ID = 234;
		Loader<ListWrapper<MangaHeader>> loader = getLoaderManager().getLoader(LOADER_ID);
		if (loader == null) {
			loader = getLoaderManager().initLoader(LOADER_ID, mArguments.toBundle(), this);
		} else {
			loader = getLoaderManager().restartLoader(LOADER_ID, mArguments.toBundle(), this);
		}
		loader.forceLoad();
		mAdapter.setLoaded();
	}

	public void updateLayout(boolean grid, int spanCount, ThumbSize thumbSize) {
		GridLayoutManager layoutManager = (GridLayoutManager) mRecyclerView.getLayoutManager();
		assert layoutManager != null;
		int position = layoutManager.findFirstCompletelyVisibleItemPosition();
		layoutManager.setSpanCount(spanCount);
//		layoutManager.setSpanSizeLookup(new MangaListAdapter.AutoSpanSizeLookup(spanCount));
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

}
