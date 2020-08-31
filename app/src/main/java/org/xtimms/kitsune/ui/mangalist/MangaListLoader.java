package org.xtimms.kitsune.ui.mangalist;

import android.annotation.SuppressLint;
import android.content.AsyncTaskLoader;
import android.content.Context;

import org.xtimms.kitsune.core.ListWrapper;
import org.xtimms.kitsune.core.models.MangaHeader;
import org.xtimms.kitsune.source.MangaProvider;

import timber.log.Timber;

@SuppressWarnings("deprecation")
public final class MangaListLoader extends AsyncTaskLoader<ListWrapper<MangaHeader>> {

	private final MangaProvider mProvider;
	private final MangaQueryArguments mArguments;

	@SuppressWarnings("deprecation")
	public MangaListLoader(Context context, MangaProvider mangaProvider, MangaQueryArguments arguments) {
		super(context);
		this.mProvider = mangaProvider;
		mArguments = arguments;
	}

	@SuppressLint("DefaultLocale")
    @SuppressWarnings("deprecation")
	@Override
	public ListWrapper<MangaHeader> loadInBackground() {
		long time = System.currentTimeMillis();
		try {
			return new ListWrapper<>(mProvider.query(mArguments.query, mArguments.page, mArguments.sort, mArguments.additionalSort, mArguments.genresValues(), mArguments.typesValues()));
		} catch (Exception e) {
			e.printStackTrace();
			return new ListWrapper<>(e);
		} finally {
			time = System.currentTimeMillis() - time;
			Timber.tag("timing").i(String.format("%.2fs", time / 1000f));
		}
	}
}
