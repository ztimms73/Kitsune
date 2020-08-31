package org.xtimms.kitsune.core.storage.db;

import androidx.annotation.Nullable;

public interface SqlSpecification {

	@Nullable
	String getSelection();
	@Nullable
	String[] getSelectionArgs();
	@Nullable
	String getOrderBy();
	@Nullable
	String getLimit();
}
