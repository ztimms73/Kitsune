package org.xtimms.kitsune.core.storage.db;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public interface Repository<T> {

	boolean add(@NonNull T t);
	boolean remove(@NonNull T t);
	boolean update(@NonNull T t);
	void clear();
	boolean contains(@NonNull T t);

	@Nullable
	List<T> query(@NonNull SqlSpecification specification);
}
