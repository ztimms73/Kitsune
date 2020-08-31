package org.xtimms.kitsune.core.storage.db;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.xtimms.kitsune.core.models.MangaChapter;

public final class SavedPagesSpecification implements SqlSpecification {

	@Nullable
	private final Long mChapterId;

	public SavedPagesSpecification(@Nullable MangaChapter chapter) {
		mChapterId = chapter == null ? null : chapter.id;
	}

	@Nullable
	@Override
	public String getSelection() {
		return mChapterId == null ? null : "chapter_id = ?";
	}

	@Nullable
	@Override
	public String[] getSelectionArgs() {
		return mChapterId == null ? null : new String[]{String.valueOf(mChapterId)};
	}

	@NonNull
	@Override
	public String getOrderBy() {
		return "number";
	}

	@Nullable
	@Override
	public String getLimit() {
		return null;
	}
}
