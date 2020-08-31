package org.xtimms.kitsune.ui.filepicker;

import android.Manifest;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;
import org.xtimms.kitsune.core.common.base.AppBaseActivity;
import org.xtimms.kitsune.R;
import org.xtimms.kitsune.core.common.WeakAsyncTask;
import org.xtimms.kitsune.utils.LayoutUtils;
import org.xtimms.kitsune.utils.ResourceUtils;
import org.xtimms.kitsune.core.ListWrapper;
import org.xtimms.kitsune.core.models.FileDesc;
import org.xtimms.kitsune.core.models.MangaHeader;
import org.xtimms.kitsune.source.ZipArchive;
import org.xtimms.kitsune.core.storage.FlagsStorage;
import org.xtimms.kitsune.ui.preview.PreviewActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

@SuppressWarnings("ALL")
public final class FilePickerActivity extends AppBaseActivity implements LoaderManager.LoaderCallbacks<ListWrapper<FileDesc>>,
		OnFileSelectListener {

	private static final int REQUEST_CODE_PERMISSION = 14;

	private RecyclerView mRecyclerView;
	private TextView mTextViewHolder;
	private ContentLoadingProgressBar mProgressBar;
	private final ArrayList<FileDesc> mDataset = new ArrayList<>();
	private FilePickerAdapter mAdapter;
	private File mRoot;
	private boolean mFilterFiles;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_filepicker);
		setSupportActionBar(R.id.toolbar);
		enableHomeAsClose();

		mTextViewHolder = findViewById(R.id.textView_holder);
		mProgressBar = findViewById(R.id.progressBar);
		mRecyclerView = findViewById(R.id.recyclerView);
		mRecyclerView.setHasFixedSize(true);
		mRecyclerView.setLayoutManager(ResourceUtils.isLandscape(getResources()) ?
				new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
				: new LinearLayoutManager(this));
		mAdapter = new FilePickerAdapter(mDataset, this);
		mRecyclerView.setAdapter(mAdapter);

		mFilterFiles = FlagsStorage.get(this).isPickerFilterFiles();

		checkPermissions(REQUEST_CODE_PERMISSION, Manifest.permission.READ_EXTERNAL_STORAGE);
	}

	@Override
	public void onConfigurationChanged(@NotNull Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mRecyclerView.setLayoutManager(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE ?
				new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
				: new LinearLayoutManager(this));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.options_file_picker, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.findItem(R.id.option_filter).setChecked(mFilterFiles);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.option_filter) {
			mFilterFiles = !item.isChecked();
			item.setChecked(mFilterFiles);
			FlagsStorage.get(this).setPickerFilterFiles(mFilterFiles);
			onFileSelected(mRoot);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@NonNull
	@Override
	public Loader<ListWrapper<FileDesc>> onCreateLoader(int id, Bundle args) {
		return new FileListLoader(this, args.getString("path"), args.getBoolean("filter"));
	}

	@Override
	public void onLoadFinished(Loader<ListWrapper<FileDesc>> loader, ListWrapper<FileDesc> data) {
		mDataset.clear();
		mProgressBar.hide();
		if (data.isSuccess()) {
			mDataset.addAll(data.get());
			mTextViewHolder.setVisibility(mDataset.isEmpty() ? View.VISIBLE : View.GONE);
			FlagsStorage.get(this).setLastPickerDir(mRoot);
		} else {
			mTextViewHolder.setVisibility(View.VISIBLE);
		}
		mAdapter.notifyDataSetChanged();
		LayoutUtils.setSelectionFromTop(mRecyclerView, 0);
	}

	@Override
	public void onLoaderReset(Loader<ListWrapper<FileDesc>> loader) {

	}

	@Override
	protected void onPermissionGranted(int requestCode, String permission) {
		mProgressBar.show();
		mRoot = FlagsStorage.get(this).getLastPickerRoot(Environment.getExternalStorageDirectory());
		setSubtitle(mRoot.getPath());
		final Bundle args = new Bundle(2);
		args.putString("path", mRoot.getAbsolutePath());
		args.putBoolean("filter", mFilterFiles);
		getLoaderManager().initLoader(0, args, this).forceLoad();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onFileSelected(@NonNull File file) {
		if (file.isDirectory()) {
			mRoot = file;
			setSubtitle(mRoot.getPath());
			mProgressBar.show();
			mTextViewHolder.setVisibility(View.GONE);
			final Bundle args = new Bundle(2);
			args.putString("path", file.getAbsolutePath());
			args.putBoolean("filter", mFilterFiles);
			getLoaderManager().restartLoader(0, args, this).forceLoad();
		} else if (file.isFile()) {
			new MangaOpenTask(this)
					.start(Uri.fromFile(file));
		}
	}

	@Override
	public void onBackPressed() {
		mRoot = mRoot.getParentFile();
		if (mRoot != null && mRoot.exists() && mRoot.canRead()) {
			onFileSelected(mRoot);
		} else {
			super.onBackPressed();
		}
	}

	private static class MangaOpenTask extends WeakAsyncTask<FilePickerActivity, Uri, Void, MangaHeader>
			implements DialogInterface.OnCancelListener {

		private final ProgressDialog mProgressDialog;

		MangaOpenTask(FilePickerActivity filePickerActivity) {
			super(filePickerActivity);
			mProgressDialog = new ProgressDialog(filePickerActivity);
			mProgressDialog.setCancelable(true);
			mProgressDialog.setOnCancelListener(this);
			mProgressDialog.setMessage(filePickerActivity.getString(R.string.loading));
		}

		@Override
		protected void onPreExecute(@NonNull FilePickerActivity filePickerActivity) {
			mProgressDialog.show();
		}

		@Override
		protected MangaHeader doInBackground(Uri... uris) {
			try {
				return ZipArchive.getManga(Objects.requireNonNull(getObject()), uris[0]);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

		@Override
		protected void onPostExecute(@NonNull FilePickerActivity filePickerActivity, MangaHeader manga) {
			mProgressDialog.dismiss();
			if (manga == null) {
				new AlertDialog.Builder(filePickerActivity)
						.setMessage(R.string.invalid_file_not_supported)
						.setNegativeButton(R.string.close, null)
						.create()
						.show();
			} else {
				filePickerActivity.startActivity(new Intent(filePickerActivity, PreviewActivity.class)
						.putExtra("manga", manga));
			}
		}

		@Override
		public void onCancel(DialogInterface dialog) {
			if (canCancel()) {
				cancel(false);
			}
		}
	}
}
