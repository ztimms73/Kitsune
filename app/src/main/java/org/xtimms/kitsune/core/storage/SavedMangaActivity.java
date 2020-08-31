package org.xtimms.kitsune.core.storage;

import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.snackbar.Snackbar;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.xtimms.kitsune.core.common.base.AppBaseActivity;
import org.xtimms.kitsune.R;
import org.xtimms.kitsune.utils.AnimationUtils;
import org.xtimms.kitsune.utils.ErrorUtils;
import org.xtimms.kitsune.utils.MenuUtils;
import org.xtimms.kitsune.core.ListWrapper;
import org.xtimms.kitsune.core.storage.db.SavedMangaRepository;
import org.xtimms.kitsune.core.storage.db.SavedMangaSpecification;

import java.util.ArrayList;

@SuppressWarnings("ALL")
public final class SavedMangaActivity extends AppBaseActivity implements LoaderManager.LoaderCallbacks<ListWrapper<SavedMangaSummary>>{

	private RecyclerView mRecyclerView;
	private ProgressBar mProgressBar;
	private TextView mTextViewHolder;
	private SwipeRefreshLayout mSwipeRefreshLayout;

	private SavedMangaAdapter mAdapter;
	private ArrayList<SavedMangaSummary> mDataset;
	private SavedMangaSpecification mSpecifications;

	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_saved);
		setSupportActionBar(R.id.toolbar);
		enableHomeAsUp();

		mProgressBar = findViewById(R.id.progressBar);
		mRecyclerView = findViewById(R.id.recyclerView);
		mTextViewHolder = findViewById(R.id.textView_holder);
		mTextViewHolder.setText(R.string.no_saved_manga);
		mRecyclerView.setHasFixedSize(true);
		mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));

		mSpecifications = new SavedMangaSpecification()
				.orderByDate(true);
		SavedMangaRepository mMangaRepository = SavedMangaRepository.get(this);

		mDataset = new ArrayList<>();
		mAdapter = new SavedMangaAdapter(mDataset);
		mRecyclerView.setAdapter(mAdapter);

		getLoaderManager().initLoader(0, mSpecifications.toBundle(), this).forceLoad();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.options_saved_manga, menu);
		MenuUtils.setRadioCheckable(menu.findItem(R.id.action_sort), R.id.group_sort);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		final String orderBy = mSpecifications.getOrderBy();
		if (orderBy != null && orderBy.contains("name")) {
			menu.findItem(R.id.sort_name).setChecked(true);
		} else {
			menu.findItem(R.id.sort_latest).setChecked(true);
		}
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.sort_latest:
				item.setChecked(true);
				mSpecifications.orderByDate(true);
				getLoaderManager().restartLoader(0, mSpecifications.toBundle(), this).forceLoad();
				return true;
			case R.id.sort_name:
				item.setChecked(true);
				mSpecifications.orderByName(false);
				getLoaderManager().restartLoader(0, mSpecifications.toBundle(), this).forceLoad();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public Loader<ListWrapper<SavedMangaSummary>> onCreateLoader(int id, Bundle args) {
		return new SavedMangaListLoader(this, SavedMangaSpecification.from(args));
	}

	@Override
	public void onLoadFinished(Loader<ListWrapper<SavedMangaSummary>> loader, ListWrapper<SavedMangaSummary> result) {
		mProgressBar.setVisibility(View.GONE);
		if (result.isSuccess()) {
			final ArrayList<SavedMangaSummary> list = result.get();
			mDataset.clear();
			mDataset.addAll(list);
			mAdapter.notifyDataSetChanged();
			AnimationUtils.setVisibility(mTextViewHolder, mDataset.isEmpty() ? View.VISIBLE : View.GONE);
		} else {
			Snackbar.make(mRecyclerView, ErrorUtils.getErrorMessage(result.getError()), Snackbar.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onLoaderReset(Loader<ListWrapper<SavedMangaSummary>> loader) {

	}

	@Override
	protected void onResume() {
		super.onResume();
		getLoaderManager().restartLoader(0, mSpecifications.toBundle(), this).forceLoad();
	}
}
