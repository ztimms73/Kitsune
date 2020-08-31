package org.xtimms.kitsune.ui.reader.pager;

import android.view.GestureDetector;

public class RtlPagerReaderFragment extends PagerReaderFragment {

	@Override
	protected PagerReaderAdapter onCreateAdapter(GestureDetector gestureDetector) {
		return new RtlPagerReaderAdapter(mPages, gestureDetector);
	}

	@Override
	public int getCurrentPageIndex() {
		return invertIndex(super.getCurrentPageIndex());
	}

	@Override
	public void scrollToPage(int index) {
		super.scrollToPage(invertIndex(index));
	}

	@Override
	public void smoothScrollToPage(int index) {
		super.smoothScrollToPage(invertIndex(index));
	}

	@Override
	public void onPageChanged(int page) {
		super.onPageChanged(invertIndex(page));
	}

	private int invertIndex(int index) {
		return mPages.size() - index - 1;
	}

	@Override
	public boolean moveRight() {
		return super.moveLeft();
	}

	@Override
	public boolean moveLeft() {
		return super.moveRight();
	}
}
