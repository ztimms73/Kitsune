package org.xtimms.kitsune.core.helpers;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;

import org.xtimms.kitsune.core.common.base.AppBaseActivity;

import java.io.File;

public class PermissionsHelper {
    public static final int REQUEST_CODE = 10;

    public static boolean accessCommonDir(AppBaseActivity activity, String dirname) {
        //noinspection deprecation
        File dir = Environment.getExternalStoragePublicDirectory(dirname);
        if (dir.canWrite()) {
            return true;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StorageManager sm = (StorageManager) activity.getSystemService(Context.STORAGE_SERVICE);
            assert sm != null;
            StorageVolume volume = sm.getPrimaryStorageVolume();
            //noinspection deprecation
            Intent intent = volume.createAccessIntent(dirname);
            activity.startActivityForResult(intent, REQUEST_CODE);
            return false;
        } else
            return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                    && activity.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }
}
