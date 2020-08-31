package org.xtimms.kitsune.core.updchecker;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.IBinder;

import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.xtimms.kitsune.core.common.OemBadgeHelper;
import org.xtimms.kitsune.core.common.WeakAsyncTask;
import org.xtimms.kitsune.utils.network.NetworkUtils;

@SuppressWarnings("ALL")
public final class UpdatesCheckService extends Service {

	private static final String ACTION_CHECK_FORCE = "org.xtimms.kitsune.ACTION_CHECK_FORCE";

	private BackgroundTask mTask;

	@Override
	public void onCreate() {
		super.onCreate();
		mTask = new BackgroundTask(this);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (ACTION_CHECK_FORCE.equals(intent.getAction())) {
			mTask = new BackgroundTask(this);
			mTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			return START_STICKY;
		}
		final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		final boolean enabled = prefs.getBoolean("mangaupdates.enabled", false);
		final boolean allowMetered = "0".equals(prefs.getString("mangaupdates.networktype", "0"));
		if (!enabled && !NetworkUtils.isNetworkAvailable(this, allowMetered)) {
			stopSelf();
			return START_NOT_STICKY;
		} else {
			mTask = new BackgroundTask(this);
			mTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			return START_STICKY;
		}
	}

	@Override
	public void onDestroy() {
		if (mTask != null && mTask.canCancel()) {
			mTask.cancel(false);
		}
		super.onDestroy();
	}

	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private static class BackgroundTask extends WeakAsyncTask<UpdatesCheckService, Void, Void, UpdatesCheckResult> {

		BackgroundTask(UpdatesCheckService updatesCheckService) {
			super(updatesCheckService);
		}

		@Override
		protected UpdatesCheckResult doInBackground(Void... voids) {
			return new MangaUpdatesChecker(getObject()).fetchUpdates();
		}

		//TODO зависает у некоторых людей
		/*@Override
		protected void onPreExecute(@NonNull UpdatesCheckService updatesCheckService) {
			super.onPreExecute(updatesCheckService);
			final NotificationsChannel notificationHelper = new NotificationsChannel(updatesCheckService);
			notificationHelper.showProgressNotification();
		}*/

		@Override
		protected void onPostExecute(@NonNull UpdatesCheckService service, UpdatesCheckResult result) {
			if (result.isSuccess()) {
				MangaUpdatesChecker.onCheckSuccess(service);
				final NotificationsChannel channel = new NotificationsChannel(service);
				final int totalCount = result.getNewChaptersCount();
				new OemBadgeHelper(service).applyCount(totalCount);
				channel.cancelProgressNotification();
				if (totalCount > 0) {
					channel.showUpdatesNotification(result.getUpdates());
				}
				/*else
				{
					channel.showNoUpdatesNotification();
				}*/
			}
			service.stopSelf();
		}
	}

	public static void runForce(Context context) {
		final Intent intent = new Intent(context, UpdatesCheckService.class);
		intent.setAction(ACTION_CHECK_FORCE);
		context.startService(intent);
	}
}
