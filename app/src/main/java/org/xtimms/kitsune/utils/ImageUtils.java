package org.xtimms.kitsune.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import android.widget.ImageView;

import com.lucasurbas.listitemview.ListItemView;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

import org.xtimms.kitsune.R;
import org.xtimms.kitsune.core.common.ThumbSize;
import org.xtimms.kitsune.core.common.TransitionDisplayer;

import java.io.File;

public abstract class ImageUtils {

	private static DisplayImageOptions mOptionsThumb = null;
	private static DisplayImageOptions mOptionsUpdate = null;

	public static void init(Context context) {
		if (!ImageLoader.getInstance().isInited()) {
			int cacheMb = 100;
			ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
					.defaultDisplayImageOptions(getImageLoaderOptionsBuilder().build())
					.diskCacheSize(cacheMb * 1024 * 1024) //100 Mb
					.diskCacheFileCount(200)
					.memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024)) // 2 Mb
					.build();

			ImageLoader.getInstance().init(config);
		}
		if (mOptionsThumb == null) {
			Drawable holder = ContextCompat.getDrawable(context, R.drawable.image_album);
			mOptionsThumb = getImageLoaderOptionsBuilder()
					.showImageOnFail(R.drawable.alert_circle_outline)
					.showImageForEmptyUri(R.drawable.image_broken_variant)
					.showImageOnLoading(holder)
					.build();
		}

		if (mOptionsUpdate == null) {
			mOptionsUpdate = getImageLoaderOptionsBuilder()
					.displayer(new TransitionDisplayer())
					.build();
		}
	}

	private static DisplayImageOptions.Builder getImageLoaderOptionsBuilder() {
		return new DisplayImageOptions.Builder()
				.cacheInMemory(true)
				.cacheOnDisk(true)
				.resetViewBeforeLoading(false)
				.displayer(new FadeInBitmapDisplayer(200, true, true, false));
	}

	@Nullable
	public static Bitmap getCachedImage(String url) {
		try {
			Bitmap b = ImageLoader.getInstance().getMemoryCache().get(url);
			if (b == null) {
				File f = ImageLoader.getInstance().getDiskCache().get(url);
				if (f != null) {
					b = BitmapFactory.decodeFile(f.getPath());
				}
			}
			return b;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private static String fixUrl(String url) {
		return (!android.text.TextUtils.isEmpty(url) && url.charAt(0) == '/') ? "file://" + url : url;
	}

	public static void setThumbnailWithSize(@NonNull ImageView imageView, String url, @Nullable ThumbSize size) {
		ImageLoader.getInstance().displayImage(
				fixUrl(url),
				new ImageViewAware(imageView),
				mOptionsThumb,
				size != null && imageView.getMeasuredWidth() == 0 ? size.toImageSize() : null,
				null,
				null
		);
	}

	public static void setThumbnail(@NonNull ImageView imageView, String url, String referer) {
		if (url != null && url.equals(imageView.getTag())) {
			return;
		}
		imageView.setTag(url);
		ImageLoader.getInstance().displayImage(
				fixUrl(url),
				new ImageViewAware(imageView),
				mOptionsThumb
		);
	}

	public static void setThumbnail(@NonNull ImageView imageView, @Nullable File file) {
		final String url = file == null ? null : "file://" + file.getPath();
		setThumbnail(imageView, url, null);
	}

	public static void setThumbnailCropped(@NonNull ImageView imageView, @Nullable String url, @NonNull MetricsUtils.Size size, String referer) {
		if (url != null && url.equals(imageView.getTag())) {
			return;
		}
		imageView.setTag(url);
		ImageLoader.getInstance().displayImage(
				url,
				new ImageViewAware(imageView),
				mOptionsThumb = getImageLoaderOptionsBuilder()
						.extraForDownloader(referer)
						.build(),
				new ImageSize(size.width, size.height),
				null, null
		);
	}

	public static void setThumbnailCropped(@NonNull ImageView imageView, @Nullable File file, @NonNull MetricsUtils.Size size) {
		final String url = file != null && file.exists() ? "file://" + file.getPath() : null;
		setThumbnailCropped(imageView, url, size, null);
	}

	public static void setEmptyThumbnail(ImageView imageView) {
		ImageLoader.getInstance().cancelDisplayTask(imageView);
		imageView.setImageResource(R.drawable.image_album);
		imageView.setTag(null);
	}

	public static void recycle(@NonNull ImageView imageView) {
		ImageLoader.getInstance().cancelDisplayTask(imageView);
		final Drawable drawable = imageView.getDrawable();
		if (drawable instanceof BitmapDrawable) {
			//((BitmapDrawable) drawable).getBitmap().recycle();
			imageView.setImageDrawable(null);
		}
		imageView.setTag(null);
	}

	public static void recycle(@NonNull ListItemView listItemView) {
		ImageLoader.getInstance().cancelDisplayTask(listItemView.getAvatarView());
		final Drawable drawable = listItemView.getForeground();
		if (drawable instanceof BitmapDrawable) {
			//((BitmapDrawable) drawable).getBitmap().recycle();
			listItemView.setIconDrawable(null);
		}
		listItemView.setTag(null);
	}

	public static void updateImage(@NonNull ImageView imageView, String url, String referer) {
		ImageLoader.getInstance().displayImage(
				fixUrl(url),
				imageView,
				mOptionsUpdate
		);
	}

	@Nullable
	public static Bitmap getThumbnail(String path, int width, int height) {
		Bitmap bitmap = getCachedImage(path);
		if (bitmap == null && path.startsWith("/")) {
			bitmap = BitmapFactory.decodeFile(path);
		}
		if (bitmap != null) {
			bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		}
		return bitmap;
	}
}

