package org.xtimms.kitsune.ui.reader;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

public final class FitWindowsFrameLayout extends FrameLayout {

	private boolean mSwipeIntercepted = false;

	@Nullable
	private Callback mCallback = null;

	public FitWindowsFrameLayout(@NonNull Context context) {
		this(context, null, 0);
	}

	public FitWindowsFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public FitWindowsFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		setFitsSystemWindows(true);
	}

	public void setCallback(@Nullable Callback callback) {
		mCallback = callback;
	}

	@Override
	@SuppressLint("ClickableViewAccessibility")
	public boolean onTouchEvent(MotionEvent ev) {
		switch (ev.getActionMasked()) {
			case MotionEvent.ACTION_DOWN:
				if (ev.getY() < getPaddingTop() || getHeight() - ev.getY() < getPaddingBottom()) {
					mSwipeIntercepted = true;
					return true;
				}
			case MotionEvent.ACTION_MOVE:
			case MotionEvent.ACTION_HOVER_MOVE:
				if (mSwipeIntercepted)
					return true;
			case MotionEvent.ACTION_UP:
				if (mCallback != null && mSwipeIntercepted) {
					mCallback.showUi();
				}
			case MotionEvent.ACTION_CANCEL:
				mSwipeIntercepted = false;
				break;
		}
		return mSwipeIntercepted || super.onTouchEvent(ev);
	}

	interface Callback {

		void showUi();
	}
}
