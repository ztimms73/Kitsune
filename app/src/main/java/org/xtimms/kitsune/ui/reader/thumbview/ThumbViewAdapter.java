package org.xtimms.kitsune.ui.reader.thumbview;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.xtimms.kitsune.R;
import org.xtimms.kitsune.core.common.DataViewHolder;
import org.xtimms.kitsune.utils.ImageUtils;
import org.xtimms.kitsune.utils.MetricsUtils;
import org.xtimms.kitsune.utils.network.NetworkUtils;
import org.xtimms.kitsune.core.models.MangaPage;
import org.xtimms.kitsune.source.MangaProvider;
import org.xtimms.kitsune.ui.reader.loader.PagesCache;

import java.io.File;
import java.util.ArrayList;

final class ThumbViewAdapter extends RecyclerView.Adapter<ThumbViewAdapter.ThumbHolder> {

	private final ArrayList<MangaPage> mDataset;
	private final OnThumbnailClickListener mClickListener;
	private final PagesCache mCache;

	ThumbViewAdapter(Context context, ArrayList<MangaPage> pages, OnThumbnailClickListener onClickListener) {
		mDataset = pages;
		mClickListener = onClickListener;
		mCache = PagesCache.getInstance(context);
		setHasStableIds(true);
	}

	@Override
	public ThumbHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return new ThumbHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_thumb, parent, false));
	}

	@Override
	public void onBindViewHolder(ThumbHolder holder, int position) {
		holder.bind(mDataset.get(position));
	}

	@Override
	public int getItemCount() {
		return mDataset.size();
	}

	@Override
	public long getItemId(int position) {
		return mDataset.get(position).id;
	}

	final class ThumbHolder extends DataViewHolder<MangaPage> implements View.OnClickListener {

		private final ImageView mImageView;
		private final TextView mTextView;

        ThumbHolder(View itemView) {
			super(itemView);
			mImageView = itemView.findViewById(R.id.imageView);
			mTextView = itemView.findViewById(R.id.textView);
            View mSelector = itemView.findViewById(R.id.selector);
			mSelector.setOnClickListener(this);
		}

		@Override
		public void bind(MangaPage mangaPage) {
			super.bind(mangaPage);
			final MetricsUtils.Size size = MetricsUtils.getPreferredCellSizeMedium(getContext().getResources());
			mTextView.setText(String.valueOf(getAdapterPosition() + 1));
			final File file = mCache.getFileForUrl(mangaPage.url);
			if (file.exists()) {
				ImageUtils.setThumbnailCropped(mImageView, mCache.getFileForUrl(mangaPage.url), size);
			} else if (NetworkUtils.isNetworkAvailable(getContext(), false)) {
				ImageUtils.setThumbnailCropped(mImageView, mangaPage.url, size, MangaProvider.getDomain(mangaPage.provider));
			} else {
				ImageUtils.setEmptyThumbnail(mImageView);
			}
		}

		@Override
		public void onClick(View v) {
			mClickListener.onThumbnailClick(getAdapterPosition());
		}
	}
}
