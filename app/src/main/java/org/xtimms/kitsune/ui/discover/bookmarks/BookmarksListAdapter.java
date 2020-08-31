package org.xtimms.kitsune.ui.discover.bookmarks;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.IntDef;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;
import org.xtimms.kitsune.R;
import org.xtimms.kitsune.core.common.DataViewHolder;
import org.xtimms.kitsune.utils.ImageUtils;
import org.xtimms.kitsune.utils.IntentUtils;
import org.xtimms.kitsune.utils.ResourceUtils;
import org.xtimms.kitsune.core.models.MangaBookmark;
import org.xtimms.kitsune.core.models.MangaHeader;
import org.xtimms.kitsune.core.models.UniqueObject;
import org.xtimms.kitsune.core.storage.files.ThumbnailsStorage;
import org.xtimms.kitsune.ui.preview.PreviewActivity;
import org.xtimms.kitsune.ui.reader.ReaderActivity;
import org.xtimms.kitsune.ui.reader.ToolButtonCompat;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;

final class BookmarksListAdapter extends RecyclerView.Adapter<DataViewHolder<? extends UniqueObject>> {

	private final ArrayList<UniqueObject> mDataset;
	private final ThumbnailsStorage mThumbStore;

	BookmarksListAdapter(ArrayList<UniqueObject> dataset, ThumbnailsStorage thumbnailsStorage) {
		mDataset = dataset;
		mThumbStore = thumbnailsStorage;
		setHasStableIds(true);
	}

	@Override
	public DataViewHolder<? extends UniqueObject> onCreateViewHolder(ViewGroup parent, @ItemViewType int viewType) {
		final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
		switch (viewType) {
			case ItemViewType.TYPE_ITEM_BOOKMARK:
				return new BookmarkHolder(inflater.inflate(R.layout.item_bookmark_grid, parent, false));
			case ItemViewType.TYPE_ITEM_HEADER:
				return new HeaderHolder(inflater.inflate(R.layout.header_group_button, parent, false));
			default:
				throw new RuntimeException("Invalid ItemViewType");
		}
	}

	@Override
	public void onBindViewHolder(@NotNull DataViewHolder<? extends UniqueObject> holder, int position) {
		if (holder instanceof HeaderHolder) {
			((HeaderHolder) holder).bind((MangaHeader) mDataset.get(position));
		} else if (holder instanceof BookmarkHolder) {
			((BookmarkHolder) holder).bind((MangaBookmark) mDataset.get(position));
		}
	}

	@Override
	public int getItemCount() {
		return mDataset.size();
	}

	@Override
	@ItemViewType
	public int getItemViewType(int position) {
		return mDataset.get(position) instanceof MangaHeader ? ItemViewType.TYPE_ITEM_HEADER : ItemViewType.TYPE_ITEM_BOOKMARK;
	}

	@Override
	public long getItemId(int position) {
		return mDataset.get(position).getId();
	}

	static class HeaderHolder extends DataViewHolder<MangaHeader> implements View.OnClickListener {

		private final TextView mTextView;

		HeaderHolder(View itemView) {
			super(itemView);
			mTextView = itemView.findViewById(R.id.textView);
			Button mButtonMore = itemView.findViewById(R.id.button_more);
			mButtonMore.setOnClickListener(this);
		}

		@Override
		public void bind(MangaHeader mangaHeader) {
			super.bind(mangaHeader);
			mTextView.setText(mangaHeader.name);
		}

		@Override
		public void onClick(View v) {
			final MangaHeader data = getData();
			if (data == null) {
				return;
			}
			final Context context = v.getContext();
			context.startActivity(new Intent(context.getApplicationContext(), PreviewActivity.class)
					.putExtra("manga", data));
		}
	}

	class BookmarkHolder extends DataViewHolder<MangaBookmark> implements View.OnClickListener,
			View.OnLongClickListener, PopupMenu.OnMenuItemClickListener {

		private final ImageView mImageView;
		private final TextView mTextView;
		private final ToolButtonCompat mToolButtonMenu;
		private final PopupMenu mPopupMenu;

		BookmarkHolder(View itemView) {
			super(itemView);
			mTextView = itemView.findViewById(R.id.textView);
			mImageView = itemView.findViewById(R.id.imageView);
			mToolButtonMenu = itemView.findViewById(R.id.toolButton_menu);
			mPopupMenu = new PopupMenu(itemView.getContext(), mToolButtonMenu);
			mPopupMenu.inflate(R.menu.popup_bookmark);
			mPopupMenu.setOnMenuItemClickListener(this);
			itemView.setOnClickListener(this);
			itemView.setOnLongClickListener(this);
			mToolButtonMenu.setOnClickListener(this);
		}

		@Override
		public void bind(MangaBookmark bookmark) {
			super.bind(bookmark);
			ImageUtils.setThumbnail(mImageView, mThumbStore.getFile(bookmark));
			mTextView.setText(ResourceUtils.formatTimeRelative(bookmark.createdAt));
		}

		@Override
		public void recycle() {
			ImageUtils.recycle(mImageView);
			super.recycle();
		}

		@Override
		public void onClick(View view) {
			if (view.getId() == R.id.toolButton_menu) {
				mPopupMenu.show();
			} else {
				final Context context = view.getContext();
				context.startActivity(new Intent(context, ReaderActivity.class)
						.setAction(ReaderActivity.ACTION_BOOKMARK_OPEN)
						.putExtra("bookmark", getData()));
			}
		}

		@Override
		public boolean onMenuItemClick(MenuItem item) {
			final MangaBookmark data = getData();
			if (data == null) {
				return false;
			}
			switch (item.getItemId()) {
				case R.id.action_remove:
					new BookmarkRemoveTask(itemView.getContext()).start(data);
					return true;
				case R.id.action_shortcut:
					IntentUtils.createLauncherShortcutRead(itemView.getContext().getApplicationContext(), data);
					return true;
				default:
					return false;
			}
		}

		@Override
		public boolean onLongClick(View v) {
			onClick(mToolButtonMenu);
			return true;
		}
	}

	@Retention(RetentionPolicy.SOURCE)
	@IntDef({ItemViewType.TYPE_ITEM_BOOKMARK, ItemViewType.TYPE_ITEM_HEADER})
	public @interface ItemViewType {
		int TYPE_ITEM_BOOKMARK = 0;
		int TYPE_ITEM_HEADER = 1;
	}
}
