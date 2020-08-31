package org.xtimms.kitsune.ui.reader.pager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.View;
import android.view.ViewStub;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import org.xtimms.kitsune.R;
import org.xtimms.kitsune.utils.ErrorUtils;
import org.xtimms.kitsune.utils.KaomojiUtils;
import org.xtimms.kitsune.utils.SsivUtils;
import org.xtimms.kitsune.core.common.views.TextProgressView;
import org.xtimms.kitsune.core.models.MangaPage;
import org.xtimms.kitsune.ui.reader.decoder.ImageConverter;
import org.xtimms.kitsune.ui.reader.loader.PageDownloader;
import org.xtimms.kitsune.ui.reader.loader.PageLoadCallback;
import org.xtimms.kitsune.ui.reader.loader.PagesCache;

import java.io.File;

public final class PageView extends FrameLayout implements View.OnClickListener,
		ImageConverter.ConvertCallback, PageLoadCallback {

	public static int scaleMode;

	private MangaPage mPage;
	private File mFile;

	private final SubsamplingScaleImageView mSubsamplingScaleImageView;
	private final TextProgressView mTextProgressView;
	@Nullable
	private ViewStub mStubError;
	@Nullable
	private View mErrorView = null;

	public PageView(@NonNull Context context) {
		this(context, null, 0);
	}

	public PageView(@NonNull Context context, @Nullable AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public PageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		View.inflate(context, R.layout.view_page, this);
		mSubsamplingScaleImageView = findViewById(R.id.subsamplingImageView);
		mTextProgressView = findViewById(R.id.progressView);
		mStubError = findViewById(R.id.stubError);
		mSubsamplingScaleImageView.setDoubleTapZoomStyle(SubsamplingScaleImageView.ZOOM_FOCUS_FIXED);
		mSubsamplingScaleImageView.setPanLimit(SubsamplingScaleImageView.PAN_LIMIT_INSIDE);
		mSubsamplingScaleImageView.setMinimumDpi(90);
		mSubsamplingScaleImageView.setMaxScale(2.0F);
		mSubsamplingScaleImageView.resetScaleAndCenter();
		mSubsamplingScaleImageView.setMinimumTileDpi(180);
		mSubsamplingScaleImageView.setOnImageEventListener(new SubsamplingScaleImageView.DefaultOnImageEventListener() {

			@Override
			public void onReady() {
				onLoadingComplete();
			}

			@Override
			public void onImageLoadError(Exception e) {
				onImageDisplayFailed(e);
			}
		});
	}

	@SuppressLint("ClickableViewAccessibility")
	public void setTapDetector(final GestureDetector detector) {
		mSubsamplingScaleImageView.setOnTouchListener((v, event) -> detector.onTouchEvent(event));
	}

	public void setData(MangaPage page) {
		mTextProgressView.setProgress(TextProgressView.INDETERMINATE);
		mTextProgressView.setVisibility(VISIBLE);
		setError(null);
		mPage = page;
		if (page.url.startsWith("file://")) {
			mSubsamplingScaleImageView.setImage(ImageSource.uri(page.url));
		} else {
			mFile = PagesCache.getInstance(getContext()).getFileForUrl(page.url);
			if (mFile.exists()) {
				mSubsamplingScaleImageView.setImage(ImageSource.uri(Uri.fromFile(mFile)));
			} else {
				PageDownloader.getInstance().downloadPage(getContext(), page, mFile, this);
			}
		}
	}

	private void setError(@Nullable CharSequence errorMessage) {
		if (errorMessage == null) {
			if (mErrorView != null) {
				mErrorView.setVisibility(GONE);
			}
			return;
		}
		mTextProgressView.setVisibility(GONE);
		if (mErrorView == null) {
			assert mStubError != null;
			mErrorView = mStubError.inflate();
			mErrorView.findViewById(R.id.button_retry).setOnClickListener(this);
			mStubError = null;
		}
		mErrorView.setVisibility(VISIBLE);
		((TextView)mErrorView.findViewById(R.id.textView_error)).setText(errorMessage);
		((TextView)mErrorView.findViewById(R.id.text_face)).setText(KaomojiUtils.getRandomErrorFace());
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.button_retry) {
			setError(null);
			mTextProgressView.setVisibility(VISIBLE);
			try {
				mFile.delete();
			} catch (Exception e) {
				setError(e.getMessage());
			}
			PageDownloader.getInstance().downloadPage(getContext(), mPage, mFile, this);
		}
	}

	/**
	 * Loading done, alles ok
	 */
	public void onLoadingComplete() {
		setError(null);
		mTextProgressView.setVisibility(GONE);
		int i = scaleMode;
		if (i != 5) {
			switch (i) {
				default:
					return;
				case 3:
					SsivUtils.setScaleZoomSrc(mSubsamplingScaleImageView);
					return;
				case 2:
					SsivUtils.setScaleHeightLeft(mSubsamplingScaleImageView);
					return;
				case 1:
					SsivUtils.setScaleWidthTop(mSubsamplingScaleImageView);
					return;
				case 0:
					break;
			}
			SsivUtils.setScaleFit(mSubsamplingScaleImageView);
			return;
		}
		SsivUtils.setScaleHeightRight(mSubsamplingScaleImageView);
	}

	public void onImageDisplayFailed(Exception e) {
		try {
			ImageConverter.getInstance().convertAsync(mFile.getPath(), this);
		} catch (Exception o) {
			setError(ErrorUtils.getErrorMessageDetailed(getContext(), e));
		}
	}

	@Override
	public void onPageDownloaded() {
		mSubsamplingScaleImageView.setImage(ImageSource.uri(Uri.fromFile(mFile)));
	}

	@Override
	public void onPageDownloadFailed(Throwable reason) {
		setError(ErrorUtils.getErrorMessageDetailed(getContext(), reason));
	}

	@Override
	public void onPageDownloadProgress(int progress, int max) {
		mTextProgressView.setProgress(progress, max);
	}

	public void recycle() {
		if (mPage != null) {
			PageDownloader.getInstance().cancel(mPage);
		}
		setError(null);
		mSubsamplingScaleImageView.recycle();
	}

	public MangaPage getData() {
		return mPage;
	}

	@Override
	public void onConvertDone(boolean success) {
		if (success) {
			mSubsamplingScaleImageView.setImage(ImageSource.uri(Uri.fromFile(mFile)));
		} else {
			setError(getContext().getString(R.string.image_decode_error));
		}
	}
}
