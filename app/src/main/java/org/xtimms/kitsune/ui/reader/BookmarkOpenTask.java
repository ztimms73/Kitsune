package org.xtimms.kitsune.ui.reader;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import org.xtimms.kitsune.R;
import org.xtimms.kitsune.core.common.WeakAsyncTask;
import org.xtimms.kitsune.utils.CollectionsUtils;
import org.xtimms.kitsune.utils.ErrorUtils;
import org.xtimms.kitsune.core.ObjectWrapper;
import org.xtimms.kitsune.core.models.MangaBookmark;
import org.xtimms.kitsune.source.MangaProvider;

final class BookmarkOpenTask extends WeakAsyncTask<ReaderActivity, MangaBookmark, Void, ObjectWrapper<ReaderActivity.Result>> {

	BookmarkOpenTask(ReaderActivity readerActivity) {
		super(readerActivity);
	}

	@NonNull
	@Override
	protected ObjectWrapper<ReaderActivity.Result> doInBackground(MangaBookmark... bookmarks) {
		try {
			final ReaderActivity.Result result = new ReaderActivity.Result();
			final MangaBookmark bookmark = bookmarks[0];
			MangaProvider provider = MangaProvider.get(getObject(), bookmark.manga.provider);
			result.mangaDetails = provider.getDetails(bookmark.manga);
			result.chapter = CollectionsUtils.findItemById(result.mangaDetails.chapters, bookmark.chapterId);
			result.pageId = bookmark.pageId;
			return new ObjectWrapper<>(result);
		} catch (Exception e) {
			e.printStackTrace();
			return new ObjectWrapper<>(e);
		}
	}

	@Override
	protected void onPostExecute(@NonNull ReaderActivity readerActivity, ObjectWrapper<ReaderActivity.Result> result) {
		super.onPostExecute(readerActivity, result);
		if (result.isSuccess()) {
			final ReaderActivity.Result data = result.get();
			readerActivity.onMangaReady(data);
		} else {
			new AlertDialog.Builder(readerActivity)
					.setMessage(ErrorUtils.getErrorMessageDetailed(readerActivity, result.getError()))
					.setCancelable(true)
					.setOnCancelListener(readerActivity)
					.setNegativeButton(R.string.close, readerActivity)
					.create()
					.show();
		}
	}
}