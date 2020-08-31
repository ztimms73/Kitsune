package org.xtimms.kitsune.ui.tools;

import android.content.AsyncTaskLoader;
import android.content.Context;

import org.xtimms.kitsune.utils.FilesystemUtils;
import org.xtimms.kitsune.core.models.SavedManga;
import org.xtimms.kitsune.core.storage.db.SavedMangaRepository;
import org.xtimms.kitsune.core.storage.db.SavedMangaSpecification;
import org.xtimms.kitsune.core.storage.files.SavedPagesStorage;

import java.util.List;

@SuppressWarnings("ALL")
final class StorageStatsLoader extends AsyncTaskLoader<StorageStats> {

	public StorageStatsLoader(Context context) {
		super(context);
	}

	@Override
	public StorageStats loadInBackground() {
		final StorageStats stats = new StorageStats();
		stats.cacheSize = FilesystemUtils.getFileSize(getContext().getExternalCacheDir())
				+ FilesystemUtils.getFileSize(getContext().getCacheDir());
		final SavedMangaRepository savedRepo = SavedMangaRepository.get(getContext());
		final List<SavedManga> manga = savedRepo.query(new SavedMangaSpecification());
		if (manga != null) {
			for (SavedManga o : manga) {
				SavedPagesStorage storage = SavedPagesStorage.get(getContext(), o);
				if (storage != null) {
					stats.savedSize += storage.size();
				}
			}
		}
		return stats;
	}
}
