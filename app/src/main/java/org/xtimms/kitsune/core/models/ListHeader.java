package org.xtimms.kitsune.core.models;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

public class ListHeader {

	@StringRes
	public final int textResId;
	@Nullable
	public final String text;
	@Nullable
	public final Object extra;

	public ListHeader(@Nullable String text, @Nullable Object extra) {
		this.text = text;
		this.textResId = 0;
		this.extra = extra;
	}

	public ListHeader(int textResId, @Nullable Object extra) {
		this.textResId = textResId;
		this.text = null;
		this.extra = extra;
	}
}
