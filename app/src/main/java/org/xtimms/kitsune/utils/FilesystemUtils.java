package org.xtimms.kitsune.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.os.Build;
import android.os.StatFs;
import android.text.TextUtils;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

public abstract class FilesystemUtils {

	public static long getFileSize(@Nullable File file) {
		if (file == null || !file.exists()) {
			return 0;
		}
		if (!file.isDirectory()) {
			return file.length();
		}
		long size = 0;
		final File[] subFiles = file.listFiles();
		assert subFiles != null;
		for (File o : subFiles) {
			if (o.isDirectory()) {
				size += getFileSize(o);
			} else {
				size += o.length();
			}
		}
		return size;
	}

	public static void clearDir(@Nullable File dir) {
		if (dir == null || !dir.exists()) {
			return;
		}
		final File[] files = dir.listFiles();
		assert files != null;
		for (File o : files) {
			if (o.isDirectory()) {
				deleteDir(o);
			} else {
				o.delete();
			}
		}
	}

	public static void deleteDir(@Nullable File dir) {
		if (dir == null || !dir.exists()) {
			return;
		}
		final File[] files = dir.listFiles();
		assert files != null;
		for (File o : files) {
			if (o.isDirectory()) {
				deleteDir(o);
			} else {
				o.delete();
			}
		}
		dir.delete();
	}

	@Nullable
	public static String getExtension(@NonNull String path) {
		final int p = path.lastIndexOf('.');
		if (path.length() - p > 6) {
			return null;
		} else {
			return path.substring(p + 1).toLowerCase();
		}
	}

	public static File getFilesDir(Context context, File root, String type) {
		final String appcat = TextUtils.join(File.separator, new String[]{"Android","data", context.getPackageName(), "files", type});
		File file = new File(root, appcat);
		file.mkdirs();
		return file;
	}

	@NonNull
	public static String getBasename(@NonNull String path) {
		final int begin = path.lastIndexOf('/') + 1;
		final int end = path.lastIndexOf('.');
		return path.length() - end > 6 ? path.substring(begin) : path.substring(begin, end);
	}

	private static final int SIZE_MB = 1024 * 1024;

	@SuppressLint("DefaultLocale")
	public static String formatSizeMb(int sizeMb) {
		if (sizeMb < 1024) {
			return sizeMb + " MB";
		} else {
			return String.format("%.1f GB", sizeMb / 1024.f);
		}
	}

	public static int getFreeSpaceMb(String path) {
		StatFs stat = new StatFs(path);
		long aval;
		aval = stat.getAvailableBytes();
		aval /= SIZE_MB;
		return (int) aval;
	}

	public static List<File> getAvailableStorages(Context context) {
		final String appcat = TextUtils.join(File.separator, new String[] {"Android", "data", context.getPackageName()});
		File storageRoot = new File("/storage");
		File[] storages = storageRoot.listFiles(file -> {
			File appRoot = new File(file, appcat);
			return appRoot.exists() && appRoot.canWrite();
		});
		assert storages != null;
		return Arrays.asList(storages);
	}

	public static void copyFile(File src, File dst) throws IOException {
		try (InputStream in = new FileInputStream(src); OutputStream out = new FileOutputStream(dst)) {
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
		}
	}

	public static boolean saveBitmap(Bitmap bitmap, String filename) {
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(filename);
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException io) {
				io.printStackTrace();
			}
		}
	}

	public static void scanFile(@NonNull Context context, @NonNull File file,
								@Nullable MediaScannerConnection.OnScanCompletedListener callback) {
		MediaScannerConnection.scanFile(context, new String[]{file.getAbsolutePath()}, null, callback == null ?
				(MediaScannerConnection.OnScanCompletedListener) (path, uri) -> {

				} : callback);
	}

	public static void scanMediaFile(Context context, File file) {
		if (!file.exists()) return;
		MediaScannerConnection.scanFile(context,
				new String[]{file.getPath()}, null,
				(path, uri) -> {
					//....
				});
	}
}
