package org.xtimms.kitsune.ui.tools;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.snackbar.Snackbar;

import androidx.core.widget.NestedScrollView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.xtimms.kitsune.core.common.base.AppBaseFragment;
import org.xtimms.kitsune.BuildConfig;
import org.xtimms.kitsune.R;
import org.xtimms.kitsune.core.storage.db.StorageHelper;
import org.xtimms.kitsune.utils.ResourceUtils;
import org.xtimms.kitsune.utils.TextUtils;

import java.sql.Time;

@SuppressWarnings("ALL")
public final class ToolsFragment extends AppBaseFragment implements View.OnClickListener,
		LoaderManager.LoaderCallbacks<StorageStats>, CacheClearTask.Callback {

	private static final int LOADER_STORAGE_STATS = 0;

	private NestedScrollView mScrollView;
	private TextView mTextViewStorageTotal;
	private TextView mTextViewStorageCache;
	private TextView mTextViewStorageManga;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, R.layout.fragment_tools);
	}

	@Override
	@SuppressLint("SetTextI18n")
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		StorageHelper storageHelper = new StorageHelper(view.getContext());
		mScrollView = view.findViewById(R.id.scrollView);
		mScrollView.setClipToPadding(false);
		mScrollView.setPadding(
				mScrollView.getPaddingLeft(),
				mScrollView.getPaddingTop(),
				mScrollView.getPaddingRight(),
				mScrollView.getPaddingBottom()
		);
		mTextViewStorageTotal = view.findViewById(R.id.textView_storage_total);
		mTextViewStorageCache = view.findViewById(R.id.textView_storage_cache);
		mTextViewStorageManga = view.findViewById(R.id.textView_storage_manga);

		view.findViewById(R.id.button_clear_cache).setOnClickListener(this);
		if (!BuildConfig.DEBUG) {
			view.<TextView>findViewById(R.id.textView_about).setText(
					view.getContext().getString(R.string.app_name) + " v" + BuildConfig.VERSION_NAME
							+ "\n" + ResourceUtils.formatDateTime(view.getContext(), BuildConfig.TIMESTAMP)
			);
		} else {
			view.<TextView>findViewById(R.id.textView_about).setText(
					view.getContext().getString(R.string.app_name) +
							"\n" + ResourceUtils.formatDateTime(view.getContext(), BuildConfig.TIMESTAMP)
			);
		}
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		final Activity activity = getActivity();
		getLoaderManager().initLoader(LOADER_STORAGE_STATS, null, this).forceLoad();
	}

	@Override
	public void scrollToTop() {
		mScrollView.smoothScrollTo(0, 0);
	}

	@Override
	public void onClick(View v) {
		final Context context = v.getContext();
		if (v.getId() == R.id.button_clear_cache) {
			new CacheClearTask(context, this).start();
		}
	}

	@Override
	public Loader<StorageStats> onCreateLoader(int id, Bundle args) {
		return new StorageStatsLoader(getActivity());
	}

	@Override
	public void onLoadFinished(Loader<StorageStats> loader, StorageStats data) {
		mTextViewStorageTotal.setText(TextUtils.formatFileSize(data.total()));
		mTextViewStorageCache.setText(TextUtils.formatFileSize(data.cacheSize));
		mTextViewStorageManga.setText(TextUtils.formatFileSize(data.savedSize));
	}

	@Override
	public void onLoaderReset(Loader<StorageStats> loader) {

	}

	@Override
	public void onCacheSizeChanged(long newSize) {
		if (newSize == -1) {
			Snackbar.make(mScrollView, R.string.error_occurred, Snackbar.LENGTH_SHORT).show();
		} else {
			mTextViewStorageCache.setText(TextUtils.formatFileSize(newSize));
			getLoaderManager().getLoader(LOADER_STORAGE_STATS).onContentChanged();
		}
	}
}
