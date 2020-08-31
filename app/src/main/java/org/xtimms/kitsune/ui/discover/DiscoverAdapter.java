package org.xtimms.kitsune.ui.discover;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.IntDef;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lucasurbas.listitemview.ListItemView;

import org.jetbrains.annotations.NotNull;
import org.xtimms.kitsune.R;
import org.xtimms.kitsune.core.models.ProviderHeader;
import org.xtimms.kitsune.ui.discover.bookmarks.BookmarksListActivity;
import org.xtimms.kitsune.ui.filepicker.FilePickerActivity;
import org.xtimms.kitsune.ui.mangalist.MangaListActivity;
import org.xtimms.kitsune.ui.mangalist.updates.MangaUpdatesActivity;
import org.xtimms.kitsune.ui.recommendations.RecommendationsActivity;
import org.xtimms.kitsune.core.storage.SavedMangaActivity;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;

public final class DiscoverAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {

	private final ArrayList<Object> mDataset;

	public DiscoverAdapter(ArrayList<Object> dataset) {
		mDataset = dataset;
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, @ItemViewType int viewType) {
		final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
		RecyclerView.ViewHolder holder;
		switch (viewType) {
			case ItemViewType.TYPE_ITEM_WITH_ICON:
				holder = new DetailsProviderHolder(inflater.inflate(R.layout.item_two_lines_icon, parent, false));
				break;
			case ItemViewType.TYPE_HEADER:
				return new HeaderHolder(inflater.inflate(R.layout.header_group, parent, false));
			case ItemViewType.TYPE_ITEM_DEFAULT:
				holder = new ProviderHolder(inflater.inflate(R.layout.item_single_line, parent, false));
				break;
			default:
				throw new AssertionError("Unknown viewType");
		}
		holder.itemView.setOnClickListener(this);
		return holder;
	}

	@Override
	public void onBindViewHolder(@NotNull RecyclerView.ViewHolder holder, int position) {
		if (holder instanceof DetailsProviderHolder) {
			ProviderHeaderDetailed item = (ProviderHeaderDetailed) mDataset.get(position);
			((DetailsProviderHolder) holder).list.setTitle(item.dName);
			holder.itemView.setTag(item.cName);
			((DetailsProviderHolder) holder).list.setIconDrawable(item.icon);
			((DetailsProviderHolder) holder).list.setSubtitle(item.summary);
		} else if (holder instanceof ProviderHolder) {
			ProviderHeader item = (ProviderHeader) mDataset.get(position);
			holder.itemView.setTag(item.cName);
			((ProviderHolder) holder).text1.setText(item.dName);
		} else if (holder instanceof HeaderHolder) {
			((HeaderHolder) holder).textView.setText((String) mDataset.get(position));
		}
	}

	@ItemViewType
	@Override
	public int getItemViewType(int position) {
		final Object item = mDataset.get(position);
		if (item instanceof String) {
			return ItemViewType.TYPE_HEADER;
		} else if (item instanceof ProviderHeaderDetailed) {
			return ItemViewType.TYPE_ITEM_WITH_ICON;
		} else if (item instanceof ProviderHeader) {
			return ItemViewType.TYPE_ITEM_DEFAULT;
		} else {
			throw new AssertionError("Unknown ItemViewType");
		}
	}

	@Override
	public int getItemCount() {
		return mDataset.size();
	}

	@Override
	public void onClick(View view) {
		final String cname = String.valueOf(view.getTag());
		final Context context = view.getContext();
		switch (cname) {
			case SpecificCName.BROWSE_IMPORT:
				context.startActivity(new Intent(context.getApplicationContext(), FilePickerActivity.class));
				break;
			case SpecificCName.BROWSE_SAVED:
				context.startActivity(new Intent(context.getApplicationContext(), SavedMangaActivity.class));
				break;
			case SpecificCName.BROWSE_RECOMMENDATIONS:
				context.startActivity(new Intent(context.getApplicationContext(), RecommendationsActivity.class));
				break;
			case SpecificCName.BROWSE_BOOKMARKS:
				context.startActivity(new Intent(context.getApplicationContext(), BookmarksListActivity.class));
				break;
			case SpecificCName.BROWSE_NEW_CHAPTERS:
				context.startActivity(new Intent(context.getApplicationContext(), MangaUpdatesActivity.class));
				break;
			default:
				context.startActivity(new Intent(context.getApplicationContext(), MangaListActivity.class)
						.putExtra("provider.cname", cname));
		}
	}

	static class ProviderHolder extends RecyclerView.ViewHolder {

		final TextView text1;

		ProviderHolder(View itemView) {
			super(itemView);
			text1 = itemView.findViewById(android.R.id.text1);
		}
	}

	static class DetailsProviderHolder extends ProviderHolder {

		final ListItemView list;

		DetailsProviderHolder(View itemView) {
			super(itemView);
			list = itemView.findViewById(R.id.list);
		}
	}

	static class HeaderHolder extends RecyclerView.ViewHolder {

		private final TextView textView;

		public HeaderHolder(View itemView) {
			super(itemView);
			textView = itemView.findViewById(R.id.textView);
		}
	}

	@Retention(RetentionPolicy.SOURCE)
	@IntDef({ItemViewType.TYPE_ITEM_DEFAULT, ItemViewType.TYPE_ITEM_WITH_ICON, ItemViewType.TYPE_HEADER})
	public @interface ItemViewType {
		int TYPE_ITEM_DEFAULT = 0;
		int TYPE_ITEM_WITH_ICON = 1;
		int TYPE_HEADER = 3;
	}
}
