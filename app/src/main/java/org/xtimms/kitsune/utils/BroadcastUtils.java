package org.xtimms.kitsune.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.xtimms.kitsune.core.models.MangaChapter;

import java.util.Objects;

public abstract class BroadcastUtils {

	public static final String ACTION_DOWNLOAD_DONE = "org.xtimms.kitsune.ACTION_DOWNLOAD_DONE";

	public static BroadcastReceiver createDownloadsReceiver(DownloadsReceiverCallback callback) {
		return new DownloadsReceiver(callback);
	}

	private static class DownloadsReceiver extends BroadcastReceiver {

		private final DownloadsReceiverCallback mCallback;

		private DownloadsReceiver(DownloadsReceiverCallback callback) {
			mCallback = callback;
		}

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent != null && ACTION_DOWNLOAD_DONE.equals(intent.getAction())) try {
				mCallback.onChapterDownloaded(MangaChapter.from(Objects.requireNonNull(intent.getExtras())));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public interface DownloadsReceiverCallback {

		void onChapterDownloaded(MangaChapter chapter);
	}
}
