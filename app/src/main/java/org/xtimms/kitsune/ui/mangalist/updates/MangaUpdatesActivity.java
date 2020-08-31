package org.xtimms.kitsune.ui.mangalist.updates;

import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import org.xtimms.kitsune.core.common.base.AppBaseActivity;
import org.xtimms.kitsune.R;
import org.xtimms.kitsune.core.common.OemBadgeHelper;
import org.xtimms.kitsune.core.common.views.recyclerview.SwipeRemoveHelper;
import org.xtimms.kitsune.utils.AnimationUtils;
import org.xtimms.kitsune.utils.ErrorUtils;
import org.xtimms.kitsune.core.ListWrapper;
import org.xtimms.kitsune.core.models.MangaFavourite;
import org.xtimms.kitsune.core.storage.db.FavouritesRepository;
import org.xtimms.kitsune.core.storage.db.FavouritesSpecification;
import org.xtimms.kitsune.ui.mangalist.favourites.FavouritesLoader;

import java.util.ArrayList;

@SuppressWarnings("ALL")
public final class MangaUpdatesActivity extends AppBaseActivity implements
		LoaderManager.LoaderCallbacks<ListWrapper<MangaFavourite>>, SwipeRemoveHelper.OnItemRemovedListener {

	private RecyclerView mRecyclerView;
	private ProgressBar mProgressBar;
	private TextView mTextViewHolder;

	private MangaUpdatesAdapter mAdapter;
	private ArrayList<MangaFavourite> mDataset;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_history);
		setSupportActionBar(R.id.toolbar);
		enableHomeAsUp();

		mProgressBar = findViewById(R.id.progressBar);
		mRecyclerView = findViewById(R.id.recyclerView);
		mTextViewHolder = findViewById(R.id.textView_holder);
		mRecyclerView.setHasFixedSize(true);
		mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
		mTextViewHolder.setText(R.string.no_new_chapters);

		mDataset = new ArrayList<>();
		mAdapter = new MangaUpdatesAdapter(mDataset);
		mRecyclerView.setAdapter(mAdapter);
		SwipeRemoveHelper.setup(mRecyclerView, this, R.color.green_overlay, R.drawable.ic_done_all_white);

		getLoaderManager().initLoader(0, null, this).forceLoad();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.options_updates, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_clear) {
			new AlertDialog.Builder(this)
					.setMessage(R.string.mark_all_viewed_confirm)
					.setNegativeButton(android.R.string.cancel, null)
					.setPositiveButton(android.R.string.yes, (dialog, which) -> {
						FavouritesRepository.get(MangaUpdatesActivity.this).clearNewChapters();
						onLoadFinished(null, new ListWrapper<>(new ArrayList<>()));
						Snackbar.make(mRecyclerView, R.string.chapters_marked_as_viewed, Snackbar.LENGTH_LONG).show();
						new OemBadgeHelper(MangaUpdatesActivity.this).applyCount(0);
					})
					.create()
					.show();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public Loader<ListWrapper<MangaFavourite>> onCreateLoader(int i, Bundle bundle) {
		return new FavouritesLoader(this, new FavouritesSpecification().onlyWithNewChapters().orderByDate(true));
	}

	@Override
	public void onLoadFinished(@Nullable Loader<ListWrapper<MangaFavourite>> loader, ListWrapper<MangaFavourite> result) {
		mProgressBar.setVisibility(View.GONE);
		if (result.isSuccess()) {
			final ArrayList<MangaFavourite> list = result.get();
			mDataset.clear();
			mDataset.addAll(list);
			mAdapter.notifyDataSetChanged();
			AnimationUtils.setVisibility(mTextViewHolder, mDataset.isEmpty() ? View.VISIBLE : View.GONE);
		} else {
			Snackbar.make(mRecyclerView, ErrorUtils.getErrorMessage(result.getError()), Snackbar.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onLoaderReset(Loader<ListWrapper<MangaFavourite>> loader) {

	}

	@Override
	public void onItemRemoved(int position) {
		final MangaFavourite item = mDataset.remove(position);
		mAdapter.notifyItemRemoved(position);
		FavouritesRepository.get(this).setNoUpdates(item);
		AnimationUtils.setVisibility(mTextViewHolder, mDataset.isEmpty() ? View.VISIBLE : View.GONE);
		Snackbar.make(mRecyclerView, R.string.chapters_marked_as_viewed, Snackbar.LENGTH_SHORT).show();
		int total = 0;
		for (MangaFavourite o : mDataset) {
			total += o.newChapters;
		}
		new OemBadgeHelper(this).applyCount(total);
	}
}
