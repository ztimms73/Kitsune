package org.xtimms.kitsune.ui.reader;

import android.content.Context;
import android.content.DialogInterface;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import org.xtimms.kitsune.R;
import org.xtimms.kitsune.core.models.MangaHistory;

final class ReaderModeDialog implements DialogInterface.OnClickListener {

	private final AlertDialog.Builder mBuilder;
	@Nullable
	private OnReaderModeChangeListener mListener = null;

	ReaderModeDialog(@NonNull Context context, @NonNull MangaHistory history) {
		mBuilder = new AlertDialog.Builder(context);
		mBuilder.setTitle(R.string.reader_mode);
		mBuilder.setSingleChoiceItems(R.array.reader_modes, history.readerPreset, this);
		mBuilder.setNegativeButton(R.string.close, null);
		mBuilder.setCancelable(true);
	}

	ReaderModeDialog setListener(@Nullable OnReaderModeChangeListener listener) {
		mListener = listener;
		return this;
	}

	public void show() {
		mBuilder.create().show();
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		if (mListener != null) {
			mListener.onReaderModeChanged((short) which);
		}
		dialog.dismiss();
	}

	interface OnReaderModeChangeListener {

		void onReaderModeChanged(short mode);
	}
}
