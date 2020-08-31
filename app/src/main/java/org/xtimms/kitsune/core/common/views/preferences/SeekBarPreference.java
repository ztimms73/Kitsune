package org.xtimms.kitsune.core.common.views.preferences;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.preference.Preference;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatSeekBar;

import org.xtimms.kitsune.R;

@SuppressWarnings("ALL")
public final class SeekBarPreference extends Preference implements SeekBar.OnSeekBarChangeListener,
        IntegerPreference {

    @Nullable
    private final Drawable mIcon;
    private final int mMax;
    private final String mSummaryPattern;
    private int mValue;
    private boolean mValueSet = false;

    public SeekBarPreference(Context context) {
        this(context, null, 0);
    }

    public SeekBarPreference(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SeekBarPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setLayoutResource(R.layout.pref_seekbar);
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.SeekBarPreferenceAttrs);
        mIcon = a.getDrawable(R.styleable.SeekBarPreferenceAttrs_iconDrawable);
        mMax = a.getInt(R.styleable.SeekBarPreferenceAttrs_max, 100);
        mSummaryPattern = a.getString(R.styleable.SeekBarPreferenceAttrs_summaryPattern);
        a.recycle();
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        setValue(restorePersistedValue ? getPersistedInt(mValue)
                : (Integer) defaultValue);
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        final AppCompatSeekBar seekBar = view.findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(this);
        seekBar.setMax(mMax);
        seekBar.setProgress(mValue);
        view.<AppCompatImageView>findViewById(R.id.icon).setImageDrawable(mIcon);
        view.<TextView>findViewById(R.id.title).setText(getTitle());
        view.<TextView>findViewById(R.id.value).setText(TextUtils.isEmpty(mSummaryPattern) ? String.valueOf(mValue) :
                String.format(mSummaryPattern, mValue));
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            setValue(progress);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public int getValue() {
        return mValue;
    }

    private void setValue(int value) {
        final boolean changed = mValue != value;
        if (changed || !mValueSet) {
            mValue = value;
            mValueSet = true;
            persistInt(value);
            if (changed) {
                notifyChanged();
            }
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInt(index, 0);
    }
}
