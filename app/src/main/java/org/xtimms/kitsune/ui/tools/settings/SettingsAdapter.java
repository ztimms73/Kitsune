package org.xtimms.kitsune.ui.tools.settings;

import androidx.annotation.IntDef;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;

import com.lucasurbas.listitemview.ListItemView;

import org.jetbrains.annotations.NotNull;
import org.xtimms.kitsune.R;
import org.xtimms.kitsune.core.common.CrashHandler;
import org.xtimms.kitsune.core.common.Dismissible;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;

public class SettingsAdapter extends RecyclerView.Adapter<SettingsAdapter.PreferenceHolder> {

	private final ArrayList<SettingsHeader> mDataset;
	private final AdapterView.OnItemClickListener mClickListener;

	SettingsAdapter(ArrayList<SettingsHeader> headers, AdapterView.OnItemClickListener clickListener) {
		mDataset = headers;
		mClickListener = clickListener;
		setHasStableIds(true);
	}

	@NotNull
	@Override
	public PreferenceHolder onCreateViewHolder(@NotNull ViewGroup parent, @ItemType int viewType) {
		switch (viewType) {
			case ItemType.TYPE_ITEM_DEFAULT:
				return new PreferenceHolder(LayoutInflater.from(parent.getContext())
						.inflate(R.layout.item_single_line_icon, parent, false));
			case ItemType.TYPE_TIP:
				return new TipHolder(LayoutInflater.from(parent.getContext())
						.inflate(R.layout.item_tip, parent, false));
			default:
				throw new AssertionError("Unknown viewType");
		}
	}

	@Override
	public void onBindViewHolder(PreferenceHolder holder, int position) {
		SettingsHeader item = mDataset.get(position);
		holder.list.setTitle(item.title);
		holder.list.setIconDrawable(item.icon);
		if (item.summary != null) {
			holder.list.setSubtitle(item.summary);
		}
		if (holder instanceof TipHolder) {
			((TipHolder) holder).button.setText(item.actionText);
			((TipHolder) holder).button.setId(item.actionId);
		}
	}

	@Override
	public int getItemViewType(int position) {
		return mDataset.get(position).hasAction() ? ItemType.TYPE_TIP : ItemType.TYPE_ITEM_DEFAULT;

	}

	@Override
	public long getItemId(int position) {
		return mDataset.get(position).title.hashCode();
	}

	@Override
	public int getItemCount() {
		return mDataset.size();
	}

	class PreferenceHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

		private final ListItemView list;

		PreferenceHolder(View itemView) {
			super(itemView);
			list = itemView.findViewById(R.id.list);
			itemView.setOnClickListener(this);
		}

		@Override
		public void onClick(View view) {
			mClickListener.onItemClick(null, itemView, getAdapterPosition(), getItemId());
		}
	}

	class TipHolder extends PreferenceHolder implements Dismissible {

		final Button button;

		TipHolder(View itemView) {
			super(itemView);
			itemView.setOnClickListener(null);
			button = itemView.findViewById(android.R.id.button1);
			button.setOnClickListener(this);
		}

		@Override
		public void onClick(View view) {
            if (view.getId() == R.id.action_crash_report) {
                final CrashHandler crashHandler = CrashHandler.get();
                if (crashHandler != null) {
                    new AlertDialog.Builder(view.getContext())
                            .setTitle(crashHandler.getErrorClassName())
                            .setMessage(crashHandler.getErrorMessage() + "\n\n" + crashHandler.getErrorStackTrace())
                            .setNegativeButton(R.string.close, null)
                            .create()
                            .show();
                }
            }
		}

		@Override
		public void dismiss() {
            if (button.getId() == R.id.action_crash_report) {
                final CrashHandler crashHandler = CrashHandler.get();
                if (crashHandler != null) {
                    crashHandler.clear();
                }
            }
			mDataset.remove(getAdapterPosition());
			notifyDataSetChanged();
			//notifyItemRemoved throws ArrayIndexOutOfBoundsException
		}
	}

	@Retention(RetentionPolicy.SOURCE)
	@IntDef({ItemType.TYPE_ITEM_DEFAULT, ItemType.TYPE_TIP})
	public @interface ItemType {
		int TYPE_ITEM_DEFAULT = 0;
		int TYPE_TIP = 1;
	}
}