package org.xtimms.kitsune.core.common.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.SimpleAdapter;

import androidx.appcompat.app.AlertDialog;

import org.xtimms.kitsune.R;
import org.xtimms.kitsune.utils.FilesystemUtils;
import org.xtimms.kitsune.utils.LayoutUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StorageSelectDialog implements DialogInterface.OnClickListener, SimpleAdapter.ViewBinder {

    private final AlertDialog mDialog;
    private final List<File> mStorages;
    private final boolean mOnlyRoots;
    private DirSelectDialog.OnDirSelectListener mDirSelectListener;

    public StorageSelectDialog(Context context) {
        this(context, false);
    }

    public StorageSelectDialog(final Context context, boolean onlyRoots) {
        mOnlyRoots = onlyRoots;
        mStorages = FilesystemUtils.getAvailableStorages(context);
        final Drawable[] icons = LayoutUtils.getThemedIcons(
                context,
                R.drawable.ic_storage_52,
                R.drawable.ic_directory_52
        );
        ArrayList<Map<String, Object>> data = new ArrayList<>(
                mStorages.size() + 1);
        Map<String, Object> m;
        for (int i = 0; i < mStorages.size(); i++) {
            m = new HashMap<>();
            m.put("title", mStorages.get(i).getName());
            m.put("subtitle", context.getString(R.string.size_free, FilesystemUtils.formatSizeMb(FilesystemUtils.getFreeSpaceMb(mStorages.get(i).getPath()))));
            m.put("icon", icons[0]);
            data.add(m);
        }
        if (!onlyRoots) {
            m = new HashMap<>();
            m.put("title", context.getString(R.string.custom_path));
            m.put("subtitle", context.getString(R.string.pick_dir));
            m.put("icon", icons[1]);
            data.add(m);
        }
        SimpleAdapter mAdapter = new SimpleAdapter(
                context,
                data,
                R.layout.item_storage,
                new String[]{"title", "subtitle", "icon"},
                new int[]{android.R.id.text1, android.R.id.text2, R.id.imageView}
        );
        mAdapter.setViewBinder(this);
        mDialog = new AlertDialog.Builder(context)
                .setAdapter(mAdapter, this)
                .setNegativeButton(android.R.string.cancel, null)
                .setCancelable(true)
                .setTitle(R.string.select_storage)
                .create();
    }

    public StorageSelectDialog setDirSelectListener(DirSelectDialog.OnDirSelectListener dirSelectListener) {
        this.mDirSelectListener = dirSelectListener;
        return this;
    }

    public void show() {
        mDialog.show();
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int position) {
        if (position >= mStorages.size()) {
            new DirSelectDialog(mDialog.getContext())
                    .setDirSelectListener(mDirSelectListener)
                    .show();
        } else if (mDirSelectListener != null) {
            File dir = mStorages.get(position);
            if (!mOnlyRoots) {
                dir = FilesystemUtils.getFilesDir(mDialog.getContext(), dir, "saved");
            }
            mDirSelectListener.onDirSelected(dir);
        }
    }

    @Override
    public boolean setViewValue(View view, Object data, String s) {
        if ((view instanceof ImageView) & (data instanceof Drawable)) {
            ((ImageView) view).setImageDrawable((Drawable) data);
            return true;
        } else {
            return false;
        }
    }
}
