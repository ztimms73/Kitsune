package org.xtimms.kitsune.ui.reader.pager;

import androidx.annotation.NonNull;
import android.view.GestureDetector;

import org.xtimms.kitsune.core.models.MangaPage;

import java.util.ArrayList;

final class RtlPagerReaderAdapter extends PagerReaderAdapter {

	RtlPagerReaderAdapter(ArrayList<MangaPage> dataset, GestureDetector gestureDetector) {
		super(dataset, gestureDetector);
	}

	@Override
	protected void onBindView(@NonNull PageView view, int position) {
		super.onBindView(view, getCount() - position - 1);
	}
}
