package org.xtimms.kitsune.core.common.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;

import org.xtimms.kitsune.R;
import org.xtimms.kitsune.utils.AnimationUtils;

public class FrameCheckLayout extends FrameLayout implements ExtraCheckable {

    private boolean mChecked;
    private static int mPadding;
    private ImageView mCheckMark;

    public FrameCheckLayout(Context context) {
        super(context);
        init();
    }

    public FrameCheckLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FrameCheckLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public FrameCheckLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        mChecked = false;
        if (mPadding == 0) {
            mPadding = getResources().getDimensionPixelOffset(R.dimen.padding8);
        }
    }

    @Override
    public void setChecked(boolean checked) {
        if (mChecked == checked) {
            return;
        }
        mChecked = checked;
        if (mChecked) {
            getCheckMark().setVisibility(VISIBLE);
        } else {
            getCheckMark().setVisibility(View.GONE);
        }
    }


    @Override
    public void setCheckedAnimated(boolean checked) {
        if (mChecked == checked) {
            return;
        }
        mChecked = checked;
        if (mChecked) {
            AnimationUtils.crossfade(null, getCheckMark());
        } else {
            AnimationUtils.crossfade(getCheckMark(), null);
        }
    }

    private View getCheckMark() {
        if (mCheckMark == null) {
            mCheckMark = new ImageView(getContext());
            mCheckMark.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            mCheckMark.setPadding(mPadding, mPadding, mPadding, mPadding);
            mCheckMark.setScaleType(ImageView.ScaleType.MATRIX);
            //mCheckMark.setImageResource(R.drawable.ic_checkmark);
            mCheckMark.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white_overlay_85));
            mCheckMark.setVisibility(GONE);
            ViewCompat.setElevation(mCheckMark, 10000);
            addView(mCheckMark);
        }
        return mCheckMark;
    }

    @Override
    public boolean isChecked() {
        return mChecked;
    }

    @Override
    public void toggle() {
        setChecked(!mChecked);
    }
}
