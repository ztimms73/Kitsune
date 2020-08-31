package org.xtimms.kitsune.core.helpers;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.text.format.DateUtils;

import androidx.annotation.Nullable;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class AppHelper {

    @Nullable
    public static File getFileFromUri(Context context, Uri uri) {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {"_data"};
            Cursor cursor = null;
            String res = null;
            try {
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
                assert cursor != null;
                int columnIndex = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    res = cursor.getString(columnIndex);
                }
            } catch (Exception e) {
                // Eat it
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
            if (res != null) {
                return new File(res);
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return new File(Objects.requireNonNull(uri.getPath()));
        }
        return null;
    }

    public static String getReadableDateTime(long milliseconds) {
        DateFormat formatter = new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliseconds);
        return formatter.format(calendar.getTime());
    }

    public static String getReadableDateTimeForChapters(long milliseconds) {
        DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliseconds);
        return formatter.format(calendar.getTime());
    }

    public static String getReadableDateTime(Context context, long milliseconds) {
        try {
            String pattern = ((SimpleDateFormat) android.text.format.DateFormat.getLongDateFormat(context)).toLocalizedPattern();
            pattern += " " + ((SimpleDateFormat) android.text.format.DateFormat.getTimeFormat(context)).toLocalizedPattern();
            DateFormat formatter = new SimpleDateFormat(pattern, Locale.getDefault());
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(milliseconds);
            return formatter.format(calendar.getTime());
        } catch (Exception e) {
            e.printStackTrace();
            return getReadableDateTime(milliseconds);
        }
    }

    public static String getDeviceSummary() {
        return Build.MANUFACTURER +
                ' ' +
                Build.MODEL +
                " (Android " +
                Build.VERSION.RELEASE +
                ")";
    }

    public static String getReadableDateTimeRelative(long milliseconds) {
        return DateUtils.getRelativeTimeSpanString(milliseconds, System.currentTimeMillis(), 0L,
                DateUtils.FORMAT_ABBREV_ALL).toString();
    }

}
