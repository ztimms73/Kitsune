package org.xtimms.kitsune.core.storage.db;

import androidx.annotation.Nullable;

public final class CategoriesSpecification implements SqlSpecification {

	@Nullable
	private String mOrderBy = null;

	public CategoriesSpecification orderByDate(boolean descending) {
		mOrderBy = "created_at";
		if (descending) {
			mOrderBy += " DESC";
		}
		return this;
	}

    @Nullable
	@Override
	public String getSelection() {
		return null;
	}

	@Nullable
	@Override
	public String[] getSelectionArgs() {
		return null;
	}

	@Nullable
	@Override
	public String getOrderBy() {
		return mOrderBy;
	}

	@Nullable
	@Override
	public String getLimit() {
		return null;
	}
}