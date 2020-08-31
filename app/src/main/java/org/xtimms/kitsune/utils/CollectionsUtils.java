package org.xtimms.kitsune.utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Pair;
import android.util.SparseBooleanArray;

import org.xtimms.kitsune.core.models.Category;
import org.xtimms.kitsune.core.models.MangaChapter;
import org.xtimms.kitsune.core.models.MangaPage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public abstract class CollectionsUtils {

	@Nullable
	public static MangaChapter findItemById(Collection<MangaChapter> collection, long id) {
		for (MangaChapter o : collection) {
			if (o.id == id) {
				return o;
			}
		}
		return null;
	}

	public static int findChapterPositionById(List<MangaChapter> list, long id) {
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).id == id) {
				return i;
			}
		}
		return -1;
	}

	public static int findPagePositionById(ArrayList<MangaPage> list, long id) {
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).id == id) {
				return i;
			}
		}
		return -1;
	}

	public static int findCategoryPositionById(List<Category> list, long id) {
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).id == id) {
				return i;
			}
		}
		return -1;
	}

	@Nullable
	public static <T> T getOrNull(List<T> list, int position) {
		try {
			return list.get(position);
		} catch (Exception e) {
			return null;
		}
	}

	@Nullable
	public static <T> T getOrNull(T[] array, int position) {
		return position < 0 || position >= array.length ? null : array[position];
	}

	public static <T> ArrayList<T> getIfTrue(T[] items, SparseBooleanArray booleanArray) {
		final ArrayList<T> values = new ArrayList<>();
		for (int i = 0; i < items.length; i++) {
			if (booleanArray.get(i, false)) {
				values.add(items[i]);
			}
		}
		return values;
	}

	public static <T> boolean contains(@NonNull T[] array, T value) {
		for (T o : array) {
			if (o != null && o.equals(value)) {
				return true;
			}
		}
		return false;
	}

	public static String toString(@NonNull Object[] elements, @NonNull String delimiter) {
		final StringBuilder builder = new StringBuilder();
		boolean nonFirst = false;
		for (Object o: elements) {
			if (nonFirst) {
				builder.append(delimiter);
			} else {
				nonFirst = true;
			}
			builder.append(o.toString());
		}
		return builder.toString();
	}

	public static void swap(SparseBooleanArray booleanArray, int x, int p, boolean defaultValue) {
		boolean value = booleanArray.get(x, defaultValue);
		booleanArray.put(x, booleanArray.get(p, defaultValue));
		booleanArray.put(p, value);
	}

	@NonNull
	public static int[] convertToInt(@NonNull String[] strings, int defaultValue) {
		final int[] res = new int[strings.length];
		for (int i = 0; i < res.length; i++) {
			try {
				res[i] = Integer.parseInt(strings[i]);
			} catch (NumberFormatException e) {
				res[i] = defaultValue;
			}
		}
		return res;
	}

	public static int indexOf(int[] array, int x) {
		for (int i = 0; i < array.length; i++) {
			if (array[i] == x) {
				return i;
			}
		}
		return -1;
	}

	public static <F, S> ArrayList<S> mapSeconds(ArrayList<Pair<F, S>> pairs) {
		final ArrayList<S> result = new ArrayList<>(pairs.size());
		for (Pair<F, S> o : pairs) {
			result.add(o.second);
		}
		return result;
	}

	@SafeVarargs
	@NonNull
	public static <T> ArrayList<T> arrayListOf(T... args) {
		final ArrayList<T> list = new ArrayList<>(args.length);
		list.addAll(Arrays.asList(args));
		return list;
	}

	@NonNull
	public static <T> ArrayList<T> empty() {
		return new ArrayList<>(0);
	}
}
