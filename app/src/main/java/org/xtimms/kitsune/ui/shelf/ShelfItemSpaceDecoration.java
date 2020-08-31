package org.xtimms.kitsune.ui.shelf;

import android.graphics.Rect;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import org.jetbrains.annotations.NotNull;

public class ShelfItemSpaceDecoration extends RecyclerView.ItemDecoration {

	private final int mSpacing;
    private final RecyclerView.Adapter mAdapter;

	public ShelfItemSpaceDecoration(int spacing, RecyclerView.Adapter adapter, int maxSpanSize) {
		mSpacing = spacing;
		mAdapter = adapter;
    }

	@Override
	public void getItemOffsets(@NotNull Rect outRect, @NotNull View view, RecyclerView parent, @NotNull RecyclerView.State state) {
		final int position = parent.getChildAdapterPosition(view);
		final int itemType = mAdapter.getItemViewType(position);
		switch (itemType) {
			case ShelfItemType.TYPE_RECENT:
			case ShelfItemType.TYPE_TIP:
			case ShelfItemType.TYPE_ITEM_DEFAULT:
			case ShelfItemType.TYPE_ITEM_SMALL:
			case ShelfItemType.TYPE_SAVED:
				outRect.left = mSpacing;
				outRect.right = mSpacing;
				outRect.bottom = mSpacing;
				outRect.top = mSpacing;
		}
	}
}
