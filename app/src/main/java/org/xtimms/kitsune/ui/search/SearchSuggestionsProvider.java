package org.xtimms.kitsune.ui.search;

import android.content.Context;
import android.content.SearchRecentSuggestionsProvider;
import android.provider.SearchRecentSuggestions;
import androidx.annotation.NonNull;

public final class SearchSuggestionsProvider extends SearchRecentSuggestionsProvider {

	final static String AUTHORITY = "org.xtimms.kitsune.SEARCH_SUGGEST";
	final static int MODE = DATABASE_MODE_QUERIES;

	public SearchSuggestionsProvider() {
		setupSuggestions(AUTHORITY, MODE);
	}

	@NonNull
	public static SearchRecentSuggestions getSuggestions(Context context) {
		return new SearchRecentSuggestions(context, SearchSuggestionsProvider.AUTHORITY, SearchSuggestionsProvider.MODE);
	}

}
