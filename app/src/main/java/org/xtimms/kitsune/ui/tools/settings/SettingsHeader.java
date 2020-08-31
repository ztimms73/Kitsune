package org.xtimms.kitsune.ui.tools.settings;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;

public final class SettingsHeader {

	public final int id;
	public final String title;
	public final String summary;
	public final Drawable icon;

	@StringRes
	public final int actionText;
	@IdRes
	public final int actionId;

	public SettingsHeader(@NonNull Context context, int id, @StringRes int title,@DrawableRes int icon) {
		this(context, id, title, 0, icon, 0, 0);
	}

	public SettingsHeader(@NonNull Context context, int id, @StringRes int title, @StringRes int summary, @DrawableRes int icon, @StringRes int actionText, @IdRes int actionId) {
		this.id = id;
		this.title = context.getString(title);
		this.icon = ContextCompat.getDrawable(context, icon);
		this.summary = summary == 0 ? null : context.getString(summary);
		this.actionText = actionText;
		this.actionId = actionId;
	}

	public boolean hasAction() {
		return actionText != 0 && actionId != 0;
	}
}
