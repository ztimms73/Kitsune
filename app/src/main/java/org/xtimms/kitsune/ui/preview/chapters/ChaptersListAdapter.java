package org.xtimms.kitsune.ui.preview.chapters;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.xtimms.kitsune.core.helpers.AppHelper;
import org.xtimms.kitsune.R;
import org.xtimms.kitsune.utils.ThemeUtils;
import org.xtimms.kitsune.core.models.MangaChapter;

import java.util.ArrayList;
import java.util.Collections;

import static android.view.View.GONE;

public final class ChaptersListAdapter extends RecyclerView.Adapter<ChaptersListAdapter.ChapterHolder> {

	private final ArrayList<MangaChapter> mDataset;
	private final OnChapterClickListener mClickListener;
	private long mCurrentId;
	private boolean mReversed = false;
	private final Drawable[] mIcons;

	private int mLastNumber;
	private final int[] mColors;

	public ChaptersListAdapter(Context context, ArrayList<MangaChapter> dataset, OnChapterClickListener listener) {
		mClickListener = listener;
		mDataset = dataset;
		mCurrentId = 0;
		mLastNumber = -1;
		setHasStableIds(true);
		mIcons = new Drawable[] {
				ContextCompat.getDrawable(context, R.drawable.ic_play_green),
				ContextCompat.getDrawable(context, R.drawable.ic_save)
		};
		mColors = new int[] {
				ThemeUtils.getAttrColor(context, android.R.attr.textColorPrimary),
				ThemeUtils.getAttrColor(context, android.R.attr.textColorSecondary)
		};
	}

	public void setCurrentChapterId(long id) {
		mCurrentId = id;
	}

	public void reverse() {
		Collections.reverse(mDataset);
		notifyDataSetChanged();
		mReversed = !mReversed;
	}

	public boolean isReversed() {
		return mReversed;
	}

	@Override
	public ChapterHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return new ChapterHolder(LayoutInflater.from(parent.getContext())
				.inflate(R.layout.item_chapter, parent, false), mClickListener);
	}

	@Override
	public void onBindViewHolder(ChapterHolder holder, int position) {
		MangaChapter ch = mDataset.get(position - (mLastNumber >= 0 ? 1 : 0));
		holder.chapter_title.setText(ch.name);
		holder.chapter_title.setTextColor(ch.number >= mLastNumber ? mColors[0] : mColors[1]);
		holder.chapter_title.setTypeface(ch.number == mLastNumber ? Typeface.DEFAULT_BOLD : Typeface.DEFAULT);
		if (ch.date > 0) {
			holder.chapter_date.setText(AppHelper.getReadableDateTimeForChapters(ch.date));
		} else {
			holder.chapter_date.setText(null);
		}
		if (ch.id == mCurrentId) {
			holder.chapter_play.setImageDrawable(mIcons[0]);
		} else {
			holder.chapter_play.setImageDrawable(null);
		}
		if (ch.isSaved()) {
			holder.download_text.setVisibility(View.VISIBLE);
		} else {
			holder.download_text.setVisibility(GONE);
		}
		holder.chapter_scanlator.setText(ch.scanlator);
		//TextViewCompat.setCompoundDrawablesRelativeWithIntrinsicBounds(
		//		holder.chapter_title,
		//		ch.id == mCurrentId ? mIcons[0] : null,
		//		null,
		//		ch.isSaved() ? mIcons[1] : null,
		//		null
		//);
	}

	@Override
	public long getItemId(int position) {
		if (mLastNumber >= 0) {
			return position == 0 ? 0 : mDataset.get(position - 1).id;
		} else {
			return mDataset.get(position).id;
		}
	}

	@Override
	public int getItemCount() {
		return mDataset.size() + (mLastNumber >= 0 ? 1 : 0);
	}

	public int findByNameContains(String what) {
		final String whatLower = what.toLowerCase();
		for (int i = 0, datasetSize = mDataset.size(); i < datasetSize; i++) {
			MangaChapter o = mDataset.get(i);
			if (o.name.toLowerCase().contains(whatLower)) {
				return i;
			}
		}
		return -1;
	}

	class ChapterHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

		private final OnChapterClickListener mListener;
		private final TextView chapter_title;
		private final TextView chapter_date;
		private final ImageView chapter_play;
		private final TextView chapter_scanlator;
		private final TextView download_text;

		ChapterHolder(View itemView, OnChapterClickListener listener) {
			super(itemView);
			chapter_title = itemView.findViewById(R.id.chapter_title);
			chapter_date = itemView.findViewById(R.id.chapter_date);
			chapter_play = itemView.findViewById(R.id.chapter_play);
			chapter_scanlator = itemView.findViewById(R.id.chapter_scanlator);
			download_text = itemView.findViewById(R.id.download_text);
			itemView.setOnClickListener(this);
			itemView.setOnLongClickListener(this);
			mListener = listener;
		}

		public TextView getTextView() {
			return (TextView) itemView;
		}

		@Override
		public void onClick(View v) {
			final int pos = getAdapterPosition();
			//mListener.onChapterClick(pos, mDataset.get(pos));
			if (mListener != null) {
				if (mLastNumber < 0) {
					mListener.onChapterClick(pos, mDataset.get(pos));
				} else {
					if (pos == -1) {
						mListener.onChapterClick(-1, null);
					} else {
						mListener.onChapterClick(pos - 1, mDataset.get(pos - 1));
					}
				}
			}
		}

		@Override
		public boolean onLongClick(View view) {
			final int pos = getAdapterPosition();
			return mListener.onChapterLongClick(pos, mDataset.get(pos));
		}
	}

	public interface OnChapterClickListener {
		void onChapterClick(int pos, MangaChapter chapter);
		boolean onChapterLongClick(int pos, MangaChapter chapter);
	}
}
