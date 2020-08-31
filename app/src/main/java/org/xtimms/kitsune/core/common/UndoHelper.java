package org.xtimms.kitsune.core.common;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import android.view.View;

import org.xtimms.kitsune.R;

public final class UndoHelper<T> extends BaseTransientBottomBar.BaseCallback<Snackbar> implements View.OnClickListener {

	@NonNull
	private final OnActionUndoCallback<T> mCallback;
	private final int mId;
	private T mData;

	public UndoHelper(int id, @NonNull OnActionUndoCallback<T> callback) {
		mCallback = callback;
		mId = id;
	}

	public void snackbar(@NonNull View view, @StringRes int message, @NonNull T data, int duration) {
		snackbar(view, view.getContext().getString(message), data, duration);
	}

	public void snackbar(@NonNull View view, @NonNull String message, @NonNull T data, int duration) {
		mData = data;
		Snackbar.make(view, message, duration)
				.addCallback(this)
				.setAction(R.string.undo, this)
				.show();
	}

	@Override
	public void onDismissed(Snackbar transientBottomBar, int event) {
		if (event != BaseTransientBottomBar.BaseCallback.DISMISS_EVENT_ACTION) {
			mData = null;
		}
		super.onDismissed(transientBottomBar, event);
	}

	@Override
	public void onClick(View v) {
		if (mData != null) {
			mCallback.onActionUndo(mId, mData);
		}
	}

	public interface OnActionUndoCallback<T> {

		void onActionUndo(int actionId, T data);
	}
}
