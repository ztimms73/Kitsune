package org.xtimms.kitsune.core.controllers;

import androidx.recyclerview.widget.RecyclerView;

public interface OnHolderClickListener {
    boolean onClick(RecyclerView.ViewHolder viewHolder);
    boolean onLongClick(RecyclerView.ViewHolder viewHolder);
}
