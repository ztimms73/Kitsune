package org.xtimms.kitsune.core.storage.db;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class HistorySpecification implements SqlSpecification {

	@Nullable
	private String mOrderBy = null;
	@Nullable
	private String mLimit = null;

	private boolean mRemoved = false;

	public HistorySpecification removed(boolean value) {
		mRemoved = value;
		return this;
	}

	public HistorySpecification orderByDate(boolean descending) {
		mOrderBy = "updated_at";
		if (descending) {
			mOrderBy += " DESC";
		}
		return this;
	}

	public void orderByName(boolean descending) {
		mOrderBy = "name";
		if (descending) {
			mOrderBy += " DESC";
		}
	}

	public HistorySpecification limit(int limit) {
		mLimit = String.valueOf(limit);
		return this;
	}

	@Override
	public String getSelection() {
		return "removed = ?";
	}

	@Override
	public String[] getSelectionArgs() {
		return new String[]{
			mRemoved ? "1" : "0"
		};
	}

	@Nullable
	@Override
	public String getOrderBy() {
		return mOrderBy;
	}

	@Nullable
	@Override
	public String getLimit() {
		return mLimit;
	}

	@NonNull
	public Bundle toBundle() {
		final Bundle bundle = new Bundle(3);
		bundle.putString("limit", mLimit);
		bundle.putString("order_by", mOrderBy);
		bundle.putBoolean("removed", mRemoved);
		return bundle;
	}

	public static HistorySpecification from(Bundle bundle) {
		final HistorySpecification specification = new HistorySpecification();
		specification.mLimit = bundle.getString("limit", null);
		specification.mOrderBy = bundle.getString("order_by", null);
		specification.mRemoved = bundle.getBoolean("removed", false);
		return specification;
	}
}
