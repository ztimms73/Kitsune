package org.xtimms.kitsune.core.common;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Icon;
import android.os.Build;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.graphics.drawable.IconCompat;

import org.xtimms.kitsune.R;
import org.xtimms.kitsune.utils.ImageUtils;
import org.xtimms.kitsune.utils.TextUtils;
import org.xtimms.kitsune.core.models.MangaHistory;
import org.xtimms.kitsune.core.storage.db.HistoryRepository;
import org.xtimms.kitsune.core.storage.db.HistorySpecification;
import org.xtimms.kitsune.ui.reader.ReaderActivity;

import java.util.ArrayList;

public final class AppShortcutHelper {

	private final Context mContext;

	public AppShortcutHelper(Context context) {
		mContext = context;
	}

	public void update(@Nullable HistoryRepository historyRepository) {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N_MR1) {
			return;
		}
		final HistoryRepository repository = historyRepository != null ? historyRepository : HistoryRepository.get(mContext);
		final ArrayList<MangaHistory> list = repository.query(
				new HistorySpecification()
						.orderByDate(true)
						.limit(5));
		updateImpl(list);
	}

	@SuppressWarnings("deprecation")
	@RequiresApi(api = Build.VERSION_CODES.N_MR1)
	private void updateImpl(ArrayList<MangaHistory> history) {
		final ShortcutManager shortcutManager = mContext.getSystemService(ShortcutManager.class);
		if (shortcutManager == null) {
			return;
		}
		final ArrayList<ShortcutInfo> shortcuts = new ArrayList<>(5);
		final ComponentName activity = new ComponentName(mContext, ReaderActivity.class);
		for (MangaHistory o : history) {
			final ShortcutInfo.Builder builder = new ShortcutInfo.Builder(mContext, String.valueOf(o.id));
			assert o.name != null;
			builder.setShortLabel(TextUtils.ellipsize(o.name, 16));
			builder.setLongLabel(o.name);
			//builder.setActivity(activity);
			final Bitmap bitmap = ImageUtils.getCachedImage(o.thumbnail);
			if (bitmap != null) {
				builder.setIcon(IconCompat.createWithAdaptiveBitmap(bitmap).toIcon());
			} else {
				builder.setIcon(Icon.createWithResource(mContext, R.drawable.image_album));
			}
			final Intent intent = new Intent(ReaderActivity.ACTION_READING_CONTINUE);
			intent.putExtras(o.toBundle());
			intent.setComponent(activity);
			builder.setIntent(intent);
			shortcuts.add(builder.build());
		}
		shortcutManager.setDynamicShortcuts(shortcuts);
	}
}
