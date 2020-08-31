package org.xtimms.kitsune.core.storage.downloaders;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;

import java.io.File;
import java.util.concurrent.atomic.AtomicReference;

public abstract class Downloader<T> implements Runnable {

	private final T mSource;
	private final File mDestination;
	private final AtomicReference<Boolean> mStatus;
	@Nullable
	private Callback mCallback;

	public Downloader(@NonNull T source, @NonNull File destination) {
		mSource = source;
		mDestination = destination;
		mStatus = new AtomicReference<>(null);
	}

	public Downloader(@NonNull T source, @NonNull String destination) {
		this(source, new File(destination));
	}

	public void setCallback(@Nullable Callback callback) {
		mCallback = callback;
	}

	@Override
	public final void run() {
		final boolean result = onDownload(mSource, mDestination);
		mStatus.set(result);
	}

	public final boolean isCompleted() {
		return mStatus.get() != null;
	}

	public final boolean isSuccess() {
		return Boolean.TRUE.equals(mStatus.get());
	}

	protected boolean isCancelled() {
		return mCallback != null && mCallback.isCancelled();
	}

	protected boolean isPaused() {
		return mCallback != null && mCallback.isPaused();
	}

	@WorkerThread
	protected abstract boolean onDownload(@NonNull T source, @NonNull File destination);

	public interface Callback {

		@WorkerThread
		boolean isCancelled();

		@SuppressWarnings("SameReturnValue")
		@WorkerThread
		boolean isPaused();
	}
}
