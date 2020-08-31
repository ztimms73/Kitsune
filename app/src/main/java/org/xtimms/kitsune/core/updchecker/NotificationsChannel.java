package org.xtimms.kitsune.core.updchecker;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import org.xtimms.kitsune.R;
import org.xtimms.kitsune.core.models.MangaUpdateInfo;
import org.xtimms.kitsune.ui.mangalist.updates.MangaUpdatesActivity;
import org.xtimms.kitsune.utils.ThemeUtils;

import java.util.List;

class NotificationsChannel {

	private static final String CHANNEL_NAME = "manga.updates";
	private static final String UPDATING_NAME = "manga.updating";

	private final NotificationManager mManager;
	private final Context mContext;

	NotificationsChannel(Context context) {
		mContext = context;
		mManager = (NotificationManager) context.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
	}

	void showUpdatesNotification(List<MangaUpdateInfo> updates) {
		final NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, CHANNEL_NAME);
		final NotificationCompat.InboxStyle style = new NotificationCompat.InboxStyle();
		int totalCount = 0;
		for (MangaUpdateInfo o : updates) {
			totalCount += o.newChapters;
			style.addLine(o.newChapters + " - " + o.mangaName);
		}
		final String summary = mContext.getResources().getQuantityString(R.plurals.chapters_new, totalCount, totalCount);
		style.setSummaryText(summary);
		builder.setContentTitle(mContext.getString(R.string.new_chapters_available));
		builder.setContentText(summary);
		builder.setTicker(summary);
		builder.setSmallIcon(R.drawable.ic_notification);
		builder.setStyle(style);
		final int color = ContextCompat.getColor(mContext, R.color.notification_chapters);
		//TODO settings
		builder.setLights(color, 800, 4000);
		builder.setContentIntent(PendingIntent.getActivity(
				mContext,
				1,
				new Intent(mContext, MangaUpdatesActivity.class),
				0
		));
		builder.setAutoCancel(true);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			createChannel();
		}
		mManager.notify(CHANNEL_NAME.hashCode(), builder.build());
	}

	void showProgressNotification() {
		BitmapFactory.Options options = new BitmapFactory.Options();
		Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_launcher, options);
		NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, UPDATING_NAME)
				.setLargeIcon(bitmap)
				.setSmallIcon(R.drawable.ic_update)
				.setContentTitle(mContext.getString(R.string.checking_new_chapters))
				.setProgress(100, 1, true)
				.setOngoing(true)
				.setColor(ThemeUtils.getThemeAttrColor(mContext, R.attr.colorAccent));
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			final NotificationChannel channel = new NotificationChannel(
					UPDATING_NAME,
					mContext.getString(R.string.checking_new_chapters),
					NotificationManager.IMPORTANCE_LOW
			);
			channel.setLightColor(mContext.getColor(R.color.notification_chapters));
			channel.enableLights(true);
			mManager.createNotificationChannel(channel);
		}
		mManager.notify(UPDATING_NAME.hashCode(), builder.build());
	}

	void showNoUpdatesNotification() {
		BitmapFactory.Options options = new BitmapFactory.Options();
		Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_launcher, options);
		NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, UPDATING_NAME)
				.setLargeIcon(bitmap)
				.setSmallIcon(R.drawable.ic_update)
				.setContentTitle("No new manga")
				.setColor(ThemeUtils.getThemeAttrColor(mContext, R.attr.colorAccent));
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			final NotificationChannel channel = new NotificationChannel(
					UPDATING_NAME,
					mContext.getString(R.string.checking_new_chapters),
					NotificationManager.IMPORTANCE_LOW
			);
			channel.setLightColor(mContext.getColor(R.color.notification_chapters));
			channel.enableLights(true);
			mManager.createNotificationChannel(channel);
		}
		mManager.notify(UPDATING_NAME.hashCode(), builder.build());
	}

	void cancelProgressNotification() {
		mManager.cancel(UPDATING_NAME.hashCode());
	}

	@RequiresApi(api = Build.VERSION_CODES.O)
	private void createChannel() {
		final NotificationChannel channel = new NotificationChannel(
				CHANNEL_NAME,
				mContext.getString(R.string.checking_new_chapters),
				NotificationManager.IMPORTANCE_LOW
		);
		channel.setLightColor(mContext.getColor(R.color.notification_chapters));
		channel.enableLights(true);
		mManager.createNotificationChannel(channel);
	}
}
