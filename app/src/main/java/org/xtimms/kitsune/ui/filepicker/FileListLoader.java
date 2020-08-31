package org.xtimms.kitsune.ui.filepicker;

import android.content.AsyncTaskLoader;
import android.content.Context;
import androidx.annotation.NonNull;

import org.xtimms.kitsune.core.ListWrapper;
import org.xtimms.kitsune.core.models.FileDesc;
import org.xtimms.kitsune.source.ZipArchive;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

@SuppressWarnings("ALL")
final class FileListLoader extends AsyncTaskLoader<ListWrapper<FileDesc>> {

	private final String mPath;
	private final boolean mSupportedOnly;

	FileListLoader(Context context, String path, boolean supportedOnly) {
		super(context);
		mPath = path;
		mSupportedOnly = supportedOnly;
	}

	@NonNull
	@Override
	public ListWrapper<FileDesc> loadInBackground() {
		try {
			File[] files = new File(mPath).listFiles();
			if (files == null) {
				return ListWrapper.badList();
			}
			final ArrayList<FileDesc> result = new ArrayList<>();
			for (File o : files) {
				if (o.isDirectory()) {
					final String[] sub = o.list();
					result.add(new FileDesc(
							o,
							sub == null ? 0 : sub.length
					));
				} else if (!mSupportedOnly || ZipArchive.isFileSupported(o)) {
					result.add(new FileDesc(
							o,
							0
					));
				}
			}
			Collections.sort(result, new FileNameComparator());
			Collections.sort(result, new FileTypeComparator());
			return new ListWrapper<>(result);
		} catch (Exception e) {
			return new ListWrapper<>(e);
		}
	}
	private static class FileTypeComparator implements Comparator<FileDesc> {

		@Override
		public int compare(FileDesc file1, FileDesc file2) {

			if (file1.file.isDirectory() && file2.file.isFile())
				return -1;
			if (file1.file.isDirectory() && file2.file.isDirectory()) {
				return 0;
			}
			if (file1.file.isFile() && file2.file.isFile()) {
				return 0;
			}
			return 1;
		}
	}

	private static class FileNameComparator implements Comparator<FileDesc> {

		@Override
		public int compare(FileDesc file1, FileDesc file2) {

			return String.CASE_INSENSITIVE_ORDER.compare(file1.file.getName(),
					file2.file.getName());
		}
	}
}
