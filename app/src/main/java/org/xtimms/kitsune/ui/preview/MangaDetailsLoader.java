package org.xtimms.kitsune.ui.preview;

import android.content.AsyncTaskLoader;
import android.content.Context;
import androidx.annotation.NonNull;

import org.xtimms.kitsune.core.ObjectWrapper;
import org.xtimms.kitsune.core.models.MangaChapter;
import org.xtimms.kitsune.core.models.MangaDetails;
import org.xtimms.kitsune.core.models.MangaHeader;
import org.xtimms.kitsune.core.models.SavedChapter;
import org.xtimms.kitsune.source.MangaProvider;
import org.xtimms.kitsune.source.OfflineManga;
import org.xtimms.kitsune.core.storage.db.SavedChaptersRepository;
import org.xtimms.kitsune.core.storage.db.SavedChaptersSpecification;

import java.util.ArrayList;

@SuppressWarnings("ALL")
final class MangaDetailsLoader extends AsyncTaskLoader<ObjectWrapper<MangaDetails>> {

	private final MangaHeader mManga;

	public MangaDetailsLoader(Context context, MangaHeader mangaHeader) {
		super(context);
		mManga = mangaHeader;
	}

	@Override
	@NonNull
	public ObjectWrapper<MangaDetails> loadInBackground() {
		try {
			final MangaProvider provider = MangaProvider.get(getContext(), mManga.provider);
			final MangaDetails details = provider.getDetails(mManga);
			if (!(provider instanceof OfflineManga)) {
				final ArrayList<SavedChapter> savedChapters = SavedChaptersRepository.get(getContext())
						.query(new SavedChaptersSpecification().manga(mManga));
				if (savedChapters != null) {
					for (SavedChapter o : savedChapters) {
						MangaChapter ch = details.chapters.findItemById(o.id);
						if (ch != null) {
							ch.addFlag(MangaChapter.FLAG_CHAPTER_SAVED);
						}
					}
				}
			}
			return new ObjectWrapper<>(details);
		} catch (Exception e) {
			e.printStackTrace();
			return new ObjectWrapper<>(e);
		}
	}
}
