package org.xtimms.kitsune.ui.search;

import android.content.AsyncTaskLoader;
import android.content.Context;

import org.xtimms.kitsune.core.ListWrapper;
import org.xtimms.kitsune.source.MangaProvider;

@SuppressWarnings("ALL")
public final class SearchLoader extends AsyncTaskLoader<ListWrapper> {

	private final SearchQueryArguments mArguments;

	public SearchLoader(Context context, SearchQueryArguments arguments) {
		super(context);
		mArguments = arguments;
	}

	@Override
	public ListWrapper loadInBackground() {
		try {
			//noinspection unchecked
			return new ListWrapper(MangaProvider.get(getContext(), this.mArguments.providerCName).query(this.mArguments.query, this.mArguments.page, -1, -1, new String[0], new String[0]));
		} catch (Exception e) {
			e.printStackTrace();
			return new ListWrapper(e);
		}
	}
}
