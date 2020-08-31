package org.xtimms.kitsune.ui.shelf;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;
import org.xtimms.kitsune.R;
import org.xtimms.kitsune.core.common.DataViewHolder;
import org.xtimms.kitsune.core.common.Dismissible;
import org.xtimms.kitsune.utils.ImageUtils;
import org.xtimms.kitsune.utils.ResourceUtils;
import org.xtimms.kitsune.core.models.ListHeader;
import org.xtimms.kitsune.core.models.MangaFavourite;
import org.xtimms.kitsune.core.models.MangaHeader;
import org.xtimms.kitsune.core.models.MangaHistory;
import org.xtimms.kitsune.core.models.UserTip;
import org.xtimms.kitsune.source.MangaProvider;
import org.xtimms.kitsune.ui.mangalist.favourites.FavouritesActivity;
import org.xtimms.kitsune.ui.mangalist.history.HistoryActivity;
import org.xtimms.kitsune.ui.preview.PreviewActivity;
import org.xtimms.kitsune.ui.reader.ReaderActivity;
import org.xtimms.kitsune.core.storage.SavedMangaActivity;

import java.util.ArrayList;

public final class ShelfAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	private final ArrayList<Object> mDataset;
	private final OnTipsActionListener mActionListener;

	public ShelfAdapter(OnTipsActionListener listener) {
		mDataset = new ArrayList<>();
		mActionListener = listener;
		setHasStableIds(true);
	}

	void updateData(ArrayList<Object> data) {
		mDataset.clear();
		mDataset.addAll(data);
		notifyDataSetChanged();
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, @ShelfItemType int viewType) {
		final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
		switch (viewType) {
			case ShelfItemType.TYPE_HEADER:
				return new HeaderHolder(inflater.inflate(R.layout.header_group_button, parent, false));
			case ShelfItemType.TYPE_ITEM_DEFAULT:
				return new MangaHolder(inflater.inflate(R.layout.item_manga, parent, false));
			case ShelfItemType.TYPE_RECENT:
				return new RecentHolder(inflater.inflate(R.layout.item_manga_recent, parent, false));
			case ShelfItemType.TYPE_ITEM_SMALL:
				return new MangaHolder(inflater.inflate(R.layout.item_manga_small, parent, false));
			case ShelfItemType.TYPE_TIP:
				return new TipHolder(inflater.inflate(R.layout.item_tip, parent, false));
			case ShelfItemType.TYPE_SAVED:
				return new SavedMangaHolder(inflater.inflate(R.layout.item_manga_saved_shelf, parent, false));
			default:
				throw new AssertionError("Unknown viewType");
		}
	}

	@Override
	public void onBindViewHolder(@NotNull RecyclerView.ViewHolder holder, int position) {
		if (holder instanceof TipHolder) {
			((TipHolder) holder).bind((UserTip) mDataset.get(position));
		} else if (holder instanceof HeaderHolder) {
			ListHeader item = (ListHeader) mDataset.get(position);
			if (item.text != null) {
				((HeaderHolder) holder).textView.setText(item.text);
			} else if (item.textResId != 0) {
				((HeaderHolder) holder).textView.setText(item.textResId);
			} else {
				((HeaderHolder) holder).textView.setText(null);
			}
			holder.itemView.setTag(item.extra);
		} else if (holder instanceof MangaHolder) {
			MangaHeader item = (MangaHeader) mDataset.get(position);
			ImageUtils.setThumbnail(((MangaHolder) holder).imageViewThumbnail, item.thumbnail, MangaProvider.getDomain(item.provider));
			((MangaHolder) holder).textViewTitle.setText(item.name);
			holder.itemView.setTag(item);
			if (holder instanceof RecentHolder) {
				MangaHistory history = (MangaHistory) item;
				((RecentHolder) holder).textViewSubtitle.setText(item.summary);
				if (item.summary == null || item.summary.isEmpty()) {
					((RecentHolder) holder).textViewSubtitle.setVisibility(View.GONE);
				}
				((RecentHolder) holder).textViewStatus.setText(ResourceUtils.formatTimeRelative(history.updatedAt));
			}
		}
	}

	@ShelfItemType
	@Override
	public int getItemViewType(int position) {
		Object item = mDataset.get(position);
		if (item instanceof ListHeader) {
			return ShelfItemType.TYPE_HEADER;
		} else if (item instanceof UserTip) {
			return ShelfItemType.TYPE_TIP;
		} else if (item instanceof MangaHistory) {
			return ShelfItemType.TYPE_RECENT;
		} else if (item instanceof MangaFavourite) {
			return ShelfItemType.TYPE_ITEM_DEFAULT;
		} else if (item instanceof MangaHeader) {
			return ShelfItemType.TYPE_ITEM_SMALL;
		} else {
			throw new AssertionError("Unknown viewType");
		}
	}

	@Override
	public int getItemCount() {
		return mDataset.size();
	}

	@Override
	public long getItemId(int position) {
		Object item = mDataset.get(position);
		if (item instanceof MangaHeader) {
			return ((MangaHeader) item).id;
		} else if (item instanceof ListHeader) {
			final String text = ((ListHeader) item).text;
			return text != null ? text.hashCode() : ((ListHeader) item).textResId;
		} else if (item instanceof UserTip) {
			return ((UserTip) item).title.hashCode() + ((UserTip) item).content.hashCode();
		} else {
			throw new AssertionError("Unknown viewType");
		}
	}

	@Override
	public void onViewRecycled(@NotNull RecyclerView.ViewHolder holder) {
		if (holder instanceof MangaHolder) {
			ImageUtils.recycle(((MangaHolder) holder).imageViewThumbnail);
		}
		super.onViewRecycled(holder);
	}

	static class HeaderHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

		final TextView textView;
		final Button buttonMore;

		HeaderHolder(View itemView) {
			super(itemView);
			textView = itemView.findViewById(R.id.textView);
			buttonMore = itemView.findViewById(R.id.button_more);
			buttonMore.setOnClickListener(this);
		}

		@Override
		public void onClick(View v) {
			final Object extra = itemView.getTag();
			if (extra == null) {
				return;
			}
			final Context context = v.getContext();
			if (extra.equals(ShelfContent.SECTION_HISTORY)) {
				context.startActivity(new Intent(context, HistoryActivity.class));
			} else if (extra.equals(ShelfContent.SECTION_SAVED)) {
				context.startActivity(new Intent(context, SavedMangaActivity.class));
			} else if (extra instanceof Integer) {
				context.startActivity(new Intent(context, FavouritesActivity.class)
					.putExtra("category_id", (Integer) extra));
			}
		}
	}

	class TipHolder extends DataViewHolder<UserTip> implements View.OnClickListener, Dismissible {

		private final TextView textViewTitle;
		private final TextView textViewContent;
		private final ImageView imageViewIcon;
		private final Button buttonAction;
		private final Button buttonDismiss;

		TipHolder(View itemView) {
			super(itemView);
			textViewTitle = itemView.findViewById(android.R.id.text1);
			textViewContent = itemView.findViewById(android.R.id.text2);
			buttonAction = itemView.findViewById(android.R.id.button1);
			buttonDismiss = itemView.findViewById(android.R.id.closeButton);
			imageViewIcon = itemView.findViewById(android.R.id.icon);
			buttonAction.setOnClickListener(this);
			buttonDismiss.setOnClickListener(this);
		}

		@Override
		public void bind(UserTip userTip) {
			super.bind(userTip);
			textViewTitle.setText(userTip.title);
			textViewContent.setText(userTip.content);
			if (userTip.hasIcon()) {
				imageViewIcon.setImageResource(userTip.icon);
				imageViewIcon.setVisibility(View.VISIBLE);
			} else {
				imageViewIcon.setVisibility(View.GONE);
			}
			if (userTip.hasAction()) {
				buttonAction.setText(userTip.actionText);
				buttonAction.setTag(userTip.actionId);
				buttonAction.setVisibility(View.VISIBLE);
			} else {
				buttonAction.setVisibility(View.GONE);
			}
			buttonDismiss.setVisibility(userTip.hasDismissButton() ? View.VISIBLE : View.GONE);
		}

		boolean isDismissible() {
			final UserTip data = getData();
			return data != null && data.isDismissible();
		}

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
				case android.R.id.button1:
					Object tag = v.getTag();
					if (tag instanceof Integer) {
						mActionListener.onTipActionClick((Integer) tag);
					}
				case android.R.id.closeButton:
					dismiss();
					break;
			}
		}

		@Override
		public void dismiss() {
			Object tag = buttonAction.getTag();
			if (tag instanceof Integer) {
				mActionListener.onTipDismissed((Integer) tag);
			}
			mDataset.remove(getAdapterPosition());
			notifyDataSetChanged();
		}
	}

	static class MangaHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

		final ImageView imageViewThumbnail;
		final TextView textViewTitle;

		MangaHolder(View itemView) {
			super(itemView);
			imageViewThumbnail = itemView.findViewById(R.id.imageViewThumbnail);
			textViewTitle = itemView.findViewById(R.id.textViewTitle);
			itemView.setOnClickListener(this);
		}

		@Override
		public void onClick(View view) {
			MangaHeader mangaHeader = (MangaHeader) itemView.getTag();
			final Context context = view.getContext();
			context.startActivity(new Intent(context.getApplicationContext(), PreviewActivity.class)
					.putExtra("manga", mangaHeader));
		}
	}

	static class RecentHolder extends MangaHolder {

		final TextView textViewStatus;
		final TextView textViewSubtitle;

		RecentHolder(View itemView) {
			super(itemView);
			textViewStatus = itemView.findViewById(R.id.textView_status);
			textViewSubtitle = itemView.findViewById(R.id.textView_subtitle);
			itemView.findViewById(R.id.button_continue).setOnClickListener(this);
		}

		@Override
		public void onClick(View view) {
			if (view.getId() == R.id.button_continue) {
				MangaHeader mangaHeader = (MangaHeader) itemView.getTag();
				final Context context = view.getContext();
				context.startActivity(new Intent(context.getApplicationContext(), ReaderActivity.class)
						.setAction(ReaderActivity.ACTION_READING_CONTINUE)
						.putExtra("manga", mangaHeader));
			} else {
				super.onClick(view);
			}
		}
	}

	static class SavedMangaHolder extends MangaHolder {

		final ImageView imageViewThumbnail;
		final TextView textViewTitle;

		SavedMangaHolder(@NonNull View itemView) {
			super(itemView);
			imageViewThumbnail = itemView.findViewById(R.id.imageViewThumbnail);
			textViewTitle = itemView.findViewById(R.id.textViewTitle);
			itemView.setOnClickListener(this);
		}

		@Override
		public void onClick(View view) {
			final Object extra = itemView.getTag();
			if (extra == null) {
				return;
			}
			final Context context = view.getContext();
			if (extra.equals(ShelfContent.SECTION_SAVED)) {
				context.startActivity(new Intent(context, SavedMangaActivity.class));
			}

		}
	}
}
