package org.xtimms.kitsune.core.helpers;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import org.xtimms.kitsune.R;

import java.io.File;

public class ContentShareHelper {

    private final Context mContext;
    private final Intent mIntent;

    public ContentShareHelper(Context context) {
        mContext = context;
        mIntent = new Intent(Intent.ACTION_SEND);
    }

    public void exportFile(File file) {
        mIntent.setType("file/*");
        mIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        mContext.startActivity(Intent.createChooser(mIntent, mContext.getString(R.string.export_file)));
    }

    public void shareImage(File file) {
        mIntent.setType("image/*");
        mIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        mContext.startActivity(Intent.createChooser(mIntent, mContext.getString(R.string.share)));
    }
}
