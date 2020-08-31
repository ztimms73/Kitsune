package org.xtimms.kitsune.core.common.base;

import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.ColorRes;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.xtimms.kitsune.R;
import org.xtimms.kitsune.utils.ThemeUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

@SuppressWarnings({"rawtypes", "SuspiciousMethodCalls"})
public abstract class AppBaseActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSION = 112;

    private boolean mHomeAsUpEnabled = false;
    private int mTheme = 0;
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    @Nullable
    private ArrayList<WeakReference<AsyncTask>> mLoaders;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTheme = ThemeUtils.getAppTheme(this);
        setTheme(ThemeUtils.getAppThemeRes(mTheme));
    }

    public void registerLoaderTask(@SuppressWarnings("rawtypes") AsyncTask task) {
        if (mLoaders == null) {
            mLoaders = new ArrayList<>();
        }
        mLoaders.add(new WeakReference<>(task));
    }

    public boolean isDarkTheme() {
        return mTheme > 18;
    }

    public void enableHomeAsUp() {
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null && !mHomeAsUpEnabled) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            mHomeAsUpEnabled = true;
        }
    }

    public void enableHomeAsClose() {
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null && !mHomeAsUpEnabled) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_cancel_light);
            mHomeAsUpEnabled = true;
        }
    }

    @Override
    public void setSupportActionBar(@Nullable Toolbar toolbar) {
        super.setSupportActionBar(toolbar);
        boolean mActionBarVisible = toolbar != null;
    }

    public void setSupportActionBar(@IdRes int toolbarId) {
        setSupportActionBar(findViewById(toolbarId));
    }

    public void setSubtitle(@Nullable CharSequence subtitle) {
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setSubtitle(subtitle);
        }
    }

    public void enableTransparentStatusBar(@ColorRes int color) {
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            if (color != 0) {
                window.setStatusBarColor(ContextCompat.getColor(this, color));
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home && mHomeAsUpEnabled) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void setKeepScreenOn(boolean flag) {
        if (flag) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    public void checkPermissions(int requestCode, String... permissions) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            for (String o : permissions) {
                onPermissionGranted(requestCode, o);
            }
        }
        final ArrayList<String> required = new ArrayList<>(permissions.length);
        for (String o : permissions) {
            if (ContextCompat.checkSelfPermission(this, o) != PackageManager.PERMISSION_GRANTED) {
                required.add(o);
            } else {
                onPermissionGranted(requestCode, o);
            }
        }
        if (!required.isEmpty()) {
            ActivityCompat.requestPermissions(this, required.toArray(new String[0]), requestCode);
        }
    }

    public boolean checkPermission(String permission) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (ContextCompat.checkSelfPermission(this,
                permission) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
            ActivityCompat.requestPermissions(this,
                    new String[]{permission},
                    REQUEST_PERMISSION);
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        for (int i = 0; i < permissions.length; i++) {
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                onPermissionGranted(requestCode, permissions[i]);
            }
        }
    }

    protected void onPermissionGranted(int requestCode, String permission) {

    }

    protected void stub() {

    }

    protected void unregisterLoaderTask(AsyncTask task) {
        if (mLoaders != null) {
            mLoaders.remove(task);
        }
    }

}

