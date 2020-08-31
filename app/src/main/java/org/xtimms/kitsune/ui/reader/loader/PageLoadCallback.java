package org.xtimms.kitsune.ui.reader.loader;

public interface PageLoadCallback {

	void onPageDownloaded();

	void onPageDownloadFailed(Throwable reason);

	void onPageDownloadProgress(int progress, int max);

}
