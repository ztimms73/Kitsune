package org.xtimms.kitsune.ui.shelf;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.xtimms.kitsune.core.models.Category;
import org.xtimms.kitsune.core.models.MangaFavourite;
import org.xtimms.kitsune.core.models.MangaHeader;
import org.xtimms.kitsune.core.models.MangaHistory;
import org.xtimms.kitsune.core.models.SavedManga;
import org.xtimms.kitsune.core.models.UserTip;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

class ShelfContent {

	static final int SECTION_HISTORY = 2;
	static final int SECTION_SAVED = 3;

	@Nullable
	MangaHistory recent;

	@NonNull
	final ArrayList<UserTip> tips;

	@NonNull
	final ArrayList<MangaHistory> history;

	@NonNull
	final ArrayList<SavedManga> savedManga;

	@NonNull
	final HashMap<Category,List<MangaFavourite>> favourites;

	@NonNull
	final ArrayList<MangaHeader> recommended;

	ShelfContent() {
		tips = new ArrayList<>(4);
		history = new ArrayList<>();
		savedManga = new ArrayList<>();
		favourites = new HashMap<>();
		recommended = new ArrayList<>();
		recent = null;
	}

	public boolean isEmpty() {
		return recent == null && tips.isEmpty() && history.isEmpty() && savedManga.isEmpty() && favourites.isEmpty() && recommended.isEmpty();
	}
}
