package org.xtimms.kitsune.core.storage.files;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.xtimms.kitsune.utils.FilesystemUtils;
import org.xtimms.kitsune.core.models.MangaBookmark;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public final class ThumbnailsStorage implements FilesStorage<MangaBookmark,Bitmap> {

	private final File mRootDirectory;

	public ThumbnailsStorage(Context context) {
		mRootDirectory = new File(context.getExternalFilesDir("thumb"), "bookmarks");
		mRootDirectory.mkdirs();
	}

	@NonNull
	@Override
	public File getFile(@NonNull MangaBookmark key) {
		return new File(mRootDirectory, encodeName(key));
	}

	@Nullable
	@Override
	public Bitmap get(@NonNull MangaBookmark key) {
		final File f = getFile(key);
		if (!f.exists()) {
			return null;
		}
		return BitmapFactory.decodeFile(f.getPath());
	}

	@Override
	public void put(@NonNull MangaBookmark key, @Nullable Bitmap bitmap) {
		final File f = getFile(key);
		if (f.exists()) {
			f.delete();
		}
		if (bitmap != null) {
            try (FileOutputStream output = new FileOutputStream(f)) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, output);
                output.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
		}
	}

	@Override
	public boolean remove(@NonNull MangaBookmark key) {
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
	private static String encodeName(MangaBookmark bookmark) {
		return String.valueOf(bookmark.id);
	}
}
