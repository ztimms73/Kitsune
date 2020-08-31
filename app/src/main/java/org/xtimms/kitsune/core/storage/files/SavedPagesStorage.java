package org.xtimms.kitsune.core.storage.files;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.xtimms.kitsune.utils.FilesystemUtils;
import org.xtimms.kitsune.core.models.MangaHeader;
import org.xtimms.kitsune.core.models.SavedManga;
import org.xtimms.kitsune.core.models.SavedPage;
import org.xtimms.kitsune.core.storage.db.SavedMangaRepository;

import java.io.File;

public final class SavedPagesStorage implements FilesStorage<SavedPage, File> {

	private final File mRootDirectory;

	public SavedPagesStorage(@NonNull SavedManga manga) {
		mRootDirectory = new File(manga.localPath);
		//noinspection ResultOfMethodCallIgnored
		mRootDirectory.mkdirs();
	}

	@NonNull
	@Override
	public File getFile(@NonNull SavedPage key) {
		return new File(mRootDirectory, encodeName(key));
	}

	@Nullable
	@Override
	public File get(@NonNull SavedPage key) {
		final File file = getFile(key);
		return file.exists() ? file : null;
	}

	@Override
	public void put(@NonNull SavedPage key, @Nullable File file) {
		final File f = getFile(key);
		if (f.exists()) {
			f.delete();
		}
        //TODO not implemented
    }

	@Override
	public boolean remove(@NonNull SavedPage key) {
		File file = getFile(key);
		return file.exists() && file.delete();
	}

	@Override
	public void clear() {
		FilesystemUtils.clearDir(mRootDirectory);
	}

	@Override
	public long size() {
		return FilesystemUtils.getFileSize(mRootDirectory);
	}

	@NonNull
	private static String encodeName(SavedPage page) {
		return page.chapterId + "_" + page.id;
	}

	@Nullable
	public static SavedPagesStorage get(@NonNull Context context, @NonNull MangaHeader manga) {
		final SavedManga savedManga = SavedMangaRepository.get(context).find(manga);
		return savedManga == null ? null : new SavedPagesStorage(savedManga);
	}
}
