package org.xtimms.kitsune.core.common.views;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;
import org.xtimms.kitsune.utils.LayoutUtils;

public final class InfiniteRecyclerView extends RecyclerView {

    private final EndlessScrollHelper mEndlessScrollHelper;
    @Nullable
    private OnLoadMoreListener mOnLoadMoreListener = null;
    private boolean mLoadingEnabled = false;

    public InfiniteRecyclerView(Context context) {
        this(context, null, 0);
    }

    public InfiniteRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public InfiniteRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mEndlessScrollHelper = new EndlessScrollHelper();
        this.addOnScrollListener(mEndlessScrollHelper);
    }

    @Override
    public void setAdapter(Adapter adapter) {
        super.setAdapter(adapter);
        if (adapter instanceof EndlessAdapter) {
            mLoadingEnabled = true;
            mEndlessScrollHelper.mLoading = false;
            ((EndlessAdapter) adapter).setHasNext(true);
        }
    }

    public void setOnLoadMoreListener(@Nullable OnLoadMoreListener onLoadMoreListener) {
        mOnLoadMoreListener = onLoadMoreListener;
    }

    public void onLoadingStarted() {
        mEndlessScrollHelper.mLoading = true;
        final Adapter adapter = getAdapter();
        if (adapter instanceof EndlessAdapter) {
            ((EndlessAdapter) adapter).setHasNext(true);
        }
    }

    public void onLoadingFinished(boolean enableNext) {
        mLoadingEnabled = enableNext;
        mEndlessScrollHelper.mLoading = false;
        final Adapter adapter = getAdapter();
        if (adapter instanceof EndlessAdapter) {
            ((EndlessAdapter) adapter).setHasNext(enableNext);
        }
        mEndlessScrollHelper.onScrolled(this, 0, 0);
    }

    public interface OnLoadMoreListener {

        @SuppressWarnings("SameReturnValue")
        boolean onLoadMore();
    }

    public interface EndlessAdapter {
        void setHasNext(boolean hasNext);
    }

    private class EndlessScrollHelper extends OnScrollListener {

        private static final int VISIBLE_THRESHOLD = 2;

        private boolean mLoading = false;

        @Override
        public void onScrolled(@NotNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            int mTotalItemCount = LayoutUtils.getItemCount(recyclerView);
            int mLastVisibleItem = LayoutUtils.findLastVisibleItemPosition(recyclerView);
            if (!mLoading && mLoadingEnabled && mTotalItemCount <= (mLastVisibleItem + VISIBLE_THRESHOLD)) {
                // End has been reached
                // Do something
                if (mOnLoadMoreListener != null) {
                    mLoading = mOnLoadMoreListener.onLoadMore();
                }
            }
        }
    }
}
