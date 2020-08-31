package org.xtimms.kitsune.ui.filepicker;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.xtimms.kitsune.R;
import org.xtimms.kitsune.core.common.DataViewHolder;
import org.xtimms.kitsune.core.common.WeakAsyncTask;
import org.xtimms.kitsune.utils.MetricsUtils;
import org.xtimms.kitsune.core.models.FileDesc;
import org.xtimms.kitsune.source.ZipArchive;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

final class FilePickerAdapter extends RecyclerView.Adapter<FilePickerAdapter.FileViewHolder> {

	private final ArrayList<FileDesc> mDataset;
	private final OnFileSelectListener mListener;

	FilePickerAdapter(ArrayList<FileDesc> dataset, OnFileSelectListener listener) {
		mDataset = dataset;
		mListener = listener;
	}

	@Override
	public FileViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return new FileViewHolder(LayoutInflater.from(parent.getContext())
				.inflate(R.layout.item_file, parent, false));
	}

	@Override
	public void onBindViewHolder(FileViewHolder holder, int position) {
		holder.bind(mDataset.get(position));
	}

	@Override
	public int getItemCount() {
		return mDataset.size();
	}

	class FileViewHolder extends DataViewHolder<FileDesc> implements View.OnClickListener {

		private final ImageView mIcon;
		private final TextView mText1;
		private final TextView mText2;

		FileViewHolder(View itemView) {
			super(itemView);
			mIcon = itemView.findViewById(android.R.id.icon);
			mText1 = itemView.findViewById(android.R.id.text1);
			mText2 = itemView.findViewById(android.R.id.text2);
			itemView.setOnClickListener(this);
		}

		@Override
		public void bind(FileDesc desc) {
			super.bind(desc);
			if (desc.file.isDirectory()) {
				mIcon.setImageResource(R.drawable.ic_folder_white);
				mText2.setText(itemView.getResources().getQuantityString(
						R.plurals.files_count,
						desc.entryCount,
						desc.entryCount
				));
				mText2.setVisibility(View.VISIBLE);
			} else {
				if (ZipArchive.isFileSupported(desc.file)) {
					new ThumbnailTask(this, desc.file.getAbsolutePath().hashCode())
							.start(desc.file);
				} else {
					mIcon.setImageResource(R.drawable.ic_file_white);
				}
				mText2.setVisibility(View.GONE);
			}
			mText1.setText(desc.file.getName());
		}

		@Override
		public void onClick(View v) {
			final FileDesc desc = getData();
			if (desc != null) {
				mListener.onFileSelected(desc.file);
			}
		}
	}

	private static class ThumbnailTask extends WeakAsyncTask<FileViewHolder, File, Void, Drawable> {

		private final Integer mId;
		private final MetricsUtils.Size mSize;

		public ThumbnailTask(FileViewHolder fileViewHolder, int id) {
			super(fileViewHolder);
			fileViewHolder.mIcon.setImageResource(R.drawable.ic_file_image_white);
			mId = id;
			fileViewHolder.mIcon.setTag(mId);
			mSize = MetricsUtils.Size.fromLayoutParams(fileViewHolder.mIcon);
		}

		@Nullable
		@Override
		protected Drawable doInBackground(File... files) {
			try {
				@SuppressLint("WrongThread") final File thumb = ZipArchive.createThumbnail(
						Objects.requireNonNull(getObject()).itemView.getContext(),
						Uri.fromFile(files[0]),
						mSize
				);
				if (thumb == null) {
					return null;
				} else {
					@SuppressLint("WrongThread") RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(
							getObject().itemView.getResources(),
							thumb.getPath()
					);
					drawable.setCornerRadius(mSize.width / 2.f);
					return drawable;
				}
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

		@Override
		protected void onPostExecute(@NonNull FileViewHolder fileViewHolder, @Nullable Drawable drawable) {
			if (mId.equals(fileViewHolder.mIcon.getTag()) && drawable != null) {
				fileViewHolder.mIcon.setImageDrawable(drawable);
			}
		}
	}
}
