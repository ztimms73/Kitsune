package org.xtimms.kitsune.ui.reader.pager;

import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;
import org.xtimms.kitsune.core.models.MangaPage;
import org.xtimms.kitsune.ui.reader.loader.PageLoadTask;

import java.lang.ref.WeakReference;

public class PageWrapper {
    public static final int STATE_QUEUED = 0;
    public static final int STATE_PROGRESS = 1;
    public static final int STATE_LOADED = 2;

    public final MangaPage page;
    public final int position;

    final int mState;
    @Nullable
    final
    String mFilename;
    @Nullable
    final
    Exception mError;
    @Nullable
    final
    WeakReference<PageLoadTask> mTaskRef;
    private boolean mConverted;

    public PageWrapper(MangaPage page, int position) {
        this.page = page;
        this.position = position;
        if (page.url.startsWith("/")) {
            mState = STATE_LOADED;
            mFilename = page.url;
        } else {
            mState = STATE_QUEUED;
            mFilename = null;
        }
        mError = null;
        mTaskRef = null;
        mConverted = false;
    }

    @Nullable
    public String getFilename() {
        return mFilename;
    }

    @Nullable
    public Exception getError() {
        return mError;
    }

    public int getState() {
        return mState;
    }

    public boolean isLoaded() {
        return mState == STATE_LOADED;
    }

    @Nullable
    PageLoadTask getLoadTask() {
        return mTaskRef == null ? null : mTaskRef.get();
    }

    public void setConverted() {
        mConverted = true;
    }

    public boolean isConverted() {
        return mConverted;
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && (
                (obj instanceof PageWrapper && ((PageWrapper) obj).page.equals(this.page))
                        || (obj instanceof MangaPage && obj.equals(this.page))
        );
    }

    //only debug info
    @NotNull
    @Override
    public String toString() {
        return "page " + position + " id: " + page.id + " filename: " + mFilename;
    }
}
