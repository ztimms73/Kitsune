package org.xtimms.kitsune.core.storage;

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
import org.xtimms.kitsune.core.models.SavedManga;
import org.xtimms.kitsune.source.MangaProvider;
import org.xtimms.kitsune.ui.preview.PreviewActivity;

import java.util.ArrayList;

public final class SavedMangaAdapter extends RecyclerView.Adapter<SavedMangaAdapter.MangaHolder> {

	private final ArrayList<SavedMangaSummary> mDataset;

	SavedMangaAdapter(ArrayList<SavedMangaSummary> dataset) {
		setHasStableIds(true);
		mDataset = dataset;
	}

	@Override
	public MangaHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return new MangaHolder(LayoutInflater.from(parent.getContext())
				.inflate(R.layout.item_manga_saved, parent, false));
	}

	@Override
	public void onBindViewHolder(MangaHolder holder, int position) {
		SavedMangaSummary item = mDataset.get(position);
		holder.text1.setText(item.manga.name);
		holder.text2.setText(item.savedChapters == -1 ? "" :
				holder.itemView.getResources().getQuantityString(R.plurals.chapters_saved_count, item.savedChapters, item.savedChapters));
		ImageUtils.setThumbnail(holder.imageView, item.manga.thumbnail, MangaProvider.getDomain(item.manga.provider));
		holder.itemView.setTag(item);
	}

	@Override
	public int getItemCount() {
		return mDataset.size();
	}

	@Override
	public long getItemId(int position) {
		return mDataset.get(position).manga.id;
	}

	@Override
	public void onViewRecycled(MangaHolder holder) {
		ImageUtils.recycle(holder.imageView);
		super.onViewRecycled(holder);
	}

	class MangaHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

		final TextView text1;
		final TextView text2;
		final ImageView imageView;

		private MangaHolder(View itemView) {
			super(itemView);
			text1 = itemView.findViewById(R.id.textViewTitle);
			text2 = itemView.findViewById(R.id.chapters_count);
			imageView = itemView.findViewById(R.id.imageViewThumbnail);

			itemView.setOnClickListener(this);
		}

		@Override
		public void onClick(View view) {
			final Context context = view.getContext();
			final SavedManga item = mDataset.get(getAdapterPosition()).manga;
			view.getId();
			context.startActivity((new Intent(context.getApplicationContext(), PreviewActivity.class)).putExtra("manga", item).putExtra("check_network", false));
		}
	}
}