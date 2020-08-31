package org.xtimms.kitsune.ui.discover.bookmarks;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class BookmarkSpanSizeLookup extends GridLayoutManager.SpanSizeLookup {

	private final int mMaxSpans;
	private final RecyclerView.Adapter mAdapter;

	public BookmarkSpanSizeLookup(RecyclerView.Adapter adapter, int maxSpans) {
		mAdapter = adapter;
		mMaxSpans = maxSpans;
	}

	@Override
	public int getSpanSize(int position) {
		if (mAdapter.getItemViewType(position) == BookmarksListAdapter.ItemViewType.TYPE_ITEM_HEADER) {
			return mMaxSpans;
		}
		return 1;
	}
}
