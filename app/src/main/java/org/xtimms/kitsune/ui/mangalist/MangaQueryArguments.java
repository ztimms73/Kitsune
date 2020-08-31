package org.xtimms.kitsune.ui.mangalist;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.xtimms.kitsune.core.models.MangaGenre;
import org.xtimms.kitsune.core.models.MangaType;

import java.util.Objects;

public final class MangaQueryArguments {

	@Nullable
	public String query;
	public int page;
	public int sort;
	public int additionalSort;
	@NonNull
	public MangaGenre[] genres;
	@NonNull
	public MangaType[] types;

	public MangaQueryArguments() {
		genres = new MangaGenre[0];
		types = new MangaType[0];
		query = null;
		page = 0;
	}

	@NonNull
	public Bundle toBundle() {
		Bundle bundle = new Bundle(4);
		bundle.putString("query", query);
		bundle.putInt("page", page);
		bundle.putInt("sort", sort);
		bundle.putInt("additionalSort", additionalSort);
		bundle.putParcelableArray("genres", genres);
		bundle.putParcelableArray("types", types);
		return bundle;
	}

	@NonNull
	public static MangaQueryArguments from(Bundle bundle) {
		MangaQueryArguments args = new MangaQueryArguments();
		args.query = bundle.getString("query");
		args.page = bundle.getInt("page");
		args.sort = bundle.getInt("sort");
		args.additionalSort = bundle.getInt("additionalSort");
		//noinspection ConstantConditions
		args.genres = (MangaGenre[]) bundle.getParcelableArray("genres");
		args.types = (MangaType[]) Objects.requireNonNull(bundle.getParcelableArray("types"));
		return args;
	}

	@NonNull
	public String[] genresValues() {
		final String[] values = new String[genres.length];
		for (int i = 0; i < genres.length; i++) {
			values[i] = genres[i].value;
		}
		return values;
	}

	@NonNull
	public String[] typesValues() {
		final String[] values = new String[types.length];
		for (int i = 0; i < types.length; i++) {
			values[i] = types[i].value;
		}
		return values;
	}

}
