package org.xtimms.kitsune.ui.recommendations;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.xtimms.kitsune.R;
import org.xtimms.kitsune.utils.ImageUtils;
import org.xtimms.kitsune.core.MangaStatus;
import org.xtimms.kitsune.core.models.MangaRecommendation;
import org.xtimms.kitsune.source.MangaProvider;
import org.xtimms.kitsune.ui.preview.PreviewActivity;

import java.util.ArrayList;

final class RecommendationsAdapter extends RecyclerView.Adapter<RecommendationsAdapter.RecommendationsHolder> {

	private final ArrayList<MangaRecommendation> mDataset;

	RecommendationsAdapter(ArrayList<MangaRecommendation> dataset) {
		setHasStableIds(true);
		mDataset = dataset;
	}

	@Override
	public RecommendationsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return new RecommendationsHolder(LayoutInflater.from(parent.getContext())
				.inflate(R.layout.item_manga_detailed, parent, false));
	}

	@Override
	public void onBindViewHolder(RecommendationsHolder holder, int position) {
		MangaRecommendation item = mDataset.get(position);
		holder.text1.setText(item.name);
		holder.text2.setText(item.summary);
		//holder.ratingBar.setRating((float) item.rating / 20);
		holder.ratingText.setText(String.valueOf((float) item.rating / 20));
		if (item.rating == 0) {
			holder.ratingText.setVisibility(View.GONE);
		} else {
			holder.ratingText.setVisibility(View.VISIBLE);
		}
		switch (item.status) {
			case MangaStatus.STATUS_ONGOING:
				holder.status.setVisibility(View.VISIBLE);
				holder.status.setText(R.string.status_ongoing);
				break;
			case MangaStatus.STATUS_COMPLETED:
				holder.status.setVisibility(View.VISIBLE);
				holder.status.setText(R.string.status_completed);
				break;
			case MangaStatus.STATUS_LICENSED:
				holder.status.setVisibility(View.VISIBLE);
				holder.status.setText(R.string.status_licensed);
				break;
			case MangaStatus.STATUS_UNKNOWN:
			default:
				holder.status.setVisibility(View.GONE);
				break;
		}
		ImageUtils.setThumbnail(holder.imageView, item.thumbnail, MangaProvider.getDomain(item.provider));
		holder.itemView.setTag(item);
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
	public void onViewRecycled(RecommendationsHolder holder) {
		ImageUtils.recycle(holder.imageView);
		super.onViewRecycled(holder);
	}

	class RecommendationsHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

		final TextView text1;
		final TextView text2;
		final TextView ratingText;
		final TextView status;
		final ImageView imageView;

		RecommendationsHolder(View itemView) {
			super(itemView);
			text1 = itemView.findViewById(android.R.id.text1);
			text2 = itemView.findViewById(android.R.id.text2);
			imageView = itemView.findViewById(R.id.imageView);
			status = itemView.findViewById(R.id.status);
			ratingText = itemView.findViewById(R.id.ratingText);

			itemView.setOnClickListener(this);
		}

		@Override
		public void onClick(View view) {
			final Context context = view.getContext();
			final MangaRecommendation item = mDataset.get(getAdapterPosition());
            context.startActivity(new Intent(context.getApplicationContext(), PreviewActivity.class)
                    .putExtra("manga", item));
        }
	}
}

