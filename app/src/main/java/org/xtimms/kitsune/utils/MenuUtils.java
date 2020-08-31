package org.xtimms.kitsune.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import androidx.annotation.IdRes;

import android.view.MenuItem;
import android.view.SubMenu;

import org.xtimms.kitsune.core.models.MangaHeader;

import java.util.List;

public abstract class MenuUtils {

	public static void buildOpenWithSubmenu(Context context,  MangaHeader mangaHeader, MenuItem menuItem) {
		final Uri uri = Uri.parse(mangaHeader.url);
		final Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		buildIntentSubmenu(context, intent, menuItem);
	}

	private static void buildIntentSubmenu(Context context, Intent intent, MenuItem menuItem) {
		final SubMenu menu = menuItem.getSubMenu();
		final PackageManager pm = context.getPackageManager();
		final List<ResolveInfo> allActivities = pm.queryIntentActivities(intent, 0);
		if (allActivities.isEmpty()) {
			menuItem.setVisible(false);
		} else {
			menuItem.setVisible(true);
			menu.clear();
			for (ResolveInfo o : allActivities) {
				MenuItem item = menu.add(o.loadLabel(pm));
				item.setIcon(o.loadIcon(pm));
				ComponentName name = new ComponentName(o.activityInfo.applicationInfo.packageName,
						o.activityInfo.name);
				Intent i = new Intent(intent);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
						Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
				i.setComponent(name);
				item.setIntent(i);
			}
		}
	}

	public static void setRadioCheckable(MenuItem menuItem, @IdRes int group_id) {
		final SubMenu menu = menuItem.getSubMenu();
		if (menu != null) {
			menu.setGroupCheckable(group_id, true, true);
		}
	}
}
