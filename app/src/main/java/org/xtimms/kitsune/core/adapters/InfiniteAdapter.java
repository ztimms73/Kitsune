package org.xtimms.kitsune.core.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;
import org.xtimms.kitsune.R;
import org.xtimms.kitsune.core.common.PagedList;
import org.xtimms.kitsune.utils.LayoutUtils;

public abstract class InfiniteAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int VIEW_ITEM = 1;
    private static final int VIEW_PROGRESS = 0;

    private final PagedList<T> mDataset;
    private final int mVisibleThreshold = 2;
    private int mLastVisibleItem, mTotalItemCount;
    private boolean mLoading;
    private OnLoadMoreListener mOnLoadMoreListener;

    public InfiniteAdapter(PagedList<T> dataset, RecyclerView recyclerView) {
        mDataset = dataset;
        attach(recyclerView);
    }

    public void attach(RecyclerView recyclerView) {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NotNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mTotalItemCount = LayoutUtils.getItemCount(recyclerView);
                mLastVisibleItem = LayoutUtils.findLastVisibleItemPosition(recyclerView);
                if (!mLoading && isLoadEnabled() && mTotalItemCount <= (mLastVisibleItem + mVisibleThreshold)) {
                    // End has been reached
                    // Do something
                    if (mOnLoadMoreListener != null) {
                        mOnLoadMoreListener.onLoadMore();
                        mLoading = true;
                    }
                }
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position) != null ? VIEW_ITEM : VIEW_PROGRESS;
    }

    @Override
    public long getItemId(int position) {
        if (position == mDataset.size()) {
            return 0;
        } else {
            return getItemId(mDataset.get(position));
        }
    }

    @NotNull
    @Override
    public final RecyclerView.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_ITEM) {
            return onCreateHolder(parent);
        } else {
            return new ProgressViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_progress, parent, false));
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public final void onBindViewHolder(@NotNull RecyclerView.ViewHolder holder, int position) {
        T item = getItem(position);
        if (item != null) {
            onBindHolder((VH) holder, item, position);
        } else if (holder instanceof ProgressViewHolder) {
            ((ProgressViewHolder) holder).setVisible(isLoadEnabled());
        }
    }

    public void setLoaded() {
        mLoading = false;
    }

    @Override
    public int getItemCount() {
        return mDataset.size() + 1;
    }

    @Nullable
    public T getItem(int position) {
        if (position == mDataset.size()) {
            return null;
        } else {
            return mDataset.get(position);
        }
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        mOnLoadMoreListener = onLoadMoreListener;
    }

    private boolean isLoadEnabled() {
        return mDataset.size() != 0 && mDataset.isHasNext();
    }

    public abstract VH onCreateHolder(ViewGroup parent);

    public abstract void onBindHolder(VH viewHolder, T data, int position);

    public abstract long getItemId(T data);

    public interface OnLoadMoreListener {
        @SuppressWarnings("SameReturnValue")
        boolean onLoadMore();
    }

    private static class ProgressViewHolder extends RecyclerView.ViewHolder {
        private final ProgressBar mProgressBar;

        ProgressViewHolder(View v) {
            super(v);
            mProgressBar = v.findViewById(R.id.progressBar);
        }

        public void setVisible(boolean visible) {
            mProgressBar.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
        }
    }
}
