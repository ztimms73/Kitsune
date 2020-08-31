package org.xtimms.kitsune.core.storage.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xtimms.kitsune.core.helpers.SyncHelper;
import org.xtimms.kitsune.core.models.MangaChapter;
import org.xtimms.kitsune.core.models.MangaDetails;
import org.xtimms.kitsune.core.models.MangaHeader;
import org.xtimms.kitsune.core.models.MangaHistory;
import org.xtimms.kitsune.core.models.MangaPage;
import org.xtimms.kitsune.core.services.SyncService;

import java.lang.ref.WeakReference;

public class HistoryRepository extends SQLiteRepository<MangaHistory> {

	private final Context mContext;

	private static final String TABLE_NAME = "history";
	private static final String[] PROJECTION = new String[] {
				"id",						//0
				"name",						//1
				"summary",					//2
				"genres",					//3
				"url",						//4
				"thumbnail",				//5
				"provider",					//6
				"status",					//7
				"rating",					//8
				"chapter_id",				//9
				"page_id",					//10
				"updated_at",				//11
				"reader_preset",			//12
				"total_chapters",			//13
				"removed"					//14
	};

	@Nullable
	private static WeakReference<HistoryRepository> sInstanceRef = null;

	@NonNull
	public static HistoryRepository get(Context context) {
		HistoryRepository instance = null;
		if (sInstanceRef != null) {
			instance = sInstanceRef.get();
		}
		if (instance == null) {
			instance = new HistoryRepository(context);
			sInstanceRef = new WeakReference<>(instance);
		}
		return instance;
	}

	private HistoryRepository(Context context) {
		super(context);
		mContext = context;
	}

	@Override
	protected void toContentValues(@NonNull MangaHistory mangaHistory, @NonNull ContentValues cv) {
		cv.put(PROJECTION[0], mangaHistory.id);
		cv.put(PROJECTION[1], mangaHistory.name);
		cv.put(PROJECTION[2], mangaHistory.summary);
		cv.put(PROJECTION[3], mangaHistory.genres);
		cv.put(PROJECTION[4], mangaHistory.url);
		cv.put(PROJECTION[5], mangaHistory.thumbnail);
		cv.put(PROJECTION[6], mangaHistory.provider);
		cv.put(PROJECTION[7], mangaHistory.status);
		cv.put(PROJECTION[8], mangaHistory.rating);
		cv.put(PROJECTION[9], mangaHistory.chapterId);
		cv.put(PROJECTION[10], mangaHistory.pageId);
		cv.put(PROJECTION[11], mangaHistory.updatedAt);
		cv.put(PROJECTION[12], mangaHistory.readerPreset);
		cv.put(PROJECTION[13], mangaHistory.totalChapters);
		//cv.put(PROJECTION[14], 0);
	}

	@NonNull
	@Override
	protected String getTableName() {
		return TABLE_NAME;
	}

	@NonNull
	@Override
	protected Object getId(@NonNull MangaHistory history) {
		return history.id;
	}

	@NonNull
	@Override
	protected String[] getProjection() {
		return PROJECTION;
	}

	@NonNull
	@Override
	protected MangaHistory fromCursor(@NonNull Cursor cursor) {
		return new MangaHistory(
				cursor.getLong(0),
				cursor.getString(1),
				cursor.getString(2),
				cursor.getString(3),
				cursor.getString(4),
				cursor.getString(5),
				cursor.getString(6),
				cursor.getInt(7),
				cursor.getShort(8),
				cursor.getLong(9),
				cursor.getLong(10),
				cursor.getLong(11),
				cursor.getShort(12),
				cursor.getInt(13)
		);
	}

	@Nullable
	public MangaHistory find(MangaHeader mangaHeader) {
		try (Cursor cursor = mStorageHelper.getReadableDatabase().query(
				TABLE_NAME,
				PROJECTION,
				"id = ?",
				new String[]{String.valueOf(mangaHeader.id)},
				null,
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

	public boolean quickUpdate(MangaHeader manga, MangaChapter chapter, MangaPage page) {
		try {
			final ContentValues cv = new ContentValues();
			cv.put(PROJECTION[9], chapter.id);
			cv.put(PROJECTION[10], page.id);
			cv.put(PROJECTION[11], System.currentTimeMillis());
			return mStorageHelper.getWritableDatabase()
					.update(TABLE_NAME, cv,
							"id=?", new String[]{String.valueOf(manga.id)}) > 0;
		} catch (Exception e) {
			return false;
		}
	}

	public short getPreset(MangaDetails manga, short defaultValue) {
		try (Cursor cursor = mStorageHelper.getReadableDatabase().query(
				TABLE_NAME,
				new String[]{PROJECTION[12]},
				"id = ?",
				new String[]{String.valueOf(manga.id)},
				null,
				null,
				null,
				null
		)) {
			if (cursor.moveToFirst()) {
				return cursor.getShort(0);
			}
			return defaultValue;
		} catch (Exception e) {
			return defaultValue;
		}
	}

	@Nullable
	public JSONArray dumps(long laterThen) {
		Cursor cursor = null;
		try {
			JSONArray dump = new JSONArray();
			cursor = mStorageHelper.getReadableDatabase().query(TABLE_NAME, new String[]{
					"id", "name", "summary", "genres", "url", "thumbnail", "provider", "status", "chapter_id", "pageId", "readerPreset", "totalChapters", "rating"
			}, "timestamp > ?", new String[]{String.valueOf(laterThen)}, null, null, null);
			if (cursor.moveToFirst()) {
				do {
					JSONObject jobj = new JSONObject();
					JSONObject manga = new JSONObject();
					manga.put("id", cursor.getInt(0));
					manga.put("name", cursor.getString(1));
					manga.put("summary", cursor.getString(2));
					manga.put("genres", cursor.getString(3));
					manga.put("url", cursor.getString(4));
					manga.put("thumbnail", cursor.getString(5));
					manga.put("provider", cursor.getInt(6));
					manga.put("status", cursor.getInt(7));
					manga.put("rating", cursor.getInt(13));
					jobj.put("manga", manga);
					jobj.put("chapter_id", cursor.getLong(8));
					jobj.put("pageId", cursor.getInt(9));
					jobj.put("updatedAt", cursor.getInt(10));
					jobj.put("readerPreset", cursor.getInt(11));
					jobj.put("totalChapters", cursor.getInt(12));
					dump.put(jobj);
				} while (cursor.moveToNext());
			}
			return dump;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	public boolean inject(JSONArray jsonArray) {
		SQLiteDatabase database = mStorageHelper.getWritableDatabase();
		try {
			int len = jsonArray.length();
			database.beginTransaction();
			for (int i=0;i<len;i++) {
				JSONObject jobj = jsonArray.getJSONObject(i);
				JSONObject manga = jobj.getJSONObject("manga");
				ContentValues cv = new ContentValues();
				int id = manga.getInt("id");
				cv.put("id", id);
				cv.put("name", manga.getString("name"));
				cv.put("summary", manga.getString("summary"));
				cv.put("genres", manga.getString("genres"));
				cv.put("url", manga.getString("url"));
				cv.put("thumbnail", manga.getString("thumbnail"));
				cv.put("provider", manga.getLong("provider"));
				cv.put("status", manga.getLong("status"));
				cv.put("rating", manga.getInt("rating"));
				cv.put("chapter_id", jobj.getInt("chapter_id"));
				cv.put("pageId", jobj.getInt("pageId"));
				cv.put("updatedAt", jobj.getInt("updatedAt"));
				cv.put("readerPreset", jobj.getInt("readerPreset"));
				cv.put("totalChapters", jobj.getInt("totalChapters"));
				if (database.update(TABLE_NAME, cv, "id=?", new String[]{String.valueOf(id)})<= 0) {
					database.insertOrThrow(TABLE_NAME, null, cv);
				}
			}
			database.setTransactionSuccessful();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			database.endTransaction();
		}
	}

	@Override
	public void remove(long[] ids) {
		final SQLiteDatabase database = mStorageHelper.getWritableDatabase();
		database.beginTransaction();
		SyncHelper syncHelper = SyncHelper.get(mContext);
		boolean syncEnabled = syncHelper.isHistorySyncEnabled();
		for (long o : ids) {
			database.delete(TABLE_NAME, "id=?", new String[]{String.valueOf(o)});
			database.delete("bookmarks", "manga_id=?", new String[]{String.valueOf(o)});
			if (syncEnabled) {
				syncHelper.setDeleted(database, TABLE_NAME, o);
			}
		}
		database.setTransactionSuccessful();
		database.endTransaction();
		if (syncEnabled) {
			SyncService.syncDelayed(mContext);
		}
    }
}
