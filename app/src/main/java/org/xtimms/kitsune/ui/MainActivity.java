package org.xtimms.kitsune.ui;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.jetbrains.annotations.NotNull;
import org.xtimms.kitsune.R;
import org.xtimms.kitsune.core.common.CrashHandler;
import org.xtimms.kitsune.core.common.base.AppBaseActivity;
import org.xtimms.kitsune.core.common.base.AppBaseFragment;
import org.xtimms.kitsune.core.updchecker.UpdatesCheckService;
import org.xtimms.kitsune.core.storage.FlagsStorage;
import org.xtimms.kitsune.ui.discover.DiscoverFragment;
import org.xtimms.kitsune.ui.mangalist.favourites.CategoriesActivity;
import org.xtimms.kitsune.ui.search.SearchActivity;
import org.xtimms.kitsune.ui.shelf.OnTipsActionListener;
import org.xtimms.kitsune.ui.shelf.ShelfFragment;
import org.xtimms.kitsune.ui.tools.DrawerHeaderImageTool;
import org.xtimms.kitsune.ui.tools.ToolsFragment;
import org.xtimms.kitsune.ui.tools.settings.SettingsActivity;
import org.xtimms.kitsune.ui.tools.settings.SettingsHeadersActivity;

import java.util.Objects;

@SuppressWarnings("ALL")
public final class MainActivity extends AppBaseActivity implements NavigationView.OnNavigationItemSelectedListener, PopupMenu.OnMenuItemClickListener,
		OnTipsActionListener{

	private NavigationView mNavigationView;
	private View mContent;
	private AppBaseFragment mFragment;
	private ActionBarDrawerToggle mToggle;
	private DrawerLayout mDrawerLayout;
	private DrawerHeaderImageTool mDrawerHeaderTool;
	//private SearchHistoryAdapter mSearchAdapter;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
        enableTransparentStatusBar(android.R.color.transparent);
		Toolbar mToolbar = findViewById(R.id.toolbar);
		setSupportActionBar(mToolbar);
		mToolbar.setNavigationOnClickListener(v -> mDrawerLayout.openDrawer(GravityCompat.START));
		mToolbar.inflateMenu(R.menu.options_shelf);

		mDrawerLayout = findViewById(R.id.drawer);
		if (mDrawerLayout != null){
			mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close){
				@Override
				public void onDrawerClosed(View drawerView) {
					super.onDrawerClosed(drawerView);
					invalidateOptionsMenu();
				}

				@Override
				public void onDrawerOpened(View drawerView) {
					super.onDrawerOpened(drawerView);
					invalidateOptionsMenu();
				}
			};
			mToggle.setDrawerIndicatorEnabled(true);
			mDrawerLayout.addDrawerListener(mToggle);
			Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
			getSupportActionBar().setHomeButtonEnabled(true);
		}
		mNavigationView = findViewById(R.id.navView);
		mContent = findViewById(R.id.content);
		mNavigationView.setNavigationItemSelectedListener(this);
		mNavigationView.setCheckedItem(R.id.section_shelf);

		mDrawerHeaderTool = new DrawerHeaderImageTool(this, mNavigationView);
		mDrawerHeaderTool.initDrawerImage();

		//mSearchAdapter = new SearchHistoryAdapter(this);

		mFragment = new ShelfFragment();
		getFragmentManager().beginTransaction()
				.replace(R.id.content, mFragment)
				.commitAllowingStateLoss();

		RecyclerView mRecyclerView = findViewById(R.id.recyclerView);
		mDrawerLayout.postDelayed(new ListTipHelper(), 500);

		if (isDarkTheme()) {
			ColorStateList csl = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white_overlay_85));
			mNavigationView.setItemTextColor(csl);
			mNavigationView.setItemIconTintList(csl);
		}
		//registerReceiver(mSyncReceiver, new IntentFilter(SyncService.SYNC_EVENT));
	}

	private static class ListTipHelper implements Runnable {
		@Override
		public void run() {
		}
	}

	@Override
	protected void onPostCreate(@Nullable Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(@NotNull Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mToggle.onConfigurationChanged(newConfig);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		mDrawerHeaderTool.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onResume() {
		super.onResume();
		mContent.requestFocus();
	}

	private void closeDrawer() {
		if (mDrawerLayout != null){
			mDrawerLayout.closeDrawer(mNavigationView);
		}
	}

	@Override
	public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
		Intent intent = new Intent();
		switch (menuItem.getItemId()) {
			case R.id.section_shelf:
				mFragment = new ShelfFragment();
				Objects.requireNonNull(getSupportActionBar()).setTitle(getResources().getString(R.string.app_name));
				break;
			case R.id.section_discover:
				mFragment = new DiscoverFragment();
				Objects.requireNonNull(getSupportActionBar()).setTitle(getResources().getString(R.string.discover));
				break;
			case R.id.action_tools:
				mFragment = new ToolsFragment();
				Objects.requireNonNull(getSupportActionBar()).setTitle(getResources().getString(R.string.tools));
				break;
			case R.id.nav_action_settings:
				intent.setClass(MainActivity.this, SettingsHeadersActivity.class);
				startActivity(intent);
				break;
			case R.id.nav_action_about:
				intent.setClass(MainActivity.this, AboutActivity.class);
				startActivity(intent);
				break;
			//case R.id.nav_action_help:
			//	mTheme = ThemeUtils.getAppTheme(this);
			//	intent.setClass(MainActivity.this, HelpActivity.class);
			//	startActivity(intent);
			//	break;
			default:
				return false;
		}

		getFragmentManager().beginTransaction()
				.setCustomAnimations(R.animator.fade_in, R.animator.fade_out, R.animator.fade_in, R.animator.fade_out)
				.replace(R.id.content, mFragment)
				.commit();
		closeDrawer();
		return true;
	}

	@Override
	public boolean onMenuItemClick(MenuItem item) {
		return mFragment.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.options_shelf, menu);
		SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
		final SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		final SearchableInfo searchableInfo;
		if (searchManager != null) {
			searchableInfo = searchManager.getSearchableInfo(new ComponentName(this, SearchActivity.class));
			searchView.setSearchableInfo(searchableInfo);
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_shelf_settings:
				startActivity(new Intent(MainActivity.this, SettingsActivity.class)
						.setAction(SettingsActivity.ACTION_SETTINGS_SHELF));
				return true;
			case R.id.action_check_updates:
                Snackbar.make(Objects.requireNonNull(mFragment.getView()), R.string.checking_new_chapters, Snackbar.LENGTH_SHORT).show();
				UpdatesCheckService.runForce(this);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		return super.onKeyUp(keyCode, event);
	}

	@Override
	public void onTipActionClick(int actionId) {
		switch (actionId) {
			case R.id.action_crash_report:
				final CrashHandler crashHandler = CrashHandler.get();
				if (crashHandler != null) {
					new AlertDialog.Builder(this)
							.setTitle(crashHandler.getErrorClassName())
							.setMessage(crashHandler.getErrorMessage() + "\n\n" + crashHandler.getErrorStackTrace())
							.setNegativeButton(R.string.close, null)
							.create()
							.show();
				}
				break;
			case R.id.action_discover:
				mFragment = new DiscoverFragment();
				mNavigationView.setCheckedItem(R.id.action_discover);
				getFragmentManager().beginTransaction()
						.replace(R.id.content, mFragment)
						.commit();
				break;
			case R.id.action_category:
				Intent intent = new Intent();
				intent.setClass(this, CategoriesActivity.class);
				startActivity(intent);
				break;
		}
	}

	@Override
	public void onTipDismissed(int actionId) {
		switch (actionId) {
			case R.id.action_wizard:
			case R.id.action_category:
				FlagsStorage.get(this).setWizardRequired(false);
				break;
		}
	}

}
