package org.xtimms.kitsune.ui.shelf;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import androidx.annotation.Nullable;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.jetbrains.annotations.NotNull;
import org.xtimms.kitsune.core.common.base.AppBaseFragment;
import org.xtimms.kitsune.R;
import org.xtimms.kitsune.core.common.Dismissible;
import org.xtimms.kitsune.utils.ResourceUtils;
import org.xtimms.kitsune.ui.tools.settings.SettingsActivity;

@SuppressWarnings("ALL")
public final class ShelfFragment extends AppBaseFragment implements LoaderManager.LoaderCallbacks<ShelfContent> {

	private RecyclerView mRecyclerView;
	private ShelfAdapter mAdapter;
	private int mColumnCount;
	private boolean mWasPaused;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mColumnCount = 12;
		mWasPaused = false;
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, R.layout.recyclerview);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mRecyclerView = view.findViewById(R.id.recyclerView);
		mRecyclerView.setHasFixedSize(true);
		new ItemTouchHelper(new DismissCallback()).attachToRecyclerView(mRecyclerView);
		mRecyclerView.setClipToPadding(false);
		mRecyclerView.setPadding(
				mRecyclerView.getPaddingLeft(),
				mRecyclerView.getPaddingTop(),
				mRecyclerView.getPaddingRight(),
				mRecyclerView.getPaddingBottom()
		);
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (ResourceUtils.isTablet(getResources().getConfiguration())) {
			mColumnCount = 24;
		}
		mAdapter = new ShelfAdapter((OnTipsActionListener) getActivity());
		GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), mColumnCount);
		layoutManager.setSpanSizeLookup(new ShelfSpanSizeLookup(mAdapter, mColumnCount));
		mRecyclerView.addItemDecoration(new ShelfItemSpaceDecoration(ResourceUtils.dpToPx(getResources(), 4), mAdapter, mColumnCount));
		mRecyclerView.setLayoutManager(layoutManager);
		mRecyclerView.setAdapter(mAdapter);
		getLoaderManager().initLoader(0, null, this).forceLoad();
		mWasPaused = false;
	}

	@Override
	public Loader<ShelfContent> onCreateLoader(int i, Bundle bundle) {
		return new ShelfLoader(getActivity(), mColumnCount);
	}

	@Override
	public void onLoadFinished(Loader<ShelfContent> loader, ShelfContent content) {
		ShelfUpdater.update(mAdapter, content);
	}

	@Override
	public void onLoaderReset(Loader<ShelfContent> loader) {

	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.options_shelf, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_shelf_settings) {
			startActivity(new Intent(getActivity(), SettingsActivity.class)
					.setAction(SettingsActivity.ACTION_SETTINGS_SHELF));
			return true;
			//case R.id.action_check_updates:
			//	Snackbar.make(mRecyclerView, R.string.checking_new_chapters, Snackbar.LENGTH_SHORT)
			//			.show();
			//	UpdatesCheckService.runForce(getActivity());
			//	return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onPause() {
		mWasPaused = true;
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
		if (mWasPaused) {
			getLoaderManager().restartLoader(0, null, this).forceLoad();
			mWasPaused = false;
		}
	}

	@Override
	public void scrollToTop() {
		mRecyclerView.smoothScrollToPosition(0);
	}

	private static class DismissCallback extends ItemTouchHelper.SimpleCallback {

		DismissCallback() {
			super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
		}

		@Override
		public int getSwipeDirs(@NotNull RecyclerView recyclerView, @NotNull RecyclerView.ViewHolder viewHolder) {
			return viewHolder instanceof ShelfAdapter.TipHolder && ((ShelfAdapter.TipHolder) viewHolder).isDismissible() ?
					super.getSwipeDirs(recyclerView, viewHolder) : 0;
		}

		@Override
		public boolean onMove(@NotNull RecyclerView recyclerView, @NotNull RecyclerView.ViewHolder viewHolder, @NotNull RecyclerView.ViewHolder target) {
			return false;
		}

		@Override
		public void onSwiped(@NotNull RecyclerView.ViewHolder viewHolder, int direction) {
			if (viewHolder instanceof Dismissible) {
				((Dismissible) viewHolder).dismiss();
			}
		}
	}
}
