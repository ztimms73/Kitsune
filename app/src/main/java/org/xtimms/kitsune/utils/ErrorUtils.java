package org.xtimms.kitsune.utils;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import org.json.JSONException;
import org.xtimms.kitsune.BuildConfig;
import org.xtimms.kitsune.R;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.SocketTimeoutException;

public abstract class ErrorUtils {

	@StringRes
	public static int getErrorMessage(@Nullable Throwable error) {
		if (error == null) {
			return R.string.error;
		} else if (error instanceof JSONException) {
			return R.string.error_bad_response;
		} else if (error instanceof SocketTimeoutException) {
			return R.string.error_timeout;
		} else if (error instanceof IOException) {
			return R.string.loading_error;
		} else { //TODO
			return R.string.error;
		}
	}

	@NonNull
	public static String getErrorMessage(Context context, @Nullable Throwable throwable) {
		return context.getString(getErrorMessage(throwable));
	}

	@NonNull
	public static String getErrorMessageDetailed(Context context, @Nullable Throwable throwable) {
		String message = getErrorMessage(context, throwable);
		if (BuildConfig.DEBUG && throwable != null) {
			message += ":\n" + throwable.getMessage();
		}
		return message;
	}

	@NonNull
	public static String getStackTrace(@NonNull Throwable throwable) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		throwable.printStackTrace(pw);
		return sw.toString();
	}
}
