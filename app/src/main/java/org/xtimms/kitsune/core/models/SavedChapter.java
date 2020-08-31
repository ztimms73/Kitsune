package org.xtimms.kitsune.core.models;

import androidx.annotation.NonNull;

public class SavedChapter extends MangaChapter {

	public final long mangaId;

	public SavedChapter(long mangaId, String name, int number, String url, String provider, String scanlator, long date) {
		super(name, number, url, provider, scanlator, date);
		this.mangaId = mangaId;
	}

	public SavedChapter(long id, long mangaId, String name, int number, String url, String provider, String scanlator, long date) {
		super(id, name, number, url, provider, scanlator, date);
		this.mangaId = mangaId;
	}

	@NonNull
	public static SavedChapter from(MangaChapter chapter, long mangaId) {
		return new SavedChapter(
				chapter.id,
				mangaId,
				chapter.name,
				chapter.number,
				chapter.url,
				chapter.provider,
				chapter.scanlator,
				chapter.date
		);
	}
}
