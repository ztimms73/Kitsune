package org.xtimms.kitsune.core.storage.db;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xtimms.kitsune.R;
import org.xtimms.kitsune.utils.FileLogger;
import org.xtimms.kitsune.utils.ResourceUtils;
import org.xtimms.kitsune.utils.TextUtils;

import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArraySet;

@SuppressWarnings("deprecation")
public class StorageHelper extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 27;
	private static final String DATABASE_NAME = "storage";

	private final Resources mResources;

	public StorageHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		mResources = context.getResources();
	}

	@Override
	public void onCreate(SQLiteDatabase sqLiteDatabase) {
		String[] parts = ResourceUtils.getRawString(mResources, R.raw.storage).split(";");
		sqLiteDatabase.beginTransaction();
		try {
			for (String query : parts) {
				sqLiteDatabase.execSQL(TextUtils.inline(query));
			}
			sqLiteDatabase.setTransactionSuccessful();
		/*} catch (Exception e) { //TODO handle it
			e.printStackTrace();*/
		} finally {
			sqLiteDatabase.endTransaction();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase sqLiteDatabase, int old, int newest) {
		try {
			final CopyOnWriteArraySet<String> tables = getTableNames(sqLiteDatabase);
			/*if (!tables.contains("search_history")) {
				sqLiteDatabase.execSQL("CREATE TABLE search_history ("
						+ "_id INTEGER PRIMARY KEY,"
						+ "query TEXT"
						+ ");");
				sqLiteDatabase.setTransactionSuccessful();
			}*/
			if (tables.contains("search_history")) {
				sqLiteDatabase.execSQL("DROP TABLE search_history");
				sqLiteDatabase.setTransactionSuccessful();
			}
			CopyOnWriteArraySet<String> columnsSavedChapters = getColumsNames(sqLiteDatabase, "saved_chapters");
			if(!columnsSavedChapters.contains("scanlator")) {
				sqLiteDatabase.execSQL("ALTER TABLE saved_chapters ADD COLUMN scanlator TEXT NOT NULL");
				sqLiteDatabase.setTransactionSuccessful();
			}
		} finally {
			if (sqLiteDatabase != null) {
				sqLiteDatabase.endTransaction();
			}
		}
	}

	@Override
	protected void finalize() throws Throwable {
		close();
		super.finalize();
	}

	@Nullable
	public JSONArray extractTableData(String tableName, @Nullable String where) {
		JSONArray jsonArray;
		Cursor cursor = null;
		try {
			jsonArray = new JSONArray();
			JSONObject jsonObject;
			cursor = getReadableDatabase().query(tableName, null, where, null, null, null, null, null);
			String[] columns = cursor.getColumnNames();
			if (cursor.moveToFirst()) {
				do {
					jsonObject = new JSONObject();
					for (int i = 0; i < columns.length; i++) {
						switch (cursor.getType(i)) {
							case Cursor.FIELD_TYPE_INTEGER:
								jsonObject.put(columns[i], cursor.getInt(i));
								break;
							case Cursor.FIELD_TYPE_STRING:
								jsonObject.put(columns[i], cursor.getString(i));
								break;
							case Cursor.FIELD_TYPE_FLOAT:
								jsonObject.put(columns[i], cursor.getFloat(i));
								break;
							case Cursor.FIELD_TYPE_BLOB:
								jsonObject.put(columns[i], cursor.getBlob(i));
								break;
						}
					}
					jsonArray.put(jsonObject);
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			jsonArray = null;
			e.printStackTrace();
			FileLogger.getInstance().report(e);
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return jsonArray;
	}

	public boolean insertTableData(String tableName, JSONArray data) {
		SQLiteDatabase database = null;
		boolean success = true;
		try {
			database = getWritableDatabase();
			database.beginTransaction();
			JSONObject o;
			Object value;
			ContentValues cv;
			String id;
			for (int i = 0; i < data.length(); i++) {
				o = data.getJSONObject(i);
				id = null;
				cv = new ContentValues();
				Iterator<String> iter = o.keys();
				while (iter.hasNext()) {
					String key = iter.next();
					try {
						value = o.get(key);
						if (value instanceof Integer) {
							cv.put(key, (int) value);
						} else if (value instanceof String) {
							cv.put(key, (String) value);
						} else if (value instanceof Float) {
							cv.put(key, (Float) value);
						}
						if ("id".equals(key)) {
							id = String.valueOf(value);
						}
					} catch (JSONException ignored) {
					}
				}
				if (id != null && (database.update(tableName, cv, "id=?", new String[]{id}) == 0)) {
					database.insertOrThrow(tableName, null, cv);
				}
			}
			database.setTransactionSuccessful();
		} catch (Exception e) {
			success = false;
			e.printStackTrace();
			FileLogger.getInstance().report("STORAGE", e);
		} finally {
			if (database != null) {
				database.endTransaction();
			}
		}
		return !success;
	}

	public static CopyOnWriteArraySet<String> getColumsNames(SQLiteDatabase db, String table) {
		CopyOnWriteArraySet<String> names = new CopyOnWriteArraySet<>();
		Cursor ti = db.rawQuery("PRAGMA table_info(" + table + ")", null);
		if (ti.moveToFirst()) {
			do {
				names.add(ti.getString(1));
			} while (ti.moveToNext());
		}
		ti.close();
		return names;
	}

	public static CopyOnWriteArraySet<String> getTableNames(SQLiteDatabase db) {
		CopyOnWriteArraySet<String> result = new CopyOnWriteArraySet<>();
		try {
			String s = "SELECT name FROM sqlite_master " +
					"WHERE type IN ('table','view') AND name NOT LIKE 'sqlite_%' " +
					"UNION ALL " +
					"SELECT name FROM sqlite_temp_master " +
					"WHERE type IN ('table','view') " +
					"ORDER BY 1";

			Cursor c = db.rawQuery(s, null);
			c.moveToFirst();

			while (c.moveToNext()) {
				result.add(c.getString(c.getColumnIndex("name")));
			}
			c.close();
		} catch (SQLiteException e) {
			FileLogger.getInstance().report(e);
		}
		return result;
	}

	public static int getRowCount(SQLiteDatabase database, String table, @Nullable String where) {
		Cursor cursor = database.rawQuery("select count(*) from "
				+ table
				+ (where == null ? "" : " where " + where), null);
		cursor.moveToFirst();
		int count = cursor.getInt(0);
		cursor.close();
		return count;
	}

}
