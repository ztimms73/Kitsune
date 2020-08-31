package org.xtimms.kitsune.ui.shelf;

import org.xtimms.kitsune.R;
import org.xtimms.kitsune.core.models.Category;
import org.xtimms.kitsune.core.models.ListHeader;
import org.xtimms.kitsune.core.models.MangaFavourite;
import org.xtimms.kitsune.core.models.MangaHeader;
import org.xtimms.kitsune.core.models.MangaHistory;
import org.xtimms.kitsune.core.models.SavedManga;

import java.util.ArrayList;
import java.util.List;

public final class ShelfUpdater {

	public static void update(ShelfAdapter adapter, ShelfContent content) {
		ArrayList<Object> dataset = new ArrayList<>(content.tips);
		if (content.recent != null || !content.history.isEmpty()) {
			dataset.add(new ListHeader(R.string.action_history, ShelfContent.SECTION_HISTORY));
			if (content.recent != null) {
				dataset.add(content.recent);
			}
			for (MangaHistory o : content.history) {
				dataset.add(MangaHeader.from(o));
			}
		}
		for (Category category : content.favourites.keySet()) {
			List<MangaFavourite> favourites = content.favourites.get(category);
			if (favourites != null && !favourites.isEmpty()) {
				dataset.add(new ListHeader(category.name, category.id));
				dataset.addAll(favourites);
			}
		}
		if (!content.savedManga.isEmpty()) {
			dataset.add(new ListHeader(R.string.saved_manga, ShelfContent.SECTION_SAVED));
			for (SavedManga o : content.savedManga) {
				dataset.add(SavedManga.from(o));
			}
		}
		if (!content.recommended.isEmpty()) {
			dataset.add(new ListHeader(R.string.recommendations, null /*TODO*/));
			dataset.addAll(content.recommended);
		}
		dataset.trimToSize();
		adapter.updateData(dataset);
	}
}
