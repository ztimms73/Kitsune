package org.xtimms.kitsune.core.common;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.assist.LoadedFrom;
import com.nostra13.universalimageloader.core.display.BitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;

public final class TransitionDisplayer implements BitmapDisplayer {

	@Override
	public void display(Bitmap bitmap, ImageAware imageAware, LoadedFrom loadedFrom) {
		Drawable last = getDrawableFromView(imageAware.getWrappedView());
		if (last == null) {
			imageAware.setImageBitmap(bitmap);
			return;
		}
		TransitionDrawable td = new TransitionDrawable(new Drawable[]{
				last,
				new BitmapDrawable(imageAware.getWrappedView().getResources(), bitmap)
		});
		td.setCrossFadeEnabled(false);
		imageAware.setImageDrawable(td);
		td.startTransition(1000);
	}

	@Nullable
	private static Drawable getDrawableFromView(@Nullable View v) {
		if (v == null) {
			return null;
		} else if (v instanceof ImageView) {
			return ((ImageView) v).getDrawable();
		} else {
			return null;
		}
	}
}
