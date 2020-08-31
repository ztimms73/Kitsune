package org.xtimms.kitsune.ui.tools.settings.providers;

import android.annotation.SuppressLint;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import org.xtimms.kitsune.R;
import org.xtimms.kitsune.core.models.ProviderHeader;

import java.util.ArrayList;

final class ProvidersAdapter extends RecyclerView.Adapter<ProvidersAdapter.ProviderHolder> {

	private final OnStartDragListener mDragListener;
	private final ArrayList<ProviderHeader> mDataset;
	private final SparseBooleanArray mChecks;

	ProvidersAdapter(ArrayList<ProviderHeader> dataset, SparseBooleanArray checks, OnStartDragListener dragListener) {
		mDataset = dataset;
		mChecks = checks;
		mDragListener = dragListener;
		setHasStableIds(true);
	}

	@Override
	public ProviderHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		Context context = parent.getContext();
		return new ProviderHolder(LayoutInflater.from(parent.getContext())
				.inflate(R.layout.item_provider, parent, false));
	}

	@Override
	public void onBindViewHolder(ProviderHolder holder, int position) {
		holder.checkBox.setChecked(mChecks.get(position, true));
		holder.textView1.setText(mDataset.get(position).dName);
		holder.textView2.setText(mDataset.get(position).desc);
		String str = mDataset.get(position).cName;
	}

	@Override
	public int getItemCount() {
		return mDataset.size();
	}

	@Override
	public long getItemId(int position) {
		return mDataset.get(position).cName.hashCode();
	}

	@SuppressLint("ClickableViewAccessibility")
	class ProviderHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnTouchListener {

		final CheckBox checkBox;
		final TextView textView1;
		final TextView textView2;
		final AppCompatImageView imageViewReorder;

		ProviderHolder(View itemView) {
			super(itemView);
			checkBox = itemView.findViewById(android.R.id.checkbox);
			textView1 = itemView.findViewById(android.R.id.text1);
			textView2 = itemView.findViewById(android.R.id.text2);
			imageViewReorder = itemView.findViewById(R.id.imageView_reorder);

			checkBox.setOnClickListener(this);
			imageViewReorder.setOnTouchListener(this);
		}

		@Override
		public void onClick(View v) {
			final int position = getAdapterPosition();
            if (v.getId() == android.R.id.checkbox) {
                mChecks.put(position, checkBox.isChecked());
            }
		}

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
				mDragListener.onStartDrag(this);
				return true;
			}
			return false;
		}
	}

	interface OnStartDragListener {
		void onStartDrag(RecyclerView.ViewHolder viewHolder);
	}
}
