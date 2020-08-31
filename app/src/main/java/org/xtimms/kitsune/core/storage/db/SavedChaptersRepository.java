package org.xtimms.kitsune.core.storage.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.xtimms.kitsune.core.models.MangaHeader;
import org.xtimms.kitsune.core.models.SavedChapter;

import java.lang.ref.WeakReference;

public final class SavedChaptersRepository extends SQLiteRepository<SavedChapter> {

	private static final String TABLE_NAME = "saved_chapters";
	private static final String[] PROJECTION = new String[]{
			"id",
			"manga_id",			//0
			"name",				//1
			"number",			//2
			"url",				//3
			"provider",			//4
			"scanlator",		//5
			"upload_date"		//6
	};

	@Nullable
	private static WeakReference<SavedChaptersRepository> sInstanceRef = null;

	@NonNull
	public static synchronized SavedChaptersRepository get(Context context) {
		SavedChaptersRepository instance = null;
		if (sInstanceRef != null) {
			instance = sInstanceRef.get();
		}
		if (instance == null) {
			instance = new SavedChaptersRepository(context);
			sInstanceRef = new WeakReference<>(instance);
		}
		return instance;
	}

	private SavedChaptersRepository(Context context) {
		super(context);
	}

	@Override
	protected void toContentValues(@NonNull SavedChapter chapter, @NonNull ContentValues cv) {
		cv.put(PROJECTION[0], chapter.id);
		cv.put(PROJECTION[1], chapter.mangaId);
		cv.put(PROJECTION[2], chapter.name);
		cv.put(PROJECTION[3], chapter.number);
		cv.put(PROJECTION[4], chapter.url);
		cv.put(PROJECTION[5], chapter.provider);
		cv.put(PROJECTION[6], chapter.scanlator);
		cv.put(PROJECTION[7], chapter.date);
	}

	@NonNull
	@Override
	protected SavedChapter fromCursor(@NonNull Cursor cursor) {
		return new SavedChapter(
				cursor.getLong(0),
				cursor.getLong(1),
				cursor.getString(2),
				cursor.getInt(3),
				cursor.getString(4),
				cursor.getString(5),
				cursor.getString(6),
				cursor.getLong(7)
		);
	}

	@NonNull
	@Override
	protected String getTableName() {
		return TABLE_NAME;
	}

	@NonNull
	@Override
	protected Object getId(@NonNull SavedChapter chapter) {
		return chapter.id;
	}

	@NonNull
	@Override
	protected String[] getProjection() {
		return PROJECTION;
	}

	public int count(MangaHeader manga) {
        try (Cursor cursor = mStorageHelper.getReadableDatabase()
                .rawQuery("SELECT COUNT(*) FROM " + TABLE_NAME + " WHERE manga_id = ?",
                        new String[]{String.valueOf(manga.id)})) {
            return cursor.moveToFirst() ? cursor.getInt(0) : -1;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
	}

	@Nullable
	public SavedChapter findChapterByUrl(String chapterUrl) {
        try (Cursor cursor = mStorageHelper.getReadableDatabase().query(
                getTableName(),
                getProjection(),
                "url = ?",
                new String[]{String.valueOf(chapterUrl)},
                null,
                null,
                null
        )) {
            if (cursor.moveToFirst()) {
                return fromCursor(cursor);
            }
            return null;
        } catch (Exception e) {
            return null;
        }
	}
}
