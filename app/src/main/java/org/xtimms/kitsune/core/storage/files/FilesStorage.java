package org.xtimms.kitsune.core.storage.files;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;

import java.io.File;

public interface FilesStorage<K,V> {

	@NonNull
	File getFile(@NonNull K key);

	@Nullable
	V get(@NonNull K key);

	void put(@NonNull K key, @Nullable V v);

	boolean remove(@NonNull K key);

	@WorkerThread
	void clear();

	@WorkerThread
	long size();
}
