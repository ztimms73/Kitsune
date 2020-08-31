package org.xtimms.kitsune.ui.mangalist;

import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.appbar.AppBarLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import org.xtimms.kitsune.R;
import org.xtimms.kitsune.core.common.dialogs.AppBaseBottomSheetDialogFragment;
import org.xtimms.kitsune.core.models.MangaType;
import org.xtimms.kitsune.utils.CollectionsUtils;
import org.xtimms.kitsune.core.common.views.recyclerview.HeaderDividerItemDecoration;
import org.xtimms.kitsune.core.models.MangaGenre;

import java.util.ArrayList;
import java.util.Objects;

public final class FilterDialogFragment extends AppBaseBottomSheetDialogFragment implements View.OnClickListener {

	private RecyclerView mRecyclerView;
	private Toolbar mToolbar;
	private FilterSortAdapter mAdapter;

	private int[] mSorts;
	private int[] mAdditionalSorts;
	private MangaGenre[] mGenres;
	private MangaType[] mTypes;
	private MangaQueryArguments mQueryArgs;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final Bundle args = getArguments();
		assert args != null;
		mSorts = args.getIntArray("sorts");
		mAdditionalSorts = args.getIntArray("additionalSorts");
		mGenres = (MangaGenre[]) args.getParcelableArray("genres");
		mTypes = (MangaType[]) args.getParcelableArray("types");
		mQueryArgs = MangaQueryArguments.from(Objects.requireNonNull(args.getBundle("query")));
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.dialog_filter, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mRecyclerView = view.findViewById(R.id.recyclerView);
		mToolbar = view.findViewById(R.id.toolbar);
		AppBarLayout mAppBar = view.findViewById(R.id.appbar);
		Button mButtonApply = view.findViewById(R.id.buttonApply);
		Button mButtonReset = view.findViewById(R.id.buttonReset);

		mButtonApply.setOnClickListener(this);
		mButtonReset.setOnClickListener(this);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		final Activity activity = getActivity();
		assert activity != null;
		mAdapter = new FilterSortAdapter(activity, mSorts, mAdditionalSorts, mGenres, mTypes, mQueryArgs.sort, mQueryArgs.additionalSort, mQueryArgs.genresValues(), mQueryArgs.typesValues());
		mRecyclerView.setAdapter(mAdapter);
		mRecyclerView.addItemDecoration(new HeaderDividerItemDecoration(activity));
		mToolbar.setNavigationOnClickListener(this);

		/*final BottomSheetBehavior behavior = BottomSheetBehavior.from(getDialog().findViewById(android.support.design.R.id.design_bottom_sheet));
		behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
			@Override
			public void onStateChanged(@NonNull View bottomSheet, int newState) {
				AnimationUtils.setVisibility(mAppBar, newState == BottomSheetBehavior.STATE_EXPANDED ? View.VISIBLE : View.GONE);
			}

			@Override
			public void onSlide(@NonNull View bottomSheet, float slideOffset) {

			}
		});*/
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.buttonReset:
				mAdapter.reset();
				final Toast toast = Toast.makeText(v.getContext(), R.string.filter_reset, Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.TOP, 0, 0);
				toast.show();
				break;
			case R.id.buttonApply:
				Activity activity = getActivity();
				if (activity instanceof FilterCallback) {
					ArrayList<MangaGenre> genres = CollectionsUtils.getIfTrue(mGenres, mAdapter.getSelectedGenres());
					ArrayList<MangaType> types = CollectionsUtils.getIfTrue(mTypes, mAdapter.getSelectedTypes());
 					((FilterCallback) activity).setFilter(mAdapter.getSelectedSort(), mAdapter.getSelectedAdditionalSort(), genres.toArray(new MangaGenre[0]), types.toArray(new MangaType[0]));
				}
			default:
				dismiss();
		}
	}
}
