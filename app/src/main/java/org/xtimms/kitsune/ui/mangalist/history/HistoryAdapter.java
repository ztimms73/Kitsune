package org.xtimms.kitsune.ui.mangalist.history;

import android.content.Context;
import android.content.Intent;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import org.xtimms.kitsune.R;
import org.xtimms.kitsune.core.common.DataViewHolder;
import org.xtimms.kitsune.utils.ImageUtils;
import org.xtimms.kitsune.utils.ResourceUtils;
import org.xtimms.kitsune.core.MangaStatus;
import org.xtimms.kitsune.core.models.MangaHistory;
import org.xtimms.kitsune.source.MangaProvider;
import org.xtimms.kitsune.ui.preview.PreviewActivity;

import java.util.ArrayList;

final class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryHolder> {

	private final ArrayList<MangaHistory> mDataset;
	private boolean mDetailed;

	HistoryAdapter(ArrayList<MangaHistory> dataset, boolean detailed) {
		setHasStableIds(true);
		mDataset = dataset;
		mDetailed = detailed;
	}

	@Override
	public HistoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
		return mDetailed ?
				new DetailedHistoryHolder(inflater.inflate(R.layout.item_history_detailed, parent, false)) :
				new HistoryHolder(inflater.inflate(R.layout.item_history, parent, false));
	}

	@Override
	public void onBindViewHolder(HistoryHolder holder, int position) {
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

	@Override
	public void onViewRecycled(HistoryHolder holder) {
		holder.recycle();
	}

	public void setIsDetailed(boolean detailed) {
		mDetailed = detailed;
	}

	public boolean isDetailed() {
		return mDetailed;
	}

	static class HistoryHolder extends DataViewHolder<MangaHistory> implements View.OnClickListener {

		private final ImageView mImageView;
		private final TextView mText1;
		private final TextView mText2;

		private HistoryHolder(View itemView) {
			super(itemView);
			mText1 = itemView.findViewById(android.R.id.text1);
			mText2 = itemView.findViewById(android.R.id.text2);
			mImageView = itemView.findViewById(R.id.imageView);
			itemView.setOnClickListener(this);
		}

		@Override
		public void bind(MangaHistory item) {
			super.bind(item);
			mText1.setText(item.name);
			mText2.setText(ResourceUtils.formatTimeRelative(item.updatedAt));
			ImageUtils.setThumbnail(mImageView, item.thumbnail, MangaProvider.getDomain(item.provider));
		}

		@Override
		public void recycle() {
			super.recycle();
			ImageUtils.recycle(mImageView);
		}

		@Override
		public void onClick(View view) {
			final Context context = view.getContext();
			final MangaHistory item = getData();
			if (item != null) {
				context.startActivity(new Intent(context.getApplicationContext(), PreviewActivity.class)
						.putExtra("manga", item));
			}
		}
	}

	@SuppressWarnings("IntegerDivisionInFloatingPointContext")
	static class DetailedHistoryHolder extends HistoryHolder {

		private final RatingBar mRatingBar;
		private final TextView mTextViewStatus;
		private final TextView mTextViewGenres;

		private DetailedHistoryHolder(View itemView) {
			super(itemView);
			mRatingBar = itemView.findViewById(R.id.ratingBar);
			mTextViewStatus = itemView.findViewById(R.id.textView_status);
			mTextViewGenres = itemView.findViewById(R.id.genres);
		}

		@Override
		public void bind(MangaHistory item) {
			super.bind(item);
			mTextViewGenres.setText(item.genres);
			if (item.rating == 0) {
				mRatingBar.setVisibility(View.GONE);
			} else {
				mRatingBar.setVisibility(View.VISIBLE);
				mRatingBar.setRating(item.rating / 20);
			}
			switch (item.status) {
				case MangaStatus.STATUS_ONGOING:
					mTextViewStatus.setVisibility(View.VISIBLE);
					mTextViewStatus.setText(R.string.status_ongoing);
					break;
				case MangaStatus.STATUS_COMPLETED:
					mTextViewStatus.setVisibility(View.VISIBLE);
					mTextViewStatus.setText(R.string.status_completed);
					break;
				case MangaStatus.STATUS_LICENSED:
					mTextViewStatus.setVisibility(View.VISIBLE);
					mTextViewStatus.setText(R.string.status_licensed);
					break;
				default:
					mTextViewStatus.setVisibility(View.GONE);

				case MangaStatus.STATUS_UNKNOWN:
					break;
			}
		}

		@Override
		public void onClick(View v) {
			final Context context = v.getContext();
			final MangaHistory item = getData();
			if (item != null) {
				context.startActivity(new Intent(context.getApplicationContext(), PreviewActivity.class)
						.putExtra("manga", item));
			}
		}
	}
}
