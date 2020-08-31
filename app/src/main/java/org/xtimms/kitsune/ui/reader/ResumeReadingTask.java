package org.xtimms.kitsune.ui.reader;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import org.xtimms.kitsune.R;
import org.xtimms.kitsune.core.common.WeakAsyncTask;
import org.xtimms.kitsune.utils.CollectionsUtils;
import org.xtimms.kitsune.utils.ErrorUtils;
import org.xtimms.kitsune.core.ObjectWrapper;
import org.xtimms.kitsune.core.models.MangaDetails;
import org.xtimms.kitsune.core.models.MangaHeader;
import org.xtimms.kitsune.core.models.MangaHistory;
import org.xtimms.kitsune.source.MangaProvider;
import org.xtimms.kitsune.core.storage.db.HistoryRepository;

final class ResumeReadingTask extends WeakAsyncTask<ReaderActivity,MangaHeader,Void,ObjectWrapper<ReaderActivity.Result>> {

	ResumeReadingTask(ReaderActivity readerActivity) {
		super(readerActivity);
	}

	@Override
	protected ObjectWrapper<ReaderActivity.Result> doInBackground(MangaHeader... mangaHeaders) {
		try {
			final ReaderActivity.Result result = new ReaderActivity.Result();
			final MangaHeader manga = mangaHeaders[0];
			final MangaHistory history = manga instanceof MangaHistory ? (MangaHistory) manga :
					HistoryRepository.get(getObject()).find(manga);
			MangaProvider provider = MangaProvider.get(getObject(), manga.provider);
			result.mangaDetails = manga instanceof MangaDetails ? (MangaDetails) manga : provider.getDetails(history);
			result.chapter = history == null ? result.mangaDetails.chapters.get(0) :
					CollectionsUtils.findItemById(result.mangaDetails.chapters, history.chapterId);
			result.pageId = history != null ? history.pageId : 0;
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