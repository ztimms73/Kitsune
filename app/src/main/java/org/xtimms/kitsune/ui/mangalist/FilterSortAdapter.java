package org.xtimms.kitsune.ui.mangalist;

import android.content.Context;
import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.recyclerview.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;
import org.xtimms.kitsune.R;
import org.xtimms.kitsune.core.models.MangaType;
import org.xtimms.kitsune.utils.CollectionsUtils;
import org.xtimms.kitsune.core.models.MangaGenre;
import org.xtimms.kitsune.core.models.TypedString;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;

@SuppressWarnings("ALL")
public final class FilterSortAdapter extends RecyclerView.Adapter {

	private final ArrayList<TypedString> mDataset;
	private int mSelectedSort;
    private int mSelectedAdditionalSort;
	private final SparseBooleanArray mSelectedGenres;
	private final SparseBooleanArray mSelectedTypes;

	FilterSortAdapter(Context context, @NonNull @StringRes int[] sorts, @StringRes int[] additionalSorts, @NonNull MangaGenre[] genres, @NonNull MangaType[] types, int selectedSort, int selectedAdditionalSort, String[] selectedGenres, String[] selectedTypes) {
		mDataset = new ArrayList<>();
		mSelectedGenres = new SparseBooleanArray();
		mSelectedTypes = new SparseBooleanArray();
		setHasStableIds(true);
		if (sorts.length != 0) {
			mDataset.add(new TypedString(context, R.string.action_sort, ItemViewType.TYPE_ITEM_HEADER, -1));
			for (int i = 0; i < sorts.length; i++) {
				mDataset.add(new TypedString(context, sorts[i], ItemViewType.TYPE_ITEM_SORT, i));
			}
		}
		if (additionalSorts.length != 0) {
			mDataset.add(new TypedString(context, R.string.types, ItemViewType.TYPE_ITEM_HEADER, -1));
			for (int i = 0; i < additionalSorts.length; i++) {
				mDataset.add(new TypedString(context, additionalSorts[i], ItemViewType.TYPE_ITEM_ADDITIONAL_SORT, i));
			}
		}
		mSelectedSort = selectedSort;
		mSelectedAdditionalSort = selectedAdditionalSort;
		if (types.length != 0) {
			mDataset.add(new TypedString(context, R.string.types, ItemViewType.TYPE_ITEM_HEADER, -1));
			for (int i = 0; i < types.length; i++) {
				mDataset.add(new TypedString(context, types[i].nameId, ItemViewType.TYPE_ITEM_TYPE, i));
				if (CollectionsUtils.contains(selectedTypes, types[i].value)) {
					mSelectedTypes.put(i, true);
				}
			}
		}
		if (genres.length != 0) {
			mDataset.add(new TypedString(context, R.string.genres, ItemViewType.TYPE_ITEM_HEADER, -1));
			for (int i = 0; i < genres.length; i++) {
				mDataset.add(new TypedString(context, genres[i].nameId, ItemViewType.TYPE_ITEM_GENRE, i));
				if (CollectionsUtils.contains(selectedGenres, genres[i].value)) {
					mSelectedGenres.put(i, true);
				}
			}
		}
	}

	@NonNull
	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, @ItemViewType int viewType) {
		final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
		RecyclerView.ViewHolder holder;
		switch (viewType) {
			case ItemViewType.TYPE_ITEM_GENRE:
				holder = new GenreHolder(inflater.inflate(R.layout.item_checkable_check, parent, false));
				break;
			case ItemViewType.TYPE_ITEM_TYPE:
				holder = new TypeHolder(inflater.inflate(R.layout.item_checkable_check, parent, false));
				break;
			case ItemViewType.TYPE_ITEM_HEADER:
				return new HeaderHolder(inflater.inflate(R.layout.header_group, parent, false));
			case ItemViewType.TYPE_ITEM_SORT:
				holder = new SortHolder(inflater.inflate(R.layout.item_checkable_radio, parent, false));
				break;
			case ItemViewType.TYPE_ITEM_ADDITIONAL_SORT:
				holder = new AdditionalSortHolder(inflater.inflate(R.layout.item_checkable_radio, parent, false));
				break;
				default:
					throw new AssertionError("");
		}
		return holder;
	}

	@Override
	public void onBindViewHolder(@NotNull RecyclerView.ViewHolder holder, int position) {
		TypedString item = mDataset.get(position);
		if (holder instanceof HeaderHolder) {
			((HeaderHolder) holder).textView.setText(item.toString());
		} else if (holder instanceof GenreHolder) {
			((GenreHolder) holder).checkedTextView.setText(item.toString());
			if (holder instanceof SortHolder) {
				((SortHolder) holder).checkedTextView.setChecked(item.getSubPosition() == mSelectedSort);
			} else if (holder instanceof AdditionalSortHolder){
                ((AdditionalSortHolder) holder).checkedTextView.setChecked(item.getSubPosition() == mSelectedAdditionalSort);
            } else {
				((GenreHolder) holder).checkedTextView.setChecked(mSelectedGenres.get(item.getSubPosition(), false));
			}
		} else if (holder instanceof TypeHolder) {
			((TypeHolder) holder).checkedTextView.setText(item.toString());
			((TypeHolder) holder).checkedTextView.setChecked(mSelectedTypes.get(item.getSubPosition(), false));
		}

	}

	@Override
	public int getItemCount() {
		return mDataset.size();
	}

	@Override
	public long getItemId(int position) {
		return mDataset.get(position).hashCode();
	}

	@ItemViewType
	@Override
	public int getItemViewType(int position) {
		return mDataset.get(position).getType();
	}

	int getSelectedSort() {
		return mSelectedSort;
	}

    int getSelectedAdditionalSort() {
        return mSelectedAdditionalSort;
    }

	SparseBooleanArray getSelectedGenres() {
		return mSelectedGenres;
	}

	SparseBooleanArray getSelectedTypes() {
		return mSelectedTypes;
	}

	void reset() {
		mSelectedSort = 0;
		mSelectedAdditionalSort = 0;
		mSelectedGenres.clear();
		mSelectedTypes.clear();
		notifyDataSetChanged();
	}

	class GenreHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

		final CheckedTextView checkedTextView;

		GenreHolder(View itemView) {
			super(itemView);
			itemView.setOnClickListener(this);
			checkedTextView = itemView.findViewById(R.id.checkedTextView);
			itemView.setTag(1); //some magic
		}

		@Override
		public void onClick(View view) {
			int pos = mDataset.get(getAdapterPosition()).getSubPosition();
			if (mSelectedGenres.get(pos, false)) {
				mSelectedGenres.put(pos, false);
			} else {
				mSelectedGenres.put(pos, true);
			}
			notifyDataSetChanged();
		}
	}

	class TypeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

		final CheckedTextView checkedTextView;

		TypeHolder(View itemView) {
			super(itemView);
			itemView.setOnClickListener(this);
			checkedTextView = itemView.findViewById(R.id.checkedTextView);
			itemView.setTag(2); //some magic
		}

		@Override
		public void onClick(View view) {
			int pos = mDataset.get(getAdapterPosition()).getSubPosition();
			if (mSelectedTypes.get(pos, false)) {
				mSelectedTypes.put(pos, false);
			} else {
				mSelectedTypes.put(pos, true);
			}
			notifyDataSetChanged();
		}
	}

	final class SortHolder extends GenreHolder {

		SortHolder(View itemView) {
			super(itemView);
		}

		@Override
		public void onClick(View view) {
			mSelectedSort = mDataset.get(getAdapterPosition()).getSubPosition();
			notifyDataSetChanged();
		}
	}

    final class AdditionalSortHolder extends GenreHolder {

        AdditionalSortHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void onClick(View view) {
            mSelectedAdditionalSort = mDataset.get(getAdapterPosition()).getSubPosition();
            notifyDataSetChanged();
        }
    }

	static final class HeaderHolder extends RecyclerView.ViewHolder {

		final TextView textView;

		HeaderHolder(View itemView) {
			super(itemView);
			textView = itemView.findViewById(R.id.textView);
		}
	}

	@Retention(RetentionPolicy.SOURCE)
	@IntDef({ItemViewType.TYPE_ITEM_HEADER, ItemViewType.TYPE_ITEM_SORT, ItemViewType.TYPE_ITEM_GENRE, ItemViewType.TYPE_ITEM_TYPE, ItemViewType.TYPE_ITEM_ADDITIONAL_SORT})
	public @interface ItemViewType {
		int TYPE_ITEM_HEADER = 0;
		int TYPE_ITEM_SORT = 1;
		int TYPE_ITEM_GENRE = 2;
		int TYPE_ITEM_TYPE = 3;
        int TYPE_ITEM_ADDITIONAL_SORT = 4;
	}
}
