package org.xtimms.kitsune.ui.reader;

import android.content.AsyncTaskLoader;
import android.content.Context;

import org.xtimms.kitsune.core.ListWrapper;
import org.xtimms.kitsune.core.models.MangaChapter;
import org.xtimms.kitsune.core.models.MangaHeader;
import org.xtimms.kitsune.core.models.MangaPage;
import org.xtimms.kitsune.source.MangaProvider;

@SuppressWarnings("ALL")
public final class ChapterLoader extends AsyncTaskLoader<ListWrapper<MangaPage>> {

	private final MangaChapter mChapter;

	public ChapterLoader(Context context, MangaHeader manga, MangaChapter chapter) {
		super(context);
		mChapter = chapter;
	}


	@Override
	public ListWrapper<MangaPage> loadInBackground() {
		try {
			MangaProvider provider = MangaProvider.get(getContext(), mChapter.provider);
			return new ListWrapper<>(provider.getPages(mChapter.url));
		} catch (Exception e) {
			e.printStackTrace();
			return new ListWrapper<>(e);
		}
	}
}
