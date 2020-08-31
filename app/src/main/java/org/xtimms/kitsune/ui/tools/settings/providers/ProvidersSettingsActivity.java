package org.xtimms.kitsune.ui.tools.settings.providers;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.util.SparseBooleanArray;

import org.jetbrains.annotations.NotNull;
import org.xtimms.kitsune.R;
import org.xtimms.kitsune.core.models.ProviderHeader;
import org.xtimms.kitsune.core.storage.ProvidersStore;
import org.xtimms.kitsune.core.common.base.AppBaseActivity;
import org.xtimms.kitsune.utils.CollectionsUtils;

import java.util.ArrayList;
import java.util.Collections;

public final class ProvidersSettingsActivity extends AppBaseActivity implements ProvidersAdapter.OnStartDragListener {

	private ProvidersStore mProvidersStore;
	private ProvidersAdapter mAdapter;
	private ArrayList<ProviderHeader> mProviders;
	private SparseBooleanArray mChecks;
	private ItemTouchHelper mItemTouchHelper;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings_providers);
		setSupportActionBar(R.id.toolbar);
		enableHomeAsUp();
		mProvidersStore = new ProvidersStore(this);
		mChecks = new SparseBooleanArray();
		mProviders = mProvidersStore.getAllProvidersSorted();
		int[] disabled = mProvidersStore.getDisabledIds();
		for (int i = 0; i < mProviders.size(); i++) {
			if (CollectionsUtils.indexOf(disabled, mProviders.get(i).hashCode()) != -1) {
				mChecks.put(i, false);
			}
		}
		final RecyclerView recyclerView = findViewById(R.id.recyclerView);
		recyclerView.setLayoutManager(new LinearLayoutManager(this));
		mAdapter = new ProvidersAdapter(mProviders, mChecks, this);
		recyclerView.setAdapter(mAdapter);

		mItemTouchHelper = new ItemTouchHelper(new ReorderCallback());
		mItemTouchHelper.attachToRecyclerView(recyclerView);
	}

	@Override
	public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
		mItemTouchHelper.startDrag(viewHolder);
	}

	@Override
	protected void onPause() {
		mProvidersStore.save(mProviders, mChecks);
		super.onPause();
	}

	private class ReorderCallback extends ItemTouchHelper.Callback {

		@Override
		public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
			return makeMovementFlags(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0);
		}

		@Override
		public boolean onMove(@NotNull RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
			/*final int fromPosition = viewHolder.getAdapterPosition();
			final int toPosition = target.getAdapterPosition();
			Collections.swap(mProviders, fromPosition, toPosition);
			CollectionsUtils.swap(mChecks, fromPosition, toPosition, true);
			mAdapter.notifyItemMoved(fromPosition, toPosition);
			return true;*/
			int fromPosition = viewHolder.getAdapterPosition();
			int toPosition = target.getAdapterPosition();

			if (fromPosition < toPosition) {
				for (int i = fromPosition; i < toPosition; i++) {
					Collections.swap(mProviders, i, i + 1);
					CollectionsUtils.swap(mChecks, i, i + 1, true);
				}
			} else {
				for (int i = fromPosition; i > toPosition; i--) {
					Collections.swap(mProviders, i, i - 1);
					CollectionsUtils.swap(mChecks, i, i - 1, true);
				}
			}

			mAdapter.notifyItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
			return true;
		}

		@Override
		public void onSwiped(@NotNull RecyclerView.ViewHolder viewHolder, int direction) {

		}

		@Override
		public boolean isLongPressDragEnabled() {
			return false;
		}
	}
}
