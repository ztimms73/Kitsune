package org.xtimms.kitsune.ui.reader.pager;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import android.view.GestureDetector;
import android.view.ViewGroup;

import org.xtimms.kitsune.core.models.MangaPage;

import java.util.ArrayList;

class PagerReaderAdapter extends RecyclerPagerAdapter<PageView> {

	private final ArrayList<MangaPage> mDataset;
	private final GestureDetector mGestureDetector;

	PagerReaderAdapter(ArrayList<MangaPage> dataset, GestureDetector gestureDetector) {
		mGestureDetector = gestureDetector;
		mDataset = dataset;
	}

	@Override
	protected PageView onCreateView(@NonNull ViewGroup container) {
		final PageView pageView = new PageView(container.getContext());
		pageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		pageView.setTapDetector(mGestureDetector);
		return pageView;
	}

	@Override
	protected void onBindView(@NonNull PageView view, int position) {
		view.setData(mDataset.get(position));
	}

	@Override
	protected void onRecyclerView(@NonNull PageView view) {
		view.recycle();
	}

	@Override
	public int getCount() {
		return mDataset.size();
	}

	@Override
	public int getItemPosition(@NonNull Object object) {
		PageView view = (PageView) object;
		return mDataset.contains(view.getData()) ? PagerAdapter.POSITION_UNCHANGED : PagerAdapter.POSITION_NONE;
	}

	public void setScaleMode(int paramInt) {
		PageView.scaleMode = paramInt;
	}


}
