package org.xtimms.kitsune.ui.reader;

import android.content.Context;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import android.util.AttributeSet;

import org.xtimms.kitsune.utils.ThemeUtils;

public final class ToolButtonCompat extends AppCompatImageView {

	public ToolButtonCompat(Context context) {
		this(context, null, 0);
	}

	public ToolButtonCompat(Context context, @Nullable AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ToolButtonCompat(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		setBackgroundDrawable(ThemeUtils.getSelectableBackgroundBorderless(context));
		setScaleType(ScaleType.CENTER);
	}
}
