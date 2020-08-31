package org.xtimms.kitsune.ui.mangalist;

import org.xtimms.kitsune.core.models.MangaGenre;
import org.xtimms.kitsune.core.models.MangaType;

interface FilterCallback {

	void setFilter(int sort, int additionalSort, MangaGenre[] genres, MangaType[] types);
}
