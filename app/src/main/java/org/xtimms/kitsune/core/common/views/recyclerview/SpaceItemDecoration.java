package org.xtimms.kitsune.core.common.views.recyclerview;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.Px;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

public final class SpaceItemDecoration extends RecyclerView.ItemDecoration {

    @Px
    private final int mSpacing;

    public SpaceItemDecoration(@Px int spacing) {
        mSpacing = spacing;
    }

    @Override
    public void getItemOffsets(Rect outRect, @NotNull View view, @NotNull RecyclerView parent, @NotNull RecyclerView.State state) {
        outRect.set(mSpacing, mSpacing, mSpacing, mSpacing);
    }
}
