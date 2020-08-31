package org.xtimms.kitsune.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import org.xtimms.kitsune.R;
import org.xtimms.kitsune.core.common.ThumbSize;

import timber.log.Timber;

import static org.xtimms.kitsune.utils.ThemeUtils.isAppThemeDark;

public abstract class LayoutUtils {

	public static boolean isTablet(Context context) {
		return context.getResources().getConfiguration().isLayoutSizeAtLeast(Configuration.SCREENLAYOUT_SIZE_LARGE);
	}

	public static boolean isLandscape(Context context) {
		return context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
	}

	public static boolean isTabletLandscape(Context context) {
		return isTablet(context) && isLandscape(context);
	}

	public static int getOptimalColumnsCount(Resources resources, ThumbSize thumbSize) {
		float width = resources.getDisplayMetrics().widthPixels;
		int count = Math.round(width / (thumbSize.getWidth() + DpToPx(resources, 8)));
		return count == 0 ? 1 : count;
	}

	public static Drawable[] getThemedIcons(Context context, int... ids) {
		boolean dark = isAppThemeDark(context);
		PorterDuffColorFilter cf = dark ?
				new PorterDuffColorFilter(ContextCompat.getColor(context, R.color.white_overlay_85), PorterDuff.Mode.SRC_ATOP)
				: null;
		Drawable[] ds = new Drawable[ids.length];
		for (int i=0;i<ids.length;i++) {
			ds[i] = ContextCompat.getDrawable(context, ids[i]);
			if (ds[i] != null && dark) {
				ds[i].setColorFilter(cf);
			}
		}
		return ds;
	}

	public static int getItemCount(RecyclerView recyclerView) {
		RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
		return layoutManager == null ? 0 : layoutManager.getItemCount();
	}

	public static int findLastVisibleItemPosition(RecyclerView recyclerView) {
		RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
		assert layoutManager != null;
		final View child = findOneVisibleChild(layoutManager, layoutManager.getChildCount() - 1, -1, false, true);
		return child == null ? RecyclerView.NO_POSITION : recyclerView.getChildAdapterPosition(child);
	}

	public static int findFirstVisibleItemPosition(RecyclerView recyclerView) {
		RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
		assert layoutManager != null;
		final View child = findOneVisibleChild(layoutManager, 0, layoutManager.getChildCount(), false, true);
		return child == null ? RecyclerView.NO_POSITION : recyclerView.getChildAdapterPosition(child);
	}

	private static int findFirstCompletelyVisibleItemPosition(RecyclerView recyclerView) {
		RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
		assert layoutManager != null;
		final View child = findOneVisibleChild(layoutManager, 0, layoutManager.getChildCount(), true, false);
		return child == null ? RecyclerView.NO_POSITION : recyclerView.getChildAdapterPosition(child);
	}

	private static View findOneVisibleChild(RecyclerView.LayoutManager layoutManager, int fromIndex, int toIndex,
											boolean completelyVisible, boolean acceptPartiallyVisible) {
		OrientationHelper helper;
		if (layoutManager.canScrollVertically()) {
			helper = OrientationHelper.createVerticalHelper(layoutManager);
		} else {
			helper = OrientationHelper.createHorizontalHelper(layoutManager);
		}

		final int start = helper.getStartAfterPadding();
		final int end = helper.getEndAfterPadding();
		final int next = toIndex > fromIndex ? 1 : -1;
		View partiallyVisible = null;
		for (int i = fromIndex; i != toIndex; i += next) {
			final View child = layoutManager.getChildAt(i);
			final int childStart = helper.getDecoratedStart(child);
			final int childEnd = helper.getDecoratedEnd(child);
			if (childStart < end && childEnd > start) {
				if (completelyVisible) {
					if (childStart >= start && childEnd <= end) {
						return child;
					} else if (acceptPartiallyVisible && partiallyVisible == null) {
						partiallyVisible = child;
					}
				} else {
					return child;
				}
			}
		}
		return partiallyVisible;
	}

	public static void setTextOrHide(TextView textView, String text) {
		if (android.text.TextUtils.isEmpty(text)) {
			textView.setVisibility(View.GONE);
		} else {
			textView.setText(text);
			textView.setVisibility(View.VISIBLE);
		}
	}

	public static void setSelectionFromTop(RecyclerView recyclerView, int position) {
		Timber.tag("Scroll").d("#%s", position);
		final RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
		if (layoutManager != null) {
			if (layoutManager instanceof LinearLayoutManager) {
				((LinearLayoutManager) layoutManager).scrollToPositionWithOffset(position, 0);
			} else {
				layoutManager.scrollToPosition(position);
			}
		}
	}

	public static void scrollToCenter(RecyclerView recyclerView, int position) {
		final int firstPos = findFirstVisibleItemPosition(recyclerView);
		final int lastPos = findLastVisibleItemPosition(recyclerView);
		final int count = getItemCount(recyclerView);
		if (position < firstPos) {
			setSelectionFromTop(recyclerView, calculateScrollPos(firstPos, position, count));
		} else if (position > lastPos) {
			setSelectionFromTop(recyclerView, calculateScrollPos(position, position - lastPos + firstPos, count));
		}
	}

	private static int calculateScrollPos(int a, int b, int max) {
		return Math.min((a + b) / 2, max);
	}

	public static void forceUpdate(@NonNull RecyclerView recyclerView) {
		int pos = findFirstCompletelyVisibleItemPosition(recyclerView);
		final RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
		final RecyclerView.Adapter adapter = recyclerView.getAdapter();
		recyclerView.setAdapter(null);
		recyclerView.setLayoutManager(null);
		recyclerView.setAdapter(adapter);
		recyclerView.setLayoutManager(layoutManager);
		assert adapter != null;
		adapter.notifyDataSetChanged();
		setSelectionFromTop(recyclerView, pos);
	}

	public static void showSoftKeyboard(@NonNull View view) {
		view.requestFocus();
		InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		assert imm != null;
		imm.showSoftInput(view, 0);
	}

	public static void hideSoftKeyboard(@NonNull View view) {
		InputMethodManager imm = (InputMethodManager)view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		assert imm != null;
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}

	public static int DpToPx(Resources res, float dp) {
		float density = res.getDisplayMetrics().density;
		return (int) (dp * density);
	}
}
