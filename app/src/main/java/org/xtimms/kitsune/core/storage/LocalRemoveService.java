package org.xtimms.kitsune.core.storage;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.Nullable;

import org.xtimms.kitsune.utils.CollectionsUtils;
import org.xtimms.kitsune.core.models.MangaChapter;
import org.xtimms.kitsune.core.models.MangaHeader;
import org.xtimms.kitsune.core.models.SavedChapter;
import org.xtimms.kitsune.core.models.SavedManga;
import org.xtimms.kitsune.core.models.SavedPage;
import org.xtimms.kitsune.core.storage.db.SavedChaptersRepository;
import org.xtimms.kitsune.core.storage.db.SavedChaptersSpecification;
import org.xtimms.kitsune.core.storage.db.SavedMangaRepository;
import org.xtimms.kitsune.core.storage.db.SavedPagesRepository;
import org.xtimms.kitsune.core.storage.db.SavedPagesSpecification;
import org.xtimms.kitsune.core.storage.files.SavedPagesStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class LocalRemoveService extends IntentService {

	private static final String EXTRA_MANGA = "saved_manga";
	private static final String EXTRA_CHAPTERS = "saved_chapters";
	private static final String EXTRA_REMOVE_MANGA = "remove_manga";

	public LocalRemoveService() {
		super("LocalRemove");
	}

	@Override
	protected void onHandleIntent(@Nullable Intent intent) {
		if (intent == null) return;
		try {
			final SavedMangaRepository mangaRepository = SavedMangaRepository.get(this);
			final SavedChaptersRepository chaptersRepository = SavedChaptersRepository.get(this);

			final SavedManga manga = mangaRepository.find(Objects.requireNonNull(intent.getParcelableExtra(EXTRA_MANGA)));
			assert manga != null;
			final ArrayList<SavedChapter> chapters = new ArrayList<>();
			if (intent.hasExtra(EXTRA_CHAPTERS)) {
				final ArrayList<MangaChapter> chList = intent.getParcelableArrayListExtra(EXTRA_CHAPTERS);
                assert chList != null;
                for (MangaChapter ch : chList) {
					chapters.add(SavedChapter.from(ch, manga.id));
				}
			} else {
				chapters.addAll(Objects.requireNonNull(chaptersRepository.query(new SavedChaptersSpecification().manga(manga))));
			}

			final SavedPagesRepository pagesRepository = SavedPagesRepository.get(this);
			final SavedPagesStorage pagesStorage = new SavedPagesStorage(manga);

			for (SavedChapter chapter : chapters) {
				final List<SavedPage> pages = pagesRepository.query(new SavedPagesSpecification(chapter));
                assert pages != null;
                for (SavedPage page : pages) {
					pagesStorage.remove(page);
					pagesRepository.remove(page);
				}
				chaptersRepository.remove(chapter);
			}
			if (intent.getBooleanExtra(EXTRA_REMOVE_MANGA, false)) {
				mangaRepository.remove(manga);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void start(Context context, MangaHeader manga, @Nullable ArrayList<MangaChapter> chapters, boolean removeManga) {
		final Intent intent = new Intent(context, LocalRemoveService.class);
		intent.putExtra(EXTRA_MANGA, manga);
		if (chapters != null) {
			intent.putExtra(EXTRA_CHAPTERS, chapters);
		}
		intent.putExtra(EXTRA_REMOVE_MANGA, removeManga);
		context.startService(intent);
	}

	public static void start(Context context, MangaHeader manga, MangaChapter chapter) {
		start(context, manga, CollectionsUtils.arrayListOf(chapter), false);
	}

	public static void start(Context context, MangaHeader manga) {
		start(context, manga, null, true);
	}
}