package org.xtimms.kitsune.ui.shelf;

import android.content.AsyncTaskLoader;
import android.content.Context;

import org.xtimms.kitsune.R;
import org.xtimms.kitsune.core.models.MangaFavourite;
import org.xtimms.kitsune.core.models.MangaHistory;
import org.xtimms.kitsune.core.models.Category;
import org.xtimms.kitsune.core.models.SavedManga;
import org.xtimms.kitsune.core.models.UserTip;
import org.xtimms.kitsune.core.storage.FlagsStorage;
import org.xtimms.kitsune.core.storage.db.CategoriesRepository;
import org.xtimms.kitsune.core.storage.db.CategoriesSpecification;
import org.xtimms.kitsune.core.storage.db.FavouritesRepository;
import org.xtimms.kitsune.core.storage.db.FavouritesSpecification;
import org.xtimms.kitsune.core.storage.db.HistoryRepository;
import org.xtimms.kitsune.core.storage.db.HistorySpecification;
import org.xtimms.kitsune.core.storage.db.SavedMangaRepository;
import org.xtimms.kitsune.core.storage.db.SavedMangaSpecification;
import org.xtimms.kitsune.core.storage.settings.AppSettings;
import org.xtimms.kitsune.core.storage.settings.ShelfSettings;

import java.util.ArrayList;

@SuppressWarnings("ALL")
public class ShelfLoader extends AsyncTaskLoader<ShelfContent> {

	private final int mColumnCount;

	ShelfLoader(Context context, int columnCount) {
		super(context);
		mColumnCount = columnCount;
	}

	@Override
	public ShelfContent loadInBackground() {
		final ShelfContent content = new ShelfContent();
		final ShelfSettings settings = AppSettings.get(getContext()).shelfSettings;
		//tips
		//TODO wizard
		//history
		final HistoryRepository historyRepository = HistoryRepository.get(getContext());
		int len = mColumnCount / 3 * settings.getMaxHistoryRows();
		if (settings.isRecentEnabled()) {
			len++;
		}
		final ArrayList<MangaHistory> history = historyRepository.query(new HistorySpecification().orderByDate(true).limit(len));
		if (history != null && !history.isEmpty()) {
			if (settings.isRecentEnabled()) {
				content.recent = history.get(0);
				history.remove(0);
			}
			if (settings.isHistoryEnabled() && !history.isEmpty()) {
				content.history.addAll(history);
			}
		}
		// saved
		final SavedMangaRepository savedMangaRepository = SavedMangaRepository.get(getContext());
		len = mColumnCount / 3 * settings.getMaxSavedMangaRows();
		final ArrayList<SavedManga> savedManga = savedMangaRepository.query(new SavedMangaSpecification().orderByDate(true).limit(len));
		if (savedManga != null && !savedManga.isEmpty()) {
			if (settings.isSavedMangaEnabled()) {
				content.savedManga.addAll(savedManga);
			}
		}
		//favourites
		if (settings.isFavouritesEnabled()) {
			final CategoriesRepository categoriesRepository = CategoriesRepository.get(getContext());
			ArrayList<Category> categories = categoriesRepository.query(new CategoriesSpecification().orderByDate(true));
			if (categories != null) {
				if (categories.isEmpty()) {
					Category defaultCategory = Category.createDefault(getContext());
					categories.add(defaultCategory);
					categoriesRepository.add(defaultCategory);
					ShelfSettings.onCategoryAdded(getContext(), defaultCategory);
				} else {
					categories = settings.getEnabledCategories(categories);
					final FavouritesRepository favouritesRepository = FavouritesRepository.get(getContext());
					for (Category category : categories) {
						len = mColumnCount / 4 * settings.getMaxFavouritesRows();
						ArrayList<MangaFavourite> favourites = favouritesRepository.query(new FavouritesSpecification().orderByDate(true).category(category.id).limit(len));
						if (favourites != null && !favourites.isEmpty()) {
							content.favourites.put(category, favourites);
						}
					}
				}
			}
		}
		//TODO
		final FlagsStorage flagsStorage = FlagsStorage.get(getContext());
		//final CrashHandler crashHandler = CrashHandler.get();
		//if (crashHandler != null || flagsStorage.isWizardRequired()) {
		//	content.tips.add(0, new UserTip(
		//			getContext().getString(R.string.error_occurred),
		//			getContext().getString(R.string.application_crashed),
		//			R.drawable.ic_bug_red,
		//			R.string.log_see,
		//			R.id.action_crash_report
		//	).addFlag(UserTip.FLAG_DISMISS_BUTTON));
		//}
		if (content.isEmpty()) {
			content.tips.add(new UserTip(
					getContext().getString(R.string.shelf_is_empty),
					getContext().getString(R.string.nothing_here_yet),
					R.drawable.ic_discover_green,
					R.string.discover,
					R.id.action_discover
			).addFlag(UserTip.FLAG_NO_DISMISSIBLE));
		}

		//if (flagsStorage.isWizardRequired()) {
		//	content.tips.add(0, new UserTip(
		//			getContext().getString(R.string.welcome),
		//			getContext().getString(R.string.first_run_tip),
		//			R.drawable.ic_wizard_blue,
		//			R.string._continue,
		//			R.id.action_wizard
		//	).addFlag(UserTip.FLAG_DISMISS_BUTTON));
		//}
		if (flagsStorage.isWizardRequired()) {
			content.tips.add(0, new UserTip(
					getContext().getString(R.string.set_favorite_categories),
					//"Для того, чтобы знать, какую мангу вы прочитали, будете читать или уже читаете",
					getContext().getString(R.string.set_favorite_categories_summary),
					R.drawable.ic_category_tip,
					R.string.add_category,
					R.id.action_category
			).addFlag(UserTip.FLAG_DISMISS_BUTTON));
		}
		return content;


	}

	@Deprecated
	private static int getOptimalCells(int items, int columns) {
		if (items <= columns) {
			return items;
		}
		return items - items % columns;
	}
}
