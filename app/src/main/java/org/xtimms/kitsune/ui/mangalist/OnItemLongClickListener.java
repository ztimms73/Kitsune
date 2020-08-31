package org.xtimms.kitsune.ui.mangalist;

import androidx.recyclerview.widget.RecyclerView;

public interface OnItemLongClickListener<VH extends RecyclerView.ViewHolder> {
    boolean onItemLongClick(VH viewHolder);
}