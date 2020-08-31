package org.xtimms.kitsune.ui.mangalist.favourites;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import org.xtimms.kitsune.core.common.base.AppBaseActivity;
import org.xtimms.kitsune.R;
import org.xtimms.kitsune.utils.CollectionsUtils;
import org.xtimms.kitsune.utils.ErrorUtils;
import org.xtimms.kitsune.core.ListWrapper;
import org.xtimms.kitsune.core.models.MangaFavourite;
import org.xtimms.kitsune.core.storage.db.CategoriesRepository;
import org.xtimms.kitsune.core.storage.db.FavouritesSpecification;

import java.util.ArrayList;

@SuppressWarnings("ALL")
public final class FavouritesActivity extends AppBaseActivity implements LoaderManager.LoaderCallbacks<ListWrapper<MangaFavourite>>{

	private static final int REQUEST_CATEGORIES = 14;

	private ViewPager mPager;
	private CategoriesPagerAdapter mPagerAdapter;
	private FavouritesSpecification mSpecifications;
	@SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
	private ArrayList<MangaFavourite> mDataset;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_favourites);
		setSupportActionBar(R.id.toolbar);
		enableHomeAsUp();

		mSpecifications = new FavouritesSpecification()
				.orderByDate(true);
		mDataset = new ArrayList<>();

		mPager = findViewById(R.id.pager);
		TabLayout mTabs = findViewById(R.id.tabs);

		mPagerAdapter = new CategoriesPagerAdapter(getFragmentManager(), CategoriesRepository.get(this));
		mPager.setAdapter(mPagerAdapter);
		mTabs.setupWithViewPager(mPager);

		final int category_id = getIntent().getIntExtra("category_id", 0);
		if (category_id != 0) {
			int index = mPagerAdapter.indexById(category_id);
			if (index != -1) {
				mPager.setCurrentItem(index, false);
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.options_favourites, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_manage_categories) {
			startActivityForResult(new Intent(this, CategoriesActivity.class), REQUEST_CATEGORIES);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_CATEGORIES && resultCode == RESULT_OK) {
			int pos = mPager.getCurrentItem();
			int id = mPagerAdapter.getData().get(pos).id;
			mPagerAdapter.notifyDataSetChanged();
			int newPos = CollectionsUtils.findCategoryPositionById(mPagerAdapter.getData(), id);
			if (newPos == -1) { //removed current page
				if (pos >= mPagerAdapter.getCount()) { //it was latest
					mPager.setCurrentItem(mPagerAdapter.getCount() - 1); //switch to new latest
				}
			} else {
				mPager.setCurrentItem(newPos, false);
			}
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onResume() {
		super.onResume();
		getLoaderManager().restartLoader(0, mSpecifications.toBundle(), this).forceLoad();
	}

	@Override
	public Loader<ListWrapper<MangaFavourite>> onCreateLoader(int id, Bundle args) {
		return new FavouritesLoader(this, FavouritesSpecification.from(args));
	}

	@Override
	public void onLoadFinished(Loader<ListWrapper<MangaFavourite>> loader, ListWrapper<MangaFavourite> data) {
		if (data.isSuccess()) {
			final ArrayList<MangaFavourite> list = data.get();
			mDataset.clear();
			mDataset.addAll(list);
			mPagerAdapter.notifyDataSetChanged();
		} else {
			Snackbar.make(mPager, ErrorUtils.getErrorMessage(data.getError()), Snackbar.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onLoaderReset(Loader<ListWrapper<MangaFavourite>> loader) {

	}
}

