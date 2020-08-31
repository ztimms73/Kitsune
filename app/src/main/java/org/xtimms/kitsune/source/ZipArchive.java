package org.xtimms.kitsune.source;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import android.util.Pair;

import org.xtimms.kitsune.R;
import org.xtimms.kitsune.core.common.NaturalOrderComparator;
import org.xtimms.kitsune.utils.CollectionsUtils;
import org.xtimms.kitsune.utils.FilesystemUtils;
import org.xtimms.kitsune.utils.MetricsUtils;
import org.xtimms.kitsune.core.MangaStatus;
import org.xtimms.kitsune.core.models.MangaChapter;
import org.xtimms.kitsune.core.models.MangaDetails;
import org.xtimms.kitsune.core.models.MangaHeader;
import org.xtimms.kitsune.core.models.MangaPage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public final class ZipArchive extends MangaProvider {

	public static final String CNAME = "storage/zip";
	public static final String DNAME = "Archive";
	public static final String SCHEME = "manga+zip";

	public ZipArchive(Context context) {
		super(context);
	}

	@NonNull
	@Override
	public ArrayList<MangaHeader> query(@Nullable String search, int page, int sortOrder, int additionalSortOrder, @NonNull String[] genres, @NonNull String[] types) {
		return EMPTY_HEADERS;
	}

	@NonNull
	@Override
	public MangaDetails getDetails(MangaHeader header) {
		final MangaDetails details = new MangaDetails(
				header,
				"",
				header.thumbnail,
				""
		);
		assert header.url != null;
		details.chapters.add(new MangaChapter(header.name, 0, header.url, CNAME, "", 0));
		return details;
	}

	@NonNull
	@Override
	public ArrayList<MangaPage> getPages(String chapterUrl) throws Exception {
		ZipFile zipFile = null;
		final Uri uri = Uri.parse(chapterUrl);
		try {
			final ArrayList<Pair<String,MangaPage>> pagesPairs = new ArrayList<>();
			zipFile = new ZipFile(uri.getPath());
			final Uri zipUri = new Uri.Builder()
					.scheme(SCHEME)
					.encodedPath(uri.getEncodedPath())
					.build();
			final Enumeration<? extends ZipEntry> entries = zipFile.entries();
			ZipEntry e;
			while (entries.hasMoreElements()) {
				e = entries.nextElement();
				if (!e.isDirectory()) {
					pagesPairs.add(new Pair<>(e.getName(), new MangaPage(
							Uri.withAppendedPath(zipUri, e.getName()).toString(),
							CNAME
					)));
				}
			}
			Collections.sort(pagesPairs, new NaturalOrderComparator<Pair<String, MangaPage>>() {
				@Override
				protected String objectToString(Pair<String, MangaPage> obj) {
					return obj.first;
				}
			});
			return CollectionsUtils.mapSeconds(pagesPairs);
		} finally {
			if (zipFile != null) {
				try {
					zipFile.close();
				} catch (IOException ignored) {
				}
			}
		}
	}

	@NonNull
	@Override
	public String getName() {
		return mContext.getString(R.string.file_archive);
	}

	@WorkerThread
	@Nullable
	public static MangaHeader getManga(@NonNull Context context, @NonNull Uri uri) {
		try {
			new ZipFile(uri.getPath()).close(); //check if supported format
			final File thumbRoot = new File(context.getExternalFilesDir("thumb"), "zip");
			if (!thumbRoot.exists() && !thumbRoot.mkdirs()) {
				return null;
			}
			String thumbUri = "";
			final Bitmap page = getFirstPage(context, uri);
			if (page != null) {
				try {
					final File thumbFile = new File(thumbRoot, String.valueOf(uri.toString().hashCode()));
					final MetricsUtils.Size size = MetricsUtils.getPreferredCellSizeMedium(context.getResources());
					final Bitmap thumb = ThumbnailUtils.extractThumbnail(page, size.width, size.height);
					page.recycle();
					FileOutputStream outputStream = new FileOutputStream(thumbFile);
					thumb.compress(Bitmap.CompressFormat.PNG, 9, outputStream);
					outputStream.close();
					thumb.recycle();
					thumbUri = Uri.fromFile(thumbFile).toString();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return new MangaHeader(
					FilesystemUtils.getBasename(Objects.requireNonNull(uri.getLastPathSegment())),
					"",
					"",
					uri.toString(),
					thumbUri,
					CNAME,
					MangaStatus.STATUS_UNKNOWN,
					(short) 0

			);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Nullable
	public static File createThumbnail(@NonNull Context context, @NonNull Uri uri, @NonNull MetricsUtils.Size size) {
		File cacheDir = context.getExternalCacheDir();
		if (cacheDir == null) {
			cacheDir = context.getCacheDir();
		}
		if (cacheDir == null) {
			return null;
		}
		cacheDir = new File(cacheDir, "zipthumbs");
		if (!cacheDir.exists() && !cacheDir.mkdir()) {
			return null;
		}
		final File output = new File(cacheDir, String.valueOf(uri.toString().hashCode()));
		if (output.exists()) {
			return output;
		}
		FileOutputStream outputStream = null;
		try {
			final Bitmap page = getFirstPage(context, uri);
			if (page != null) {
				final Bitmap thumbnail = ThumbnailUtils.extractThumbnail(page, size.width, size.height);
				page.recycle();
				outputStream = new FileOutputStream(output);
				thumbnail.compress(Bitmap.CompressFormat.PNG, 9, outputStream);
				return output;
			}
			return null;
		} catch (Exception e) {
			return null;
		} finally {
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException ignored) {
				}
			}
		}
	}

	public static boolean isFileSupported(@NonNull File file) {
		return isFileSupported(file.getPath());
	}

	public static boolean isFileSupported(@NonNull String filename) {
		final String ext = FilesystemUtils.getExtension(filename);
		return "cbz".equalsIgnoreCase(ext) || "zip".equalsIgnoreCase(ext);
	}

	@Nullable
	private static Bitmap getFirstPage(@NonNull Context context, @NonNull Uri uri) {
		ZipInputStream zipInputStream = null;
		try {
			int tries = 4;
			zipInputStream = new ZipInputStream(context.getContentResolver().openInputStream(uri));
			ZipEntry entry;
			while (tries >= 0 && (entry = zipInputStream.getNextEntry()) != null) {
				if (!entry.isDirectory()) {
					try {
						final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
						int len;
						final byte[] buffer = new byte[512];
						while ((len = zipInputStream.read(buffer)) > 0) {
							bytes.write(buffer, 0, len);
						}
						return BitmapFactory.decodeByteArray(bytes.toByteArray(), 0, bytes.size());
					} catch (Exception e) {
						e.printStackTrace();
						tries--;
					}
				}
			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (zipInputStream != null) {
				try {
					zipInputStream.close();
				} catch (IOException ignored) {
				}
			}
		}
	}

	public static void extractTo(String pageUrl, File destination) throws Throwable {
		InputStream inputStream = null;
		FileOutputStream outputStream = null;
		ZipFile zipFile = null;
		try {
			final Uri uri = Uri.parse(pageUrl);
			final String name = uri.getLastPathSegment();
			String path = uri.getPath();
			assert path != null;
			path = path.substring(0, path.lastIndexOf('/'));
			zipFile = new ZipFile(path);
			final ZipEntry entry = zipFile.getEntry(name);
			inputStream = zipFile.getInputStream(entry);
			outputStream = new FileOutputStream(destination);
			byte[] buffer = new byte[1024];
			int length;
			while ((length = inputStream.read(buffer)) > 0) {
				outputStream.write(buffer, 0, length);
			}
			outputStream.flush();
			//throw new FileNotFoundException(String.format("Entry %s was not found in archive", name));
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException ignored) {
				}
			}
			if (zipFile != null) {
				try {
					zipFile.close();
				} catch (IOException ignored) {
				}
			}
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException ignored) {
				}
			}
		}
	}

	@Override
	public String getPageImage(MangaPage mangaPage) {
		return mangaPage.url;
	}

}
