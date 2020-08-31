package org.xtimms.kitsune.ui.mangalist.favourites;

import android.app.Fragment;
import android.app.FragmentManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.xtimms.kitsune.core.common.NativeFragmentPagerAdapter;
import org.xtimms.kitsune.core.models.Category;
import org.xtimms.kitsune.core.storage.db.CategoriesRepository;
import org.xtimms.kitsune.core.storage.db.CategoriesSpecification;
import org.xtimms.kitsune.core.storage.db.FavouritesSpecification;

import java.util.ArrayList;
import java.util.Objects;

@SuppressWarnings("deprecation")
final class CategoriesPagerAdapter extends NativeFragmentPagerAdapter {

	@NonNull
	private final CategoriesRepository mRepository;
	@NonNull
	private final CategoriesSpecification mSpecification;
	@NonNull
	private final ArrayList<Category> mDataset;

	public CategoriesPagerAdapter(@NonNull FragmentManager fm, @NonNull CategoriesRepository repository) {
		super(fm);
		mSpecification = new CategoriesSpecification()
				.orderByDate(false);
		mRepository = repository;
		mDataset = Objects.requireNonNull(repository.query(mSpecification));
	}

	@SuppressWarnings("deprecation")
	@NonNull
	@Override
	public Fragment getItem(int position) {
		final FavouritesFragment fragment = new FavouritesFragment();
		fragment.setArguments(new FavouritesSpecification()
				.category(mDataset.get(position).id)
				.orderByDate(true)
				.toBundle());
		return fragment;
	}

	@Override
	public void notifyDataSetChanged() {
		mDataset.clear();
		mDataset.addAll(Objects.requireNonNull(mRepository.query(mSpecification)));
		super.notifyDataSetChanged();
	}

	@Nullable
	@Override
	public CharSequence getPageTitle(int position) {
		return mDataset.get(position).name;
	}

	@Override
	public int getCount() {
		return mDataset.size();
	}

	int indexById(int id) {
		for (int i = 0; i < mDataset.size(); i++) {
			if (mDataset.get(i).id == id) {
				return i;
			}
		}
		return -1;
	}

	public ArrayList<Category> getData() {
		return mDataset;
	}
}
