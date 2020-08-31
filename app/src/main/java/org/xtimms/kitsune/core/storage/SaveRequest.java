package org.xtimms.kitsune.core.storage;

import android.os.Bundle;
import androidx.annotation.NonNull;

import android.util.SparseBooleanArray;

import org.xtimms.kitsune.core.models.MangaChapter;
import org.xtimms.kitsune.core.models.MangaChaptersList;
import org.xtimms.kitsune.core.models.MangaDetails;

public final class SaveRequest {

	public final MangaDetails manga;
	public final MangaChaptersList chapters;

	public SaveRequest(MangaDetails manga, MangaChaptersList chapters) {
		this.manga = manga;
		this.chapters = chapters;
	}

	public SaveRequest(MangaDetails manga, MangaChapter oneChapter) {
		this.manga = manga;
		this.chapters = new MangaChaptersList(1);
		chapters.add(oneChapter);
	}

	public SaveRequest(MangaDetails manga, SparseBooleanArray selection) {
		this.manga = manga;
		this.chapters = new MangaChaptersList();
		for (int i = 0; i < manga.chapters.size(); i++) {
			if (selection.get(i, false)) {
				this.chapters.add(manga.chapters.get(i));
			}
		}
	}

	@NonNull
	public Bundle toBundle() {
		final Bundle bundle = new Bundle(2);
		bundle.putParcelable("manga", manga);
		bundle.putParcelableArrayList("chapters", chapters);
		return bundle;
	}

	@NonNull
	public static SaveRequest from(Bundle bundle) {
		return new SaveRequest(
				bundle.getParcelable("manga"),
				new MangaChaptersList(bundle.getParcelableArrayList("chapters"))
		);
	}
}
