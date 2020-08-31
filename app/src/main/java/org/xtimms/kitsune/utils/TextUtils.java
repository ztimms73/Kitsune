package org.xtimms.kitsune.utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.Html;
import android.text.Spanned;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public abstract class TextUtils {

	@NonNull
	public static String notNull(@Nullable String str) {
		return str == null ? "" : str;
	}

	public static Spanned fromHtmlCompat(String html) {
		Spanned spanned;
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
			spanned = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
		} else {
			spanned = Html.fromHtml(html);
		}
		return spanned;
	}

	public static String ellipsize(String string, int maxLength) {
		return string.length() <= maxLength ? string : string.substring(0, maxLength - 1) + '…';
	}

	public static String inline(String string) {
		return string.replaceAll("\\s+", " ");
	}

	@NonNull
	public static String formatFileSize(long size) {
		if(size <= 0) return "0 B";
		final String[] units = new String[] { "B", "kB", "MB", "GB", "TB" };
		int digitGroups = (int) (Math.log10(size)/Math.log10(1024));
		final DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();
		symbols.setDecimalSeparator('.');
		symbols.setGroupingSeparator(' ');
		return new DecimalFormat("#,##0.#", symbols).format(size/Math.pow(1024, digitGroups)) + " " + units[digitGroups];
	}
}
