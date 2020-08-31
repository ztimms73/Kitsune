package org.xtimms.kitsune.ui.reader;

import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.xtimms.kitsune.core.common.base.AppBaseDialogFragment;
import org.xtimms.kitsune.R;
import org.xtimms.kitsune.utils.CollectionsUtils;
import org.xtimms.kitsune.utils.LayoutUtils;
import org.xtimms.kitsune.core.models.MangaChapter;
import org.xtimms.kitsune.ui.preview.chapters.ChaptersListAdapter;

import java.util.ArrayList;

public final class ChaptersDialogFragment extends AppBaseDialogFragment implements View.OnClickListener, Runnable {

	private RecyclerView mRecyclerView;
	private Button mButtonNext;

	private ArrayList<MangaChapter> mChaptersList;
	private long mCurrentId;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final Bundle args = getArguments();
		assert args != null;
		mChaptersList = args.getParcelableArrayList("chapters");
		mCurrentId = args.getLong("current_id");
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.dialog_chapters, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mRecyclerView = view.findViewById(R.id.recyclerView);
		mButtonNext = view.findViewById(R.id.button_next);
		mButtonNext.setOnClickListener(this);
		view.findViewById(R.id.button_close).setOnClickListener(this);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		final Activity activity = getActivity();
		assert activity != null;
		mRecyclerView.addItemDecoration(new DividerItemDecoration(activity, LinearLayoutManager.VERTICAL));
		ChaptersListAdapter adapter = new ChaptersListAdapter(activity, mChaptersList, (ChaptersListAdapter.OnChapterClickListener) activity);
		adapter.setCurrentChapterId(mCurrentId);
		mRecyclerView.setAdapter(adapter);
		mRecyclerView.post(this);
	}

	@Override
	public void onClick(View v) {
		final Activity activity = getActivity();
		if (activity instanceof View.OnClickListener) {
			((View.OnClickListener) activity).onClick(v);
		}
		dismiss();
	}

	@Override
	public void run() {
		int current = CollectionsUtils.findChapterPositionById(mChaptersList, mCurrentId);
		if (current != -1) {
			LayoutUtils.scrollToCenter(mRecyclerView, current);
			//((LinearLayoutManager)mRecyclerView.getLayoutManager()).scrollToPositionWithOffset(current, 20);
			if (current == mChaptersList.size() - 1) {
				mButtonNext.setEnabled(false);
			}
		}
	}
}
