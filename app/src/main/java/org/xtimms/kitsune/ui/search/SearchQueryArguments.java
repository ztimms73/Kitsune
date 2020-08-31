package org.xtimms.kitsune.ui.search;

import android.os.Bundle;
import androidx.annotation.NonNull;

public final class SearchQueryArguments {

	@NonNull
	public final String query;
	@NonNull
	public String providerCName;
	public int page;

	public SearchQueryArguments(@NonNull String query, @NonNull String providerCName) {
		this.query = query;
		this.providerCName = providerCName;
		this.page = 0;
	}

	@NonNull
	public Bundle toBundle() {
		final Bundle bundle = new Bundle(3);
		bundle.putString("query", query);
		bundle.putString("cname", providerCName);
		bundle.putInt("page", page);
		return bundle;
	}

	public static SearchQueryArguments from(@NonNull Bundle bundle) {
		SearchQueryArguments searchQueryArguments = new SearchQueryArguments(bundle.getString("query", ""), bundle.getString("cname", ""));
		searchQueryArguments.page = bundle.getInt("page", 0);
		return searchQueryArguments;
	}
}
