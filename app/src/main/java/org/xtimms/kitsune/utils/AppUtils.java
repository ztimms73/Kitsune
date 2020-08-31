package org.xtimms.kitsune.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import org.xtimms.kitsune.R;

public class AppUtils {

    public static String getVersionName(Context context){
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            String version = info.versionName;
            return context.getString(R.string.version_about) + version;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
