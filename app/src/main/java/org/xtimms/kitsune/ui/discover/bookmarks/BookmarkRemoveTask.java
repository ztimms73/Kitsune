package org.xtimms.kitsune.ui.discover.bookmarks;

import android.content.Context;
import androidx.annotation.NonNull;
import android.widget.Toast;

import org.xtimms.kitsune.R;
import org.xtimms.kitsune.core.common.WeakAsyncTask;
import org.xtimms.kitsune.core.models.MangaBookmark;
import org.xtimms.kitsune.core.storage.db.BookmarksRepository;
import org.xtimms.kitsune.core.storage.files.ThumbnailsStorage;

import java.util.Objects;

public class BookmarkRemoveTask extends WeakAsyncTask<Context,MangaBookmark,Void,MangaBookmark> {

	public BookmarkRemoveTask(Context context) {
		super(context);
	}

	@Override
	protected MangaBookmark doInBackground(MangaBookmark... bookmarks) {
		try {
			if (BookmarksRepository.get(getObject()).remove(bookmarks[0])) {
				new ThumbnailsStorage(Objects.requireNonNull(getObject())).remove(bookmarks[0]);
				return bookmarks[0];
			} else {
				return null;
			}
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	protected void onPostExecute(@NonNull Context context, MangaBookmark bookmark) {
		super.onPostExecute(context, bookmark);
		if (bookmark == null) {
			Toast.makeText(context, R.string.error_occurred, Toast.LENGTH_SHORT).show();
		} else if (context instanceof OnBookmarkRemovedListener) {
			((OnBookmarkRemovedListener) context).onBookmarkRemoved(bookmark);
		}
	}

	public interface OnBookmarkRemovedListener {

		void onBookmarkRemoved(@NonNull MangaBookmark bookmark);
	}
}
