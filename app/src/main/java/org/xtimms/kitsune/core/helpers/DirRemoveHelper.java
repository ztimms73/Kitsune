package org.xtimms.kitsune.core.helpers;

import androidx.annotation.Nullable;
import android.util.Log;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

import timber.log.Timber;

public class DirRemoveHelper implements Runnable {

    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();

    @Nullable
    private final File[] mFiles;

    public DirRemoveHelper(File file) {
        mFiles = new File[]{file};
    }

    @SuppressWarnings("NullableProblems")
    public DirRemoveHelper(File[] files) {
        mFiles = files;
    }

    public DirRemoveHelper(File dir, String regexp) {
        final Pattern pattern = Pattern.compile(regexp);
        mFiles = dir.listFiles((dir1, filename) -> pattern.matcher(filename).matches());
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static void removeDir(File dir) {
        if (dir == null || !dir.exists()) {
            Timber.tag("DIRRM").w("not exists: " + (dir != null ? dir.getPath() : "null"));
            return;
        }
        if (dir.isDirectory()) {
            for (File o : Objects.requireNonNull(dir.listFiles())) {
                if (o.isDirectory()) {
                    removeDir(o);
                } else {
                    o.delete();
                }
            }
        }
        Timber.tag("DIRRM").d("removed: " + dir.getPath());
        dir.delete();
    }

    @Override
    public void run() {
        if (mFiles == null) {
            return;
        }
        for (File file : mFiles) {
            removeDir(file);
        }
    }

    public void runAsync() {
        EXECUTOR.execute(this);
    }
}
