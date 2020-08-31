package org.xtimms.kitsune.core.storage;

import org.xtimms.kitsune.core.models.SavedManga;

final class SavedMangaSummary {

	public final SavedManga manga;
	public final int savedChapters;

	SavedMangaSummary(SavedManga manga, int savedChapters) {
		this.manga = manga;
		this.savedChapters = savedChapters;
	}
}
