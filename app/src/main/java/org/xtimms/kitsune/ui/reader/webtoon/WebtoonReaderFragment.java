package org.xtimms.kitsune.ui.reader.webtoon;

import android.app.Activity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import org.xtimms.kitsune.R;
import org.xtimms.kitsune.core.models.MangaPage;
import org.xtimms.kitsune.core.storage.settings.AppSettings;
import org.xtimms.kitsune.core.storage.settings.ReaderSettings;
import org.xtimms.kitsune.ui.reader.OnOverScrollListener;
import org.xtimms.kitsune.ui.reader.ReaderFragment;
import org.xtimms.kitsune.ui.reader.pager.OverScrollPager;

import java.util.List;

@SuppressWarnings("ALL")
public class WebtoonReaderFragment extends ReaderFragment implements ViewPager.OnPageChangeListener {

	protected OverScrollPager mPager;
	protected WebtoonReaderAdapter mAdapter;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_reader_webtoon, container, false);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		mPager = view.findViewById(R.id.verticalPager);
		mAdapter = onCreateAdapter(new GestureDetector(view.getContext(), new TapDetector()));
		ReaderSettings mSettings = (AppSettings.get(view.getContext())).readerSettings;
		mAdapter.setScaleMode(mSettings.getToWidthScaleMode());
		mPager.setOffscreenPageLimit(2);
		mPager.setAdapter(mAdapter);
		mPager.addOnPageChangeListener(this);
	}

	protected WebtoonReaderAdapter onCreateAdapter(GestureDetector gestureDetector) {
		return new WebtoonReaderAdapter(getPages(), gestureDetector);
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		final Activity activity = getActivity();
		if (activity instanceof OnOverScrollListener) {
			mPager.setOnOverScrollListener((OnOverScrollListener) activity);
		}
	}

	@Override
	public void setPages(List<MangaPage> pages) {
		super.setPages(pages);
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public int getCurrentPageIndex() {
		return mPager.getCurrentItem();
	}

	@Override
	public void scrollToPage(int index) {
		mPager.setCurrentItem(index, false);
	}

	@Override
	public void smoothScrollToPage(int index) {
		mPager.setCurrentItem(index, true);
	}

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

	}

	@Override
	public void onPageSelected(int position) {
		onPageChanged(position);
	}

	@Override
	public void onPageScrollStateChanged(int state) {

	}

	@Override
	public boolean moveLeft() {
		return false;
	}

	@Override
	public boolean moveRight() {
		return false;
	}

	@Override
	public boolean moveUp() {
		return movePrevious();
	}

	@Override
	public boolean moveDown() {
		return moveNext();
	}

	@SuppressWarnings("IntegerDivisionInFloatingPointContext")
	private final class TapDetector extends GestureDetector.SimpleOnGestureListener {

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			if (mPager == null) {
				return false;
			}
			final float x = e.getX();
			final float w3 = mPager.getWidth() / 3;
			if (x < w3) {
				moveLeft();
			} else if (x <= w3 + w3) {
				final float y = e.getY();
				final float h3 = mPager.getHeight() / 3;
				if (y < h3) {
					moveLeft();
				} else if (y <= h3 + h3) {
					toggleUi();
				} else {
					moveRight();
				}
			} else {
				moveRight();
			}
			return true;
		}
	}

	@Override
	public void onRestoreState(@NonNull Bundle savedState) {
		super.onRestoreState(savedState);
		onPageSelected(mPager.getCurrentItem());
	}


}
