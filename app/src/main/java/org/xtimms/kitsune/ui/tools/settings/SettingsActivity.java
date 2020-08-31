package org.xtimms.kitsune.ui.tools.settings;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.snackbar.Snackbar;

import android.view.View;
import android.widget.Toast;

import org.xtimms.kitsune.core.common.base.AppBaseActivity;
import org.xtimms.kitsune.core.helpers.AppHelper;
import org.xtimms.kitsune.R;
import org.xtimms.kitsune.core.common.dialogs.DirSelectDialog;
import org.xtimms.kitsune.core.common.dialogs.StorageSelectDialog;
import org.xtimms.kitsune.core.helpers.SyncHelper;
import org.xtimms.kitsune.core.services.SyncService;
import org.xtimms.kitsune.core.updchecker.JobSetupReceiver;
import org.xtimms.kitsune.core.updchecker.UpdatesCheckService;
import org.xtimms.kitsune.utils.BackupRestoreUtil;
import org.xtimms.kitsune.utils.FileLogger;
import org.xtimms.kitsune.utils.ProgressAsyncTask;
import org.xtimms.kitsune.utils.TextUtils;
import org.xtimms.kitsune.utils.network.RESTResponse;

import java.io.File;
import java.util.Objects;

@SuppressWarnings("ALL")
public class SettingsActivity extends AppBaseActivity implements SharedPreferences.OnSharedPreferenceChangeListener,
		Preference.OnPreferenceClickListener {

	public static final String ACTION_SETTINGS_GENERAL = "org.xtimms.kitsune.ACTION_SETTINGS_GENERAL";
	public static final String ACTION_SETTINGS_READER = "org.xtimms.kitsune.ACTION_SETTINGS_READER";
	public static final String ACTION_SETTINGS_SHELF = "org.xtimms.kitsune.ACTION_SETTINGS_SHELF";
	public static final String ACTION_SETTINGS_BACKUP = "org.xtimms.kitsune.ACTION_SETTINGS_BACKUP";
	public static final String ACTION_SETTINGS_ADVANCED = "org.xtimms.kitsune.ACTION_SETTINGS_ADVANCED";
	public static final String ACTION_SETTINGS_SYNCHRONIZATION = "org.xtimms.kitsune.ACTION_SETTINGS_SYNCHRONIZATION";
	public static final String ACTION_SETTINGS_AUTH = "org.xtimms.kitsune.ACTION_SETTINGS_AUTH";

	public static final int RESULT_RESTART = Activity.RESULT_FIRST_USER + 1;

    private PreferenceFragment mFragment;
	private Fragment pFragment;
    private SharedPreferences mDefaultPreferences;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		setSupportActionBar(R.id.toolbar);
		enableHomeAsUp();
        Context context = this;
		pFragment = null;
        View mContent = findViewById(R.id.content);
		mDefaultPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		final String action = TextUtils.notNull(getIntent().getAction());
		switch (action) {
			case Intent.ACTION_MANAGE_NETWORK_USAGE:
				mFragment = new NetworkSettingsFragment();
				break;
			case ACTION_SETTINGS_GENERAL:
				mFragment = new GeneralSettingsFragment();
				break;
			case ACTION_SETTINGS_READER:
				mFragment = new ReaderSettingsFragment();
				break;
			case ACTION_SETTINGS_SHELF:
				mFragment = new ShelfSettingsFragment();
				break;
			case ACTION_SETTINGS_BACKUP:
				mFragment = new BackupSettingsFragment();
				break;
			case ACTION_SETTINGS_ADVANCED:
				mFragment = new AdvancedSettingsFragment();
				break;
			case ACTION_SETTINGS_SYNCHRONIZATION:
				mFragment = new SyncSettingsFragment();
				break;
			case ACTION_SETTINGS_AUTH:
				mFragment = new AuthLoginFragment();
				break;
			default:
				finish();
		}
		getFragmentManager()
				.beginTransaction()
				.replace(R.id.content, mFragment)
				.commit();
	}

	@Override
	protected void onStart() {
		super.onStart();
		mDefaultPreferences.registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	protected void onStop() {
		mDefaultPreferences.unregisterOnSharedPreferenceChangeListener(this);
		super.onStop();
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		switch (key) {
			case "mangaupdates.enabled":
				JobSetupReceiver.setup(this);
				break;
			case "theme":
				setResult(RESULT_RESTART);
				break;
		}
	}

	@Override
	public boolean onPreferenceClick(Preference preference) {
		Intent intent = new Intent();
		switch (preference.getKey()) {
			case "mangaupdates.check_now":
				UpdatesCheckService.runForce(this);
				Snackbar.make(Objects.requireNonNull(mFragment.getView()), R.string.checking_new_chapters_summary, Snackbar.LENGTH_SHORT).show();
				return true;
			case "bugreport":
				FileLogger.sendLog(this);
				return true;
			/*case "csearchhist":
				SearchHistoryAdapter.clearHistory(context);
				Snackbar.make(mFragment.getView(), R.string.completed, Snackbar.LENGTH_SHORT).show();
				preference.setSummary(getString(R.string.items_, 0));
				return true;*/
			case "mangadir":
				if (checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
					new StorageSelectDialog(this)
							.setDirSelectListener(dir -> {
								if (!dir.canWrite()) {
									Toast.makeText(SettingsActivity.this, R.string.dir_no_access,
											Toast.LENGTH_SHORT).show();
									return;
								}
								preference.setSummary(dir.getPath());
								preference.getEditor()
										.putString("mangadir", dir.getPath()).apply();
								checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
							})
							.show();
				}
				return true;
			case "backup":
				if (checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
					BackupRestoreUtil.showBackupDialog(this);
				}
				return true;
			case "restore":
				if (checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
					BackupRestoreUtil.showRestoreDialog(this);
				}
				return true;
			case "sync.username":
				new AlertDialog.Builder(this)
						.setMessage(R.string.logout_confirm)
						.setPositiveButton(R.string.logout, (dialogInterface, i) -> new SyncLogoutTask(SettingsActivity.this).start())
						.setNegativeButton(android.R.string.cancel, null)
						.create().show();
				return true;
			case "sync.start":
				SyncService.start(this);
				return true;
			default:
				return false;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		if (requestCode == BackupRestoreUtil.BACKUP_IMPORT_CODE) {
			if (resultCode == RESULT_OK) {
				File file = AppHelper.getFileFromUri(this, data.getData());
				if (file != null) {
					new BackupRestoreUtil(this).restore(file);
				} else {
					Toast.makeText(this, R.string.error, Toast.LENGTH_SHORT).show();
				}
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
	}

	public void openFragment(Fragment fragment) {
		pFragment = fragment;
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		transaction.replace(R.id.content, pFragment);
		transaction.commit();
	}

	private static class SyncLogoutTask extends ProgressAsyncTask<Void, Void, RESTResponse> {

		SyncLogoutTask(AppBaseActivity activity) {
			super(activity);
			setCancelable(false);
		}

		@Override
		protected RESTResponse doInBackground(Void... voids) {
			try {
				return SyncHelper.get(getActivity()).logout();
			} catch (Exception e) {
				e.printStackTrace();
				return RESTResponse.fromThrowable(e);
			}
		}

		@Override
		protected void onPostExecute(@NonNull AppBaseActivity activity, RESTResponse restResponse) {
			if (restResponse.isSuccess()) {
				((SettingsActivity) activity).openFragment(new AuthLoginFragment());
			} else {
				Toast.makeText(activity, restResponse.getMessage(), Toast.LENGTH_SHORT).show();
			}
		}
	}
}
