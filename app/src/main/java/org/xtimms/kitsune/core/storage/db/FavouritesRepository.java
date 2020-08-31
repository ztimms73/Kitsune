package org.xtimms.kitsune.core.storage.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xtimms.kitsune.core.models.MangaFavourite;
import org.xtimms.kitsune.core.models.MangaHeader;
import org.xtimms.kitsune.core.models.MangaUpdateInfo;

import java.lang.ref.WeakReference;

public final class FavouritesRepository extends SQLiteRepository<MangaFavourite> {

	private static final String TABLE_NAME = "favourites";
	private static final String[] PROJECTION = new String[]{
			"id",					//0
			"name",					//1
			"summary",				//2
			"genres",				//3
			"url",					//4
			"thumbnail",			//5
			"provider",				//6
			"status",				//7
			"rating",				//8
			"created_at",			//9
			"category_id",			//10
			"total_chapters",		//11
			"new_chapters",			//12
			"removed"				//13
	};

	@Nullable
	private static WeakReference<FavouritesRepository> sInstanceRef = null;

	@NonNull
	public static FavouritesRepository get(Context context) {
		FavouritesRepository instance = null;
		if (sInstanceRef != null) {
			instance = sInstanceRef.get();
		}
		if (instance == null) {
			instance = new FavouritesRepository(context);
			sInstanceRef = new WeakReference<>(instance);
		}
		return instance;
	}

	private FavouritesRepository(Context context) {
		super(context);
	}

	@Override
	protected void toContentValues(@NonNull MangaFavourite mangaFavourite, @NonNull ContentValues cv) {
		cv.put(PROJECTION[0], mangaFavourite.id);
		cv.put(PROJECTION[1], mangaFavourite.name);
		cv.put(PROJECTION[2], mangaFavourite.summary);
		cv.put(PROJECTION[3], mangaFavourite.genres);
		cv.put(PROJECTION[4], mangaFavourite.url);
		cv.put(PROJECTION[5], mangaFavourite.thumbnail);
		cv.put(PROJECTION[6], mangaFavourite.provider);
		cv.put(PROJECTION[7], mangaFavourite.status);
		cv.put(PROJECTION[8], mangaFavourite.rating);
		cv.put(PROJECTION[9], mangaFavourite.createdAt);
		cv.put(PROJECTION[10], mangaFavourite.categoryId);
		cv.put(PROJECTION[11], mangaFavourite.totalChapters);
		cv.put(PROJECTION[12], mangaFavourite.newChapters);
		//cv.put(PROJECTION[13], 0);
	}

	@NonNull
	@Override
	protected String getTableName() {
		return TABLE_NAME;
	}

	@NonNull
	@Override
	protected Object getId(@NonNull MangaFavourite mangaFavourite) {
		return mangaFavourite.id;
	}

	@NonNull
	@Override
	protected String[] getProjection() {
		return PROJECTION;
	}

	@NonNull
	@Override
	protected MangaFavourite fromCursor(@NonNull Cursor cursor) {
		return new MangaFavourite(
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
				cursor.getInt(10),
				cursor.getInt(11),
				cursor.getInt(12)
		);
	}

	@Nullable
	public MangaFavourite get(MangaHeader mangaHeader) {
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
			} else {
				return null;
			}
		} catch (Exception e) {
			return null;
		}
	}

	public void remove(MangaHeader manga) {
		mStorageHelper.getWritableDatabase()
				.delete(TABLE_NAME, "id=?", new String[]{String.valueOf(manga.id)});
	}

	public boolean putUpdateInfo(MangaUpdateInfo updateInfo) {
		try {
			final ContentValues cv = new ContentValues(1);
			cv.put("new_chapters", updateInfo.newChapters);
			return mStorageHelper.getWritableDatabase().update(getTableName(), cv,
					"id=?", new String[]{String.valueOf(updateInfo.mangaId)}) > 0;
		} catch (Exception e) {
			return false;
		}
	}

	public void setNoUpdates(MangaHeader manga) {
		final SQLiteDatabase database = this.mStorageHelper.getWritableDatabase();
		try {
			database.beginTransaction();
			database.execSQL("UPDATE favourites SET total_chapters = total_chapters + new_chapters WHERE id = ?", new String[] { String.valueOf(manga.id) });
			database.execSQL("UPDATE favourites SET new_chapters = 0 WHERE id = ?", new String[] { String.valueOf(manga.id) });
			database.setTransactionSuccessful();
		} catch (Exception exception) {
			exception.printStackTrace();
		} finally {
			if (database != null) {
				database.endTransaction();
			}
		}
	}


	public void clearNewChapters() {
		SQLiteDatabase sQLiteDatabase = mStorageHelper.getWritableDatabase();
		try {
			sQLiteDatabase.beginTransaction();
			sQLiteDatabase.execSQL("UPDATE favourites SET total_chapters = total_chapters + new_chapters, new_chapters = 0");
			sQLiteDatabase.setTransactionSuccessful();
		} catch (Exception exception) {
			exception.printStackTrace();
		} finally {
			if (sQLiteDatabase != null) {
				sQLiteDatabase.endTransaction();
			}
		}
	}

	@Nullable
	public JSONArray dumps(long laterThen) {
		Cursor cursor = null;
		try {
			JSONArray dump = new JSONArray();
			cursor = mStorageHelper.getReadableDatabase().query(TABLE_NAME, new String[]{
					"id", "name", "summary", "genres", "url", "thumbnail", "provider", "status", "createdAt", "categoryId", "totalChapters", "newChapters", "rating"
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
					manga.put("rating", cursor.getInt(12));
					jobj.put("manga", manga);
					jobj.put("createdAt", cursor.getLong(8));
					jobj.put("categoryId", cursor.getInt(9));
					jobj.put("totalChapters", cursor.getInt(10));
					jobj.put("newChapters", cursor.getInt(11));
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
				cv.put("createdAt", jobj.getInt("createdAt"));
				cv.put("categoryId", jobj.getInt("categoryId"));
				cv.put("totalChapters", jobj.getInt("totalChapters"));
				cv.put("newChapters", jobj.getInt("newChapters"));
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
}
