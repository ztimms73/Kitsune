package org.xtimms.kitsune.ui.preview.bookmarks;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.xtimms.kitsune.R;
import org.xtimms.kitsune.core.common.DataViewHolder;
import org.xtimms.kitsune.utils.ImageUtils;
import org.xtimms.kitsune.utils.IntentUtils;
import org.xtimms.kitsune.utils.ResourceUtils;
import org.xtimms.kitsune.core.models.MangaBookmark;
import org.xtimms.kitsune.core.storage.files.ThumbnailsStorage;
import org.xtimms.kitsune.ui.discover.bookmarks.BookmarkRemoveTask;
import org.xtimms.kitsune.ui.reader.ReaderActivity;
import org.xtimms.kitsune.ui.reader.ToolButtonCompat;

import java.util.ArrayList;

public final class BookmarksAdapter extends RecyclerView.Adapter<BookmarksAdapter.BookmarkHolder> {

	private final ArrayList<MangaBookmark> mDataset;
	private final ThumbnailsStorage mThumbStore;

	BookmarksAdapter(ArrayList<MangaBookmark> dataset, ThumbnailsStorage thumbnailsStorage) {
		mDataset = dataset;
		mThumbStore = thumbnailsStorage;
		setHasStableIds(true);
	}

	@NonNull
	@Override
	public BookmarkHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return new BookmarkHolder(LayoutInflater.from(parent.getContext())
				.inflate(R.layout.item_bookmark_grid, parent, false));
	}

	@Override
	public void onBindViewHolder(BookmarkHolder holder, int position) {
		holder.bind(mDataset.get(position));
	}

	@Override
	public void onViewRecycled(BookmarkHolder holder) {
		holder.recycle();
		super.onViewRecycled(holder);
	}

	@Override
	public int getItemCount() {
		return mDataset.size();
	}

	@Override
	public long getItemId(int position) {
		return mDataset.get(position).id;
	}

	class BookmarkHolder extends DataViewHolder<MangaBookmark> implements View.OnClickListener, PopupMenu.OnMenuItemClickListener, View.OnLongClickListener {

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
		public void onClick(View view) {
			if (view.getId() == R.id.toolButton_menu) {
				mPopupMenu.show();
			} else {
				final Context context = view.getContext();
				context.startActivity(new Intent(context, ReaderActivity.class)
						.setAction(ReaderActivity.ACTION_BOOKMARK_OPEN)
						.putExtra("bookmark", mDataset.get(getAdapterPosition())));
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
}
