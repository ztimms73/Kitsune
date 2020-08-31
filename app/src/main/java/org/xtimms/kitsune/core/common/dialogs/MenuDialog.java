package org.xtimms.kitsune.core.common.dialogs;

import android.content.Context;
import android.content.DialogInterface;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;

import java.util.ArrayList;

public final class MenuDialog<D> implements DialogInterface.OnClickListener {

    private final AlertDialog.Builder mBuilder;
    private final ArrayList<SimpleMenuItem> mMenuItems = new ArrayList<>();
    @Nullable
    private OnMenuItemClickListener<D> mItemClickListener = null;
    private D mData;

    public MenuDialog(@NonNull Context context) {
        mBuilder = new AlertDialog.Builder(context);
        mBuilder.setCancelable(true);
    }

    public void addItem(@IdRes int id, @StringRes int title) {
        mMenuItems.add(new SimpleMenuItem(id, mBuilder.getContext().getString(title)));
    }

    public MenuDialog<D> addItem(@IdRes int id, @NonNull String title) {
        mMenuItems.add(new SimpleMenuItem(id, title));
        return this;
    }

    public MenuDialog<D> setTitle(@StringRes int title) {
        mBuilder.setTitle(title);
        return this;
    }

    public MenuDialog<D> setTitle(@Nullable CharSequence title) {
        mBuilder.setTitle(title);
        return this;
    }

    public MenuDialog<D> setItemClickListener(@Nullable OnMenuItemClickListener<D> listener) {
        mItemClickListener = listener;
        return this;
    }

    public AlertDialog create(D data) {
        mData = data;
        final CharSequence[] items = new CharSequence[mMenuItems.size()];
        for (int i = 0; i < items.length; i++) {
            items[i] = mMenuItems.get(i).title;
        }
        mBuilder.setItems(items, this);
        return mBuilder.create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which >= 0 && mItemClickListener != null && which < mMenuItems.size()) {
            mItemClickListener.onMenuItemClick(mMenuItems.get(which).id, mData);
        }
    }

    public interface OnMenuItemClickListener<D> {

        void onMenuItemClick(@IdRes int id, D d);
    }

    private static class SimpleMenuItem {

        @IdRes
        final int id;
        final String title;

        private SimpleMenuItem(@IdRes int id, String title) {
            this.id = id;
            this.title = title;
        }
    }
}
