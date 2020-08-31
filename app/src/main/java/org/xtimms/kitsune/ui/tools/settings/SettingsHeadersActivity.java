package org.xtimms.kitsune.ui.tools.settings;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import org.xtimms.kitsune.core.common.base.AppBaseActivity;
import org.xtimms.kitsune.KitsuneApp;
import org.xtimms.kitsune.R;
import org.xtimms.kitsune.core.helpers.SyncHelper;
import org.xtimms.kitsune.ui.help.HelpActivity;
import org.xtimms.kitsune.ui.tools.settings.providers.ProvidersSettingsActivity;

import java.util.ArrayList;

public final class SettingsHeadersActivity extends AppBaseActivity implements AdapterView.OnItemClickListener {

	private static final int REQUEST_SETTINGS = 12;

	private ArrayList<SettingsHeader> mHeaders;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings_headers);
		setSupportActionBar(R.id.toolbar);
		enableHomeAsUp();

		RecyclerView mRecyclerView = findViewById(R.id.recyclerView);
		mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
		mRecyclerView.setHasFixedSize(true);

		mHeaders = new ArrayList<>();
		mHeaders.add(new SettingsHeader(this, 1, R.string.general, R.drawable.ic_tune_white_24dp));
		mHeaders.add(new SettingsHeader(this, 2, R.string.manga_catalogues, R.drawable.ic_network_white));
		mHeaders.add(new SettingsHeader(this, 4, R.string.action_reading_options, R.drawable.ic_chrome_reader_mode_white_24dp));
		mHeaders.add(new SettingsHeader(this, 5, R.string.backup_options, R.drawable.ic_settings_backup_restore_white_24dp));
		//mHeaders.add(new SettingsHeader(this, 7, R.string.synchronization, R.drawable.ic_baseline_cloud_24));
		mHeaders.add(new SettingsHeader(this, 6, R.string.advanced, R.drawable.ic_code_white_24dp));

		SettingsAdapter mAdapter = new SettingsAdapter(mHeaders, this);
		mRecyclerView.setAdapter(mAdapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.options_settings, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.nav_action_help) {
            startActivity(new Intent(SettingsHeadersActivity.this, HelpActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		SettingsHeader header = mHeaders.get(position);
		Intent intent;
		switch (header.id) {
			case 2:
				intent = new Intent(view.getContext(), ProvidersSettingsActivity.class);
				break;
			case 1:
				intent = new Intent(view.getContext(), SettingsActivity.class)
						.setAction(SettingsActivity.ACTION_SETTINGS_GENERAL);
				break;
			case 4:
				intent = new Intent(view.getContext(), SettingsActivity.class)
						.setAction(SettingsActivity.ACTION_SETTINGS_READER);
				break;
			case 5:
				intent = new Intent(view.getContext(), SettingsActivity.class)
						.setAction(SettingsActivity.ACTION_SETTINGS_BACKUP);
				break;
			case 6:
				intent = new Intent(view.getContext(), SettingsActivity.class)
						.setAction(SettingsActivity.ACTION_SETTINGS_ADVANCED);
				break;
			case 7:
				intent = SyncHelper.get(this).isAuthorized() ? new Intent(view.getContext(), SettingsActivity.class)
						.setAction(SettingsActivity.ACTION_SETTINGS_SYNCHRONIZATION) :
						new Intent(view.getContext(), SettingsActivity.class)
						.setAction(SettingsActivity.ACTION_SETTINGS_AUTH);
				break;
			default:
				return;
		}
		startActivityForResult(intent, REQUEST_SETTINGS);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_SETTINGS && resultCode == SettingsActivity.RESULT_RESTART) {
			new AlertDialog.Builder(this)
					.setMessage(R.string.need_restart)
					.setNegativeButton(R.string.postpone, null)
					.setPositiveButton(R.string.restart, (dialog, which) ->
							KitsuneApp.from(SettingsHeadersActivity.this).restart())
					.create()
					.show();
		}
	}
}
