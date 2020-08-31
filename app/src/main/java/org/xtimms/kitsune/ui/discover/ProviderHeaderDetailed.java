package org.xtimms.kitsune.ui.discover;

import android.graphics.drawable.Drawable;

import org.xtimms.kitsune.core.models.ProviderHeader;

public final class ProviderHeaderDetailed extends ProviderHeader {

	public final String summary;
	public final Drawable icon;

	public ProviderHeaderDetailed(String cname, String dname, String desc, String summary, Drawable icon) {
		super(cname, dname, desc);
		this.summary = summary;
		this.icon = icon;
	}
}
