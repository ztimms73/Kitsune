package org.xtimms.kitsune.core.storage.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

abstract class SQLiteRepository<T> implements Repository<T> {

	protected final StorageHelper mStorageHelper;

	protected SQLiteRepository(Context context) {
		mStorageHelper = new StorageHelper(context);
	}

	@Override
	public boolean add(@NonNull T t) {
		try {
			final ContentValues cv = new ContentValues(getProjection().length);
			toContentValues(t, cv);
			return mStorageHelper.getWritableDatabase()
					.insert(getTableName(), null, cv) >= 0;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public boolean remove(@NonNull T t) {
		return mStorageHelper.getWritableDatabase()
				.delete(getTableName(), "id=?", new String[]{String.valueOf(getId(t))}) >= 0;
	}

	public void remove(long[] ids) {
    }

	@Override
	public boolean update(@NonNull T t) {
		try {
			final ContentValues cv = new ContentValues(getProjection().length);
			toContentValues(t, cv);
			return mStorageHelper.getWritableDatabase().update(getTableName(), cv,
					"id=?", new String[]{String.valueOf(getId(t))}) > 0;
		} catch (Exception e) {
			return false;
		}
	}

	public void addOrUpdate(@NonNull T t) {
		final ContentValues cv = new ContentValues(getProjection().length);
		toContentValues(t, cv);
		final SQLiteDatabase database = mStorageHelper.getWritableDatabase();
		try {
			if(database.insert(getTableName(), null, cv) >= 0) {
				return;
			}
		} catch (Exception ignored) {
		}
		try {
			if(database.update(getTableName(), cv,"id=?", new String[]{String.valueOf(getId(t))}) > 0) {
            }
		} catch (Exception ignored) {
		}
    }

	public void updateOrAdd(@NonNull T t) {
		final ContentValues cv = new ContentValues(getProjection().length);
		toContentValues(t, cv);
		final SQLiteDatabase database = mStorageHelper.getWritableDatabase();
		try {
			if(database.update(getTableName(), cv,"id=?", new String[]{String.valueOf(getId(t))}) > 0) {
				return;
			}
		} catch (Exception ignored) {
		}
		try {
			if(database.insert(getTableName(), null, cv) >= 0) {
            }
		} catch (Exception ignored) {
		}
    }

	@Override
	public void clear() {
		mStorageHelper.getWritableDatabase().delete(getTableName(), null, null);
	}

	@Override
	public boolean contains(@NonNull T t) {
		try (Cursor cursor = mStorageHelper.getReadableDatabase().rawQuery("SELECT * FROM " + getTableName() + " WHERE id = ?", new String[]{String.valueOf(getId(t))})) {
			return cursor.getCount() > 0;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Nullable
	@Override
	public ArrayList<T> query(@NonNull SqlSpecification specification) {
		try (Cursor cursor = mStorageHelper.getReadableDatabase().query(
				getTableName(),
				getProjection(),
				specification.getSelection(),
				specification.getSelectionArgs(),
				null,
				null,
				specification.getOrderBy(),
				specification.getLimit()
		)) {
			ArrayList<T> list = new ArrayList<>();
			if (cursor.moveToFirst()) {
				do {
					list.add(fromCursor(cursor));
				} while (cursor.moveToNext());
			}
			return list;
		} catch (Exception e) {
			return null;
		}
	}

	@Nullable
	protected T findById(@NonNull Object id) {
		try (Cursor cursor = mStorageHelper.getReadableDatabase().query(
				getTableName(),
				getProjection(),
				"id = ?",
				new String[]{String.valueOf(id)},
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

	protected abstract void toContentValues(@NonNull T t, @NonNull ContentValues cv);

	@NonNull
	protected abstract String getTableName();

	@NonNull
	protected abstract Object getId(@NonNull T t);

	@NonNull
	protected abstract String[] getProjection();

	@NonNull
	protected abstract T fromCursor(@NonNull Cursor cursor);
}
