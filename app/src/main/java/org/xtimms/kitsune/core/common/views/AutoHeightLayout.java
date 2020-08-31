package org.xtimms.kitsune.core.common.views;

import android.content.Context;
import android.util.AttributeSet;

public class AutoHeightLayout extends FrameCheckLayout {

    private double mAspectRatio = 18f / 13f;

    public AutoHeightLayout(Context context) {
        super(context);
    }

    public AutoHeightLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AutoHeightLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int originalWidth = MeasureSpec.getSize(widthMeasureSpec);
        int calculatedHeight = (int) (originalWidth * mAspectRatio);
        super.onMeasure(
                MeasureSpec.makeMeasureSpec(originalWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(calculatedHeight, MeasureSpec.EXACTLY)
        );
    }
}