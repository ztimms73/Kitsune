package org.xtimms.kitsune.core.storage;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.core.app.ServiceCompat;

import org.xtimms.kitsune.core.AsyncService;
import org.xtimms.kitsune.BuildConfig;
import org.xtimms.kitsune.R;
import org.xtimms.kitsune.core.common.NotificationHelper;
import org.xtimms.kitsune.utils.ImageUtils;
import org.xtimms.kitsune.core.models.MangaPage;
import org.xtimms.kitsune.core.models.SavedChapter;
import org.xtimms.kitsune.core.models.SavedManga;
import org.xtimms.kitsune.core.models.SavedPage;
import org.xtimms.kitsune.source.MangaProvider;
import org.xtimms.kitsune.core.storage.db.SavedChaptersRepository;
import org.xtimms.kitsune.core.storage.db.SavedMangaRepository;
import org.xtimms.kitsune.core.storage.db.SavedPagesRepository;
import org.xtimms.kitsune.core.storage.files.SavedPagesStorage;
import org.xtimms.kitsune.core.storage.downloaders.Downloader;
import org.xtimms.kitsune.core.storage.downloaders.SimplePageDownloader;

import java.io.File;
import java.util.ArrayList;

@SuppressWarnings("ALL")
public final class SaveService extends AsyncService<SaveRequest> implements Downloader.Callback {

	private static final int RESULT_OK = 0;
	private static final int RESULT_CANCELLED = 1;
	private static final int RESULT_ERROR_UNKNOWN = -1;
	private static final int RESULT_ERROR_WRITE_DIR = -2;
	private static final int RESULT_ERROR_NETWORK = -3;

	public static final String ACTION_MANGA_SAVE_START = "org.xtimms.kitsune.ACTION_MANGA_SAVE_START";
	public static final String ACTION_MANGA_SAVE_CANCEL = "org.xtimms.kitsune.ACTION_MANGA_SAVE_CANCEL";
	public static final String ACTION_MANGA_SAVE_PAUSE = "org.xtimms.kitsune.ACTION_MANGA_SAVE_PAUSE";
	public static final String ACTION_MANGA_SAVE_RESUME = "org.xtimms.kitsune.ACTION_MANGA_SAVE_RESUME";

	private NotificationHelper mNotificationHelper;

	@Override
	public void onCreate() {
		super.onCreate();
		mNotificationHelper = new NotificationHelper(this, 0, "save", R.string.saving_manga);
	}

	@Override
	public boolean onNewIntent(@NonNull String action, @NonNull Bundle extras) {
		switch (action) {
			case ACTION_MANGA_SAVE_START:
				startBackground(SaveRequest.from(extras));
				return true;
			case ACTION_MANGA_SAVE_CANCEL:
				mNotificationHelper.setIndeterminate();
				mNotificationHelper.setText(R.string.cancelling);
				mNotificationHelper.clearActions();
				mNotificationHelper.update();
				cancelBackground();
				return true;
			default:
				return false;
		}
	}

	@Override
	public boolean onStopService() {
		ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE);
		if (!isCancelled()) {
			mNotificationHelper.update();
		}
		return true;
	}

	@Override
	public void onPreExecute(SaveRequest request) {
		mNotificationHelper.nextId();
		mNotificationHelper.setTitle(request.manga.name);
		mNotificationHelper.setText(R.string.saving_manga);
		mNotificationHelper.setIndeterminate();
		mNotificationHelper.setImage(ImageUtils.getCachedImage(request.manga.thumbnail));
		mNotificationHelper.setIcon(android.R.drawable.stat_sys_download);
		mNotificationHelper.addCancelAction(PendingIntent.getService(this, mNotificationHelper.getId() + 1,
				new Intent(this, SaveService.class).setAction(SaveService.ACTION_MANGA_SAVE_CANCEL), 0));
		mNotificationHelper.setOngoing();
		startForeground(mNotificationHelper.getId(), mNotificationHelper.get());
		mNotificationHelper.update();
	}

	@Override
	public int doInBackground(SaveRequest request) {
		try {
			MangaProvider provider = MangaProvider.get(this, request.manga.provider);
			SavedMangaRepository mangaRepository = SavedMangaRepository.get(this);
			SavedChaptersRepository chaptersRepository = SavedChaptersRepository.get(this);
			SavedPagesRepository pagesRepository = SavedPagesRepository.get(this);
			int j = request.chapters.size();
			final String dir = PreferenceManager.getDefaultSharedPreferences(this).getString("mangadir", "");
			File rootDir;
			if (dir.isEmpty()) {
				rootDir = new File(getExternalFilesDir("saved"), String.valueOf(request.manga.id));
			} else {
				rootDir = new File(dir, String.valueOf(request.manga.id));
			}
			if (!rootDir.exists() && (!rootDir.mkdirs() || !rootDir.canWrite())) {
				return RESULT_ERROR_WRITE_DIR;
			}
			SavedManga manga = SavedManga.from(request.manga, rootDir);
			SavedPagesStorage storage = new SavedPagesStorage(manga);
			mangaRepository.addOrUpdate(manga);
			int i = 0;
			while (i < j){
				final SavedChapter chapter = SavedChapter.from(request.chapters.get(i), manga.id);
				chaptersRepository.addOrUpdate(chapter);
				setProgress(0, -1, chapter);
				final ArrayList<MangaPage> pages = provider.getPages(chapter.url);
				final int totalPages = pages.size();
				for (int k = 0; k < totalPages; k++) {
					setProgress(k, totalPages, null);
					final SavedPage page = SavedPage.from(pages.get(k), chapter.id, k);
					SimplePageDownloader downloader = new SimplePageDownloader(page, storage.getFile(page), provider);
					downloader.setCallback(this);
					downloader.run();
					if (isCancelled()) {
						//TODO remove chapter
						return RESULT_CANCELLED;
					}
					if (downloader.isSuccess()) {
						pagesRepository.addOrUpdate(page);
					} else {
						//try again
						Thread.sleep(500L);
						k--;
					}
				}
				i++;
			}
			return RESULT_OK;
		} catch (Exception e) {
			e.printStackTrace();
			return RESULT_ERROR_UNKNOWN;
		}
	}

	@Override
	public void onPostExecute(SaveRequest request, int result) {
		switch (result) {
			case RESULT_OK:
				mNotificationHelper.setIcon(android.R.drawable.stat_sys_download_done);
				mNotificationHelper.setText(getResources().getQuantityString(R.plurals.chapters_saved, request.chapters.size(), request.chapters.size()));
				break;
			case RESULT_CANCELLED:
				mNotificationHelper.dismiss();
				return;
			case RESULT_ERROR_WRITE_DIR:
				mNotificationHelper.setIcon(R.drawable.ic_stat_error);
				mNotificationHelper.setText(R.string.cannot_create_file);
				break;
			case RESULT_ERROR_NETWORK:
				mNotificationHelper.setIcon(R.drawable.ic_stat_error);
				mNotificationHelper.setText(R.string.network_error);
				break;
			case RESULT_ERROR_UNKNOWN:
				mNotificationHelper.setIcon(R.drawable.ic_stat_error);
				mNotificationHelper.setText(R.string.error_occurred);
				break;
			default:
				if (BuildConfig.DEBUG) {
					throw new AssertionError("Unknown result: " + result);
				}
		}
		mNotificationHelper.clearActions();
		mNotificationHelper.setAutoCancel();
		mNotificationHelper.removeProgress();
		mNotificationHelper.update();
	}

	@Override
	public void onProgressUpdate(int progress, int max) {
		if (max == -1) {
			mNotificationHelper.setIndeterminate();
		} else {
			mNotificationHelper.setProgress(progress, max);
		}
		mNotificationHelper.update();
	}

	@Override
	public boolean isPaused() {
		return false;
	}

	public static void start(Context context, SaveRequest request) {
		context.startService(new Intent(context, SaveService.class)
				.setAction(SaveService.ACTION_MANGA_SAVE_START)
				.putExtras(request.toBundle()));
	}

	private static void cancel(Context context) {
		context.startService(new Intent(context, SaveService.class)
				.setAction(SaveService.ACTION_MANGA_SAVE_CANCEL));
	}
}
