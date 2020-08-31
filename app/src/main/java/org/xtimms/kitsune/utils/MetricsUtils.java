package org.xtimms.kitsune.utils;

import android.content.res.Resources;
import androidx.annotation.DimenRes;
import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import android.view.View;
import android.view.ViewGroup;

import org.xtimms.kitsune.R;

public abstract class MetricsUtils {

	private static final float DEF_ASPECT_RATIO = 18f / 13f;

	public static Size getPreferredCellSizeMedium(Resources resources) {
		return getPreferredCellSize(resources);
	}

	private static Size getPreferredCellSize(Resources resources) {
		int columns = getPreferredColumnsCount(resources, R.dimen.column_width_medium);
		int width = (int) Math.floor(resources.getDisplayMetrics().widthPixels / (float) columns);
		return new Size(width, (int) (width * DEF_ASPECT_RATIO));
	}

	public static int getPreferredColumnsCountMedium(Resources resources) {
		return getPreferredColumnsCount(resources, R.dimen.column_width_medium);
	}

	private static int getPreferredColumnsCount(Resources resources, @DimenRes int columnWidthDimen) {
		int totalWidth = resources.getDisplayMetrics().widthPixels;
		int columnWidth = resources.getDimensionPixelSize(columnWidthDimen);
		return Math.max(1, Math.round(totalWidth / (float) columnWidth));
	}

	public static class Size {

		public final int width;
		public final int height;

		private Size(int width, int height) {
			this.width = width;
			this.height = height;
		}

		@NonNull
		@UiThread
		public static Size fromLayoutParams(View view) {
			ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
			return new Size(layoutParams.width, layoutParams.height);
		}
	}
}
