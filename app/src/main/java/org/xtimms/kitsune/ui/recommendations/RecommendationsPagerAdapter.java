package org.xtimms.kitsune.ui.recommendations;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.res.Resources;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.xtimms.kitsune.core.common.NativeFragmentPagerAdapter;
import org.xtimms.kitsune.core.storage.db.RecommendationsRepository;
import org.xtimms.kitsune.core.storage.db.RecommendationsSpecifications;

import java.util.ArrayList;

@SuppressWarnings("deprecation")
class RecommendationsPagerAdapter extends NativeFragmentPagerAdapter {

	private final RecommendationsRepository mRepository;
	private final ArrayList<Integer> mDataset;
	private final Resources mResources;

	RecommendationsPagerAdapter(FragmentManager fm, Context context) {
		super(fm);
		mRepository = RecommendationsRepository.get(context);
		mResources = context.getResources();
		mDataset = mRepository.getCategories();
	}

	@NonNull
	@Override
	public Fragment getItem(int position) {
		final RecommendationsFragment fragment = new RecommendationsFragment();
		fragment.setArguments(new RecommendationsSpecifications()
				.category(mDataset.get(position))
				//.orderByRand()
				.toBundle());
		return fragment;
	}

	@Override
	public void notifyDataSetChanged() {
		mDataset.clear();
		mDataset.addAll(mRepository.getCategories());
		super.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mDataset.size();
	}

	@Nullable
	@Override
	public CharSequence getPageTitle(int position) {
		return mResources.getString(mDataset.get(position));
	}
}
