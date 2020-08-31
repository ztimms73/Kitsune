package org.xtimms.kitsune.ui.recommendations;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import org.xtimms.kitsune.core.common.base.AppBaseActivity;
import org.xtimms.kitsune.R;

@SuppressWarnings("deprecation")
public final class RecommendationsActivity extends AppBaseActivity {

	private ViewPager mPager;
	private RecommendationsPagerAdapter mPagerAdapter;
	private BroadcastReceiver mBroadcastReceiver;
	@Nullable
	private Snackbar mSnackBar;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recommendations);
		setSupportActionBar(R.id.toolbar);
		enableHomeAsUp();

		mPager = findViewById(R.id.pager);
		TabLayout mTabs = findViewById(R.id.tabs);

		mPagerAdapter = new RecommendationsPagerAdapter(getFragmentManager(), this);
		mPager.setAdapter(mPagerAdapter);
		mTabs.setupWithViewPager(mPager);
		mBroadcastReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				onUpdated();
			}
		};
		final IntentFilter intentFilter = new IntentFilter(RecommendationsUpdateService.ACTION_RECOMMENDATIONS_UPDATED);
		registerReceiver(mBroadcastReceiver, intentFilter);
		if (mPagerAdapter.getCount() == 0) {
			updateContent();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.options_recommendations, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_refresh) {
			updateContent();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void updateContent() {
		if (mSnackBar != null) {
			mSnackBar.dismiss();
		}
		mSnackBar = Snackbar.make(mPager, R.string.updating_recommendations, Snackbar.LENGTH_INDEFINITE);
		mSnackBar.addCallback(new Snackbar.Callback() {
			@Override
			public void onDismissed(Snackbar transientBottomBar, int event) {
				mSnackBar = null;
				super.onDismissed(transientBottomBar, event);
			}
		});
		mSnackBar.show();
		startService(new Intent(this, RecommendationsUpdateService.class));
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(mBroadcastReceiver);
		super.onDestroy();
	}

	private void onUpdated() {
		mPagerAdapter.notifyDataSetChanged();
		if (mSnackBar != null) {
			mSnackBar.dismiss();
		}
	}
}
