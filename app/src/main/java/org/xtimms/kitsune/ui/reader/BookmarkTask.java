package org.xtimms.kitsune.ui.reader;

import android.content.Context;
import android.graphics.Bitmap;
import androidx.annotation.NonNull;
import android.widget.Toast;

import org.xtimms.kitsune.R;
import org.xtimms.kitsune.core.common.WeakAsyncTask;
import org.xtimms.kitsune.utils.ImageUtils;
import org.xtimms.kitsune.utils.MetricsUtils;
import org.xtimms.kitsune.core.models.MangaBookmark;
import org.xtimms.kitsune.core.models.MangaChapter;
import org.xtimms.kitsune.core.models.MangaHeader;
import org.xtimms.kitsune.core.models.MangaPage;
import org.xtimms.kitsune.core.storage.db.BookmarksRepository;
import org.xtimms.kitsune.core.storage.files.ThumbnailsStorage;
import org.xtimms.kitsune.ui.reader.loader.PagesCache;

import java.io.File;

public final class BookmarkTask extends WeakAsyncTask<Context,BookmarkTask.Request,Void,Boolean> {

	public BookmarkTask(Context context) {
		super(context);
	}

	@SuppressWarnings("ConstantConditions")
	@Override
	protected Boolean doInBackground(Request... requests) {
		try {
			final MangaBookmark bookmark = new MangaBookmark(
					requests[0].manga,
					requests[0].chapter.id,
					requests[0].page.id,
					System.currentTimeMillis()
			);
			BookmarksRepository repo = BookmarksRepository.get(getObject());
			ThumbnailsStorage thumbs = new ThumbnailsStorage(getObject());
			File file = PagesCache.getInstance(getObject()).getFileForUrl(requests[0].page.url);
			MetricsUtils.Size size = MetricsUtils.getPreferredCellSizeMedium(getObject().getResources());
			Bitmap bitmap = ImageUtils.getThumbnail(file.getPath(), size.width, size.height); //TODO size
			thumbs.put(bookmark, bitmap);
			if (bitmap != null) {
				bitmap.recycle();
			}
			return repo.add(bookmark) || repo.update(bookmark);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	protected void onPostExecute(@NonNull Context context, @NonNull Boolean aBoolean) {
		Toast.makeText(context, aBoolean ? R.string.bookmark_added : R.string.failed_to_create_bookmark, Toast.LENGTH_SHORT).show();
	}

	static class Request {

		final MangaHeader manga;
		final MangaChapter chapter;
		final MangaPage page;

		public Request(MangaHeader manga, MangaChapter chapter, MangaPage page) {
			this.manga = manga;
			this.chapter = chapter;
			this.page = page;
		}
	}
}
