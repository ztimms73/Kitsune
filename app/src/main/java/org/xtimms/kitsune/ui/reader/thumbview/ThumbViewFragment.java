package org.xtimms.kitsune.ui.reader.thumbview;

import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.xtimms.kitsune.R;
import org.xtimms.kitsune.core.common.dialogs.AppBaseBottomSheetDialogFragment;
import org.xtimms.kitsune.utils.MetricsUtils;
import org.xtimms.kitsune.utils.ResourceUtils;
import org.xtimms.kitsune.core.common.views.recyclerview.SpaceItemDecoration;
import org.xtimms.kitsune.core.models.MangaPage;

import java.util.ArrayList;

public final class ThumbViewFragment extends AppBaseBottomSheetDialogFragment implements OnThumbnailClickListener {

	private RecyclerView mRecyclerView;
	private ArrayList<MangaPage> mPages;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final Bundle args = getArguments();
		assert args != null;
		mPages = args.getParcelableArrayList("pages");
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.recyclerview, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mRecyclerView = view.findViewById(R.id.recyclerView);
		final int spanCount = MetricsUtils.getPreferredColumnsCountMedium(view.getResources());
		mRecyclerView.setLayoutManager(new GridLayoutManager(view.getContext(), spanCount));
		mRecyclerView.addItemDecoration(new SpaceItemDecoration(ResourceUtils.dpToPx(view.getResources(), 4)));
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		final Activity activity = getActivity();
		ThumbViewAdapter adapter = new ThumbViewAdapter(activity, mPages, this);
		mRecyclerView.setAdapter(adapter);
	}

	@Override
	public void onThumbnailClick(int position) {
		final Activity activity = getActivity();
		if (activity instanceof OnThumbnailClickListener) {
			((OnThumbnailClickListener) activity).onThumbnailClick(position);
			dismiss();
		}
	}
}
