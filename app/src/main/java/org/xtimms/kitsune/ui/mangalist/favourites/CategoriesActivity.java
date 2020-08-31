package org.xtimms.kitsune.ui.mangalist.favourites;

import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import org.xtimms.kitsune.core.common.base.AppBaseActivity;
import org.xtimms.kitsune.R;
import org.xtimms.kitsune.core.models.Category;
import org.xtimms.kitsune.core.storage.db.CategoriesRepository;
import org.xtimms.kitsune.core.storage.db.CategoriesSpecification;
import org.xtimms.kitsune.core.storage.settings.ShelfSettings;

import java.util.ArrayList;
import java.util.Objects;

public final class CategoriesActivity extends AppBaseActivity implements View.OnClickListener, DialogInterface.OnClickListener, CategoriesAdapter.OnClickListener {

	private RecyclerView mRecyclerView;
	private CategoriesRepository mRepository;
	private ArrayList<Category> mDataset;
	private CategoriesAdapter mAdapter;
	private AlertDialog mDialog;
	private TextInputLayout mInputLayout;
	private TextInputEditText mEditName;
	private int mCurrentCategoryIndex;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_categories);
		setSupportActionBar(R.id.toolbar);
		enableHomeAsUp();

		mRepository = CategoriesRepository.get(this);
		mDataset = mRepository.query(new CategoriesSpecification().orderByDate(false));
		mAdapter = new CategoriesAdapter(mDataset, this);

		mRecyclerView = findViewById(R.id.recyclerView);
		mRecyclerView.setHasFixedSize(true);
		mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
		mRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
		mRecyclerView.setAdapter(mAdapter);

		final View view = getLayoutInflater().inflate(R.layout.dialog_category_name, mRecyclerView, false);
		mInputLayout = view.findViewById(R.id.inputLayout);
		mEditName = view.findViewById(R.id.edit_name);
		mDialog = new AlertDialog.Builder(this)
				.setView(view)
				.setNegativeButton(android.R.string.cancel, null)
				.setPositiveButton(R.string.rename, this)
				.create();

		findViewById(R.id.fav_add).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		mCurrentCategoryIndex = -1;
		mEditName.setText(null);
		mInputLayout.setError(null);
		mDialog.setTitle(R.string.create_new_category);
		mDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.create), this);
		mDialog.show();
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		final String name = Objects.requireNonNull(mEditName.getText()).toString();
		if (mCurrentCategoryIndex == -1) {
			final Category category = new Category(name, System.currentTimeMillis());
			mRepository.add(category);
			mDataset.add(category);
			ShelfSettings.onCategoryAdded(this, category);
			mAdapter.notifyItemInserted(mDataset.size() - 1);
			setResult(RESULT_OK);
		}  //TODO rename
		// final Category category = mDataset.get(mCurrentCategoryIndex);
		// setResult(RESULT_OK);

	}

	@Override
	public void onItemClick() {
		stub();
	}

	@Override
	public void onItemActionClick(int position) {
		if (mDataset.size() == 1) {
			new AlertDialog.Builder(this)
					.setMessage(R.string.favourites_category_must_be)
					.setPositiveButton(android.R.string.ok, null)
					.create()
					.show();
			return;
		}
		final Category category = mDataset.get(position);
		new AlertDialog.Builder(this)
				.setMessage(getString(R.string.category_remove_confirm, category.name))
				.setNegativeButton(android.R.string.cancel, null)
				.setPositiveButton(R.string.remove, (dialog, which) -> {
					mRepository.remove(category);
					mDataset.remove(category);
					mAdapter.notifyDataSetChanged();
					Snackbar.make(mRecyclerView, getString(R.string.category_x_removed, category.name), Snackbar.LENGTH_SHORT)
							.show();
					setResult(RESULT_OK);
				})
				.create()
				.show();
	}
}
