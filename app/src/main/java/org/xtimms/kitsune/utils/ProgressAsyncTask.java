package org.xtimms.kitsune.utils;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.xtimms.kitsune.R;
import org.xtimms.kitsune.core.common.base.AppBaseActivity;

import java.lang.ref.WeakReference;

public abstract class ProgressAsyncTask<Param, Progress, Result> extends AsyncTask<Param, Progress, Result> implements DialogInterface.OnDismissListener, DialogInterface.OnCancelListener {

    @Nullable
    private ProgressDialog mDialog;
    private final WeakReference<AppBaseActivity> mActivityRef;

    public ProgressAsyncTask(AppBaseActivity activity) {
        mActivityRef = new WeakReference<>(activity);
        mDialog = new ProgressDialog(activity);
        mDialog.setMessage(activity.getString(R.string.loading));
        mDialog.setOnDismissListener(this);
        mDialog.setOwnerActivity(activity);
        mDialog.setOnCancelListener(this);
        mDialog.setIndeterminate(true);
        mDialog.setCancelable(true);
        activity.registerLoaderTask(this);
    }

    @Nullable
    protected AppBaseActivity getActivity() {
        return mActivityRef.get();
    }

    @Nullable
    public ProgressDialog getDialog() {
        return mDialog;
    }

    public void setCancelable(boolean cancelable) {
        if (mDialog != null) {
            mDialog.setCancelable(cancelable);
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        AppBaseActivity activity = getActivity();
        if (activity != null) {
            if (mDialog != null) {
                mDialog.show();
            }
            onPreExecute(activity);
        }
    }

    @Override
    protected void onPostExecute(Result result) {
        super.onPostExecute(result);
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
        AppBaseActivity activity = getActivity();
        if (activity != null) {
            onPostExecute(activity, result);
        }
    }

    @Override
    protected void onProgressUpdate(Progress[] values) {
        super.onProgressUpdate(values);
        AppBaseActivity activity = getActivity();
        if (activity != null) {
            onProgressUpdate(activity, values);
        }
    }

    @SafeVarargs
    public final void start(Param... params) {
        this.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
    }

    private boolean canCancel() {
        return getStatus() != Status.FINISHED;
    }

    protected void onProgressUpdate(@NonNull AppBaseActivity activity, Progress[] values) {
    }

    protected void onPreExecute(@NonNull AppBaseActivity activity) {
    }

    protected void onPostExecute(@NonNull AppBaseActivity activity, Result result) {
    }

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        mDialog = null;
    }

    @Override
    public void onCancel(DialogInterface dialogInterface) {
        if (this.canCancel()) {
            this.cancel(true);
        }
        mDialog = null;
    }
}