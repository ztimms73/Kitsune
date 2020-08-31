package org.xtimms.kitsune.core.common.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import org.xtimms.kitsune.core.common.transformers.DefaultPageTransformer;
import org.xtimms.kitsune.ui.reader.pager.OverScrollPager;

public class VerticalViewPager extends OverScrollPager {

    private float lastX;
    private float lastY;

    public VerticalViewPager(Context context) {
        this(context, null);
    }

    public VerticalViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        setPageTransformer(false, new DefaultPageTransformer());
    }

    private MotionEvent swapTouchEvent(MotionEvent event) {
        float width = getWidth();
        float height = getHeight();

        float swappedX = (event.getY() / height) * width;
        float swappedY = (event.getX() / width) * height;

        event.setLocation(swappedX, swappedY);

        return event;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        boolean isVertical = false;
        boolean intercept = false;
        try {
            float x = event.getX();
            float y = event.getY();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    lastX = x;
                    lastY = y;
                    break;
                case MotionEvent.ACTION_MOVE:
                    float dx = x - lastX;
                    float dy = y - lastY;
                    if (Math.abs(dx) < Math.abs(dy)) {
                        isVertical = true;
                    }
                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    lastX = lastY = 0;
                    break;

            }
            intercept = super.onInterceptTouchEvent(swapTouchEvent(event));
            //If not intercept, touch event should not be swapped.
            swapTouchEvent(event);
        } catch (IllegalArgumentException e) {
            //do nothing
        }
        return intercept || isVertical;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return super.onTouchEvent(swapTouchEvent(ev));
    }

}