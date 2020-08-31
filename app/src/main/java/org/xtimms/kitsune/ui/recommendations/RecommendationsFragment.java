package org.xtimms.kitsune.ui.recommendations;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.snackbar.Snackbar;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.xtimms.kitsune.core.common.base.AppBaseFragment;
import org.xtimms.kitsune.R;
import org.xtimms.kitsune.utils.ErrorUtils;
import org.xtimms.kitsune.core.ListWrapper;
import org.xtimms.kitsune.core.models.MangaRecommendation;
import org.xtimms.kitsune.core.storage.db.RecommendationsSpecifications;

import java.util.ArrayList;

@SuppressWarnings("ALL")
public final class RecommendationsFragment extends AppBaseFragment implements LoaderManager.LoaderCallbacks<ListWrapper<MangaRecommendation>> {

	private RecyclerView mRecyclerView;
	private ProgressBar mProgressBar;

	private RecommendationsSpecifications mSpecifications;
	private RecommendationsAdapter mAdapter;
	private ArrayList<MangaRecommendation> mDataset;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mDataset = new ArrayList<>();
		mSpecifications = RecommendationsSpecifications.from(getArguments());
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_favourites, container,false);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mProgressBar = view.findViewById(R.id.progressBar);
		mRecyclerView = view.findViewById(R.id.recyclerView);
		mRecyclerView.setHasFixedSize(true);
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		final Activity activity = getActivity();
		mAdapter = new RecommendationsAdapter(mDataset);
		mRecyclerView.setAdapter(mAdapter);
		getLoaderManager().initLoader((int) mSpecifications.getId(), mSpecifications.toBundle(), this).forceLoad();
	}

	@Override
	public Loader<ListWrapper<MangaRecommendation>> onCreateLoader(int id, Bundle args) {
		return new RecommendationsLoader(getActivity(), RecommendationsSpecifications.from(args));
	}

	@Override
	public void onLoadFinished(Loader<ListWrapper<MangaRecommendation>> loader, ListWrapper<MangaRecommendation> result) {
		mProgressBar.setVisibility(View.GONE);
		if (result.isSuccess()) {
			final ArrayList<MangaRecommendation> list = result.get();
			mDataset.clear();
			mDataset.addAll(list);
			mAdapter.notifyDataSetChanged();
		} else {
			Snackbar.make(mRecyclerView, ErrorUtils.getErrorMessage(result.getError()), Snackbar.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onLoaderReset(Loader<ListWrapper<MangaRecommendation>> loader) {

	}
}
