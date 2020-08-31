package org.xtimms.kitsune.ui.mangalist.favourites;

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
import org.xtimms.kitsune.utils.ThemeUtils;
import org.xtimms.kitsune.core.MangaStatus;
import org.xtimms.kitsune.core.models.MangaFavourite;
import org.xtimms.kitsune.source.MangaProvider;
import org.xtimms.kitsune.ui.preview.PreviewActivity;

import java.util.ArrayList;

final class FavouritesAdapter extends RecyclerView.Adapter<FavouritesAdapter.FavouriteHolder> {

	private final ArrayList<MangaFavourite> mDataset;

	FavouritesAdapter(ArrayList<MangaFavourite> dataset) {
		setHasStableIds(true);
		mDataset = dataset;
	}

	@Override
	public FavouriteHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return new FavouriteHolder(LayoutInflater.from(parent.getContext())
				.inflate(R.layout.item_manga_detailed, parent, false));
	}

	@Override
	public void onBindViewHolder(FavouriteHolder holder, int position) {
		MangaFavourite item = mDataset.get(position);
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
		//LayoutUtils.setTextOrHide(holder.text2, item.summary);
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
	public void onViewRecycled(FavouriteHolder holder) {
		ImageUtils.recycle(holder.imageView);
		super.onViewRecycled(holder);
	}

	class FavouriteHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

		final TextView text1;
		final TextView text2;
		final TextView ratingText;
		final TextView status;
		final ImageView imageView;

		FavouriteHolder(View itemView) {
			super(itemView);
			text1 = itemView.findViewById(android.R.id.text1);
			text2 = itemView.findViewById(android.R.id.text2);
			imageView = itemView.findViewById(R.id.imageView);
			status = itemView.findViewById(R.id.status);
			ratingText = itemView.findViewById(R.id.ratingText);
			int mPrimaryColor = ThemeUtils.getThemeAttrColor(itemView.getContext(), R.attr.colorPrimary);
			int mAccentColor = ThemeUtils.getThemeAttrColor(itemView.getContext(), R.attr.colorAccent);

			itemView.setOnClickListener(this);
		}

		@Override
		public void onClick(View view) {
			final Context context = view.getContext();
			final MangaFavourite item = mDataset.get(getAdapterPosition());
			context.startActivity(new Intent(context.getApplicationContext(), PreviewActivity.class)
					.putExtra("manga", item));
		}
	}

}
