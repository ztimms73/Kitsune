package org.xtimms.kitsune.core.common.views.behavior;

import android.content.Context;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@SuppressWarnings("ALL")
abstract class VerticalScrollingBehavior<V extends View> extends CoordinatorLayout.Behavior<V> {

    private int mTotalDyUnconsumed = 0;
    private int mTotalDy = 0;
    @ScrollDirection
    private int mOverScrollDirection = ScrollDirection.SCROLL_NONE;
    @ScrollDirection
    private int mScrollDirection = ScrollDirection.SCROLL_NONE;

    public VerticalScrollingBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VerticalScrollingBehavior() {
        super();
    }

    /*
       @return Overscroll direction: SCROLL_DIRECTION_UP, CROLL_DIRECTION_DOWN, SCROLL_NONE
   */
    @ScrollDirection
    public int getOverScrollDirection() {
        return mOverScrollDirection;
    }

    /**
     * @return Scroll direction: SCROLL_DIRECTION_UP, SCROLL_DIRECTION_DOWN, SCROLL_NONE
     */

    @ScrollDirection
    public int getScrollDirection() {
        return mScrollDirection;
    }

    /**
     * @param coordinatorLayout CoordinatoyLayout parent
     * @param child             View child
     * @param direction         Direction of the overscroll: SCROLL_DIRECTION_UP, SCROLL_DIRECTION_DOWN
     * @param currentOverScroll Unconsumed value, negative or positive based on the direction;
     * @param totalOverScroll   Cumulative value for current direction
     */
    public abstract void onNestedVerticalOverScroll(CoordinatorLayout coordinatorLayout, V child, @ScrollDirection int direction, int currentOverScroll, int totalOverScroll);

    /**
     * @param scrollDirection Direction of the overscroll: SCROLL_DIRECTION_UP, SCROLL_DIRECTION_DOWN
     */
    public abstract void onDirectionNestedPreScroll(CoordinatorLayout coordinatorLayout, V child, View target, int dx, int dy, int[] consumed, @ScrollDirection int scrollDirection);

    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NotNull V child, @NotNull View directTargetChild, @NotNull View target, int nestedScrollAxes) {
        return (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
    }

    @Override
    public void onNestedScrollAccepted(@NonNull CoordinatorLayout coordinatorLayout, @NotNull V child, @NotNull View directTargetChild, @NotNull View target, int nestedScrollAxes) {
        super.onNestedScrollAccepted(coordinatorLayout, child, directTargetChild, target, nestedScrollAxes);
    }

    @Override
    public void onStopNestedScroll(@NotNull CoordinatorLayout coordinatorLayout, @NotNull V child, @NotNull View target) {
        super.onStopNestedScroll(coordinatorLayout, child, target);
    }

    @Override
    public void onNestedScroll(@NotNull CoordinatorLayout coordinatorLayout, @NotNull V child, @NotNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
        if (dyUnconsumed > 0 && mTotalDyUnconsumed < 0) {
            mTotalDyUnconsumed = 0;
            mOverScrollDirection = ScrollDirection.SCROLL_DIRECTION_UP;
        } else if (dyUnconsumed < 0 && mTotalDyUnconsumed > 0) {
            mTotalDyUnconsumed = 0;
            mOverScrollDirection = ScrollDirection.SCROLL_DIRECTION_DOWN;
        }
        mTotalDyUnconsumed += dyUnconsumed;
        onNestedVerticalOverScroll(coordinatorLayout, child, mOverScrollDirection, dyConsumed, mTotalDyUnconsumed);
    }

    @Override
    public void onNestedPreScroll(@NotNull CoordinatorLayout coordinatorLayout, @NotNull V child, @NotNull View target, int dx, int dy, @NotNull int[] consumed) {
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed);
        if (dy > 0 && mTotalDy < 0) {
            mTotalDy = 0;
            mScrollDirection = ScrollDirection.SCROLL_DIRECTION_UP;
        } else if (dy < 0 && mTotalDy > 0) {
            mTotalDy = 0;
            mScrollDirection = ScrollDirection.SCROLL_DIRECTION_DOWN;
        }
        mTotalDy += dy;
        onDirectionNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, mScrollDirection);
    }

    @Override
    public boolean onNestedFling(@NotNull CoordinatorLayout coordinatorLayout, @NotNull V child, @NotNull View target, float velocityX, float velocityY, boolean consumed) {
        super.onNestedFling(coordinatorLayout, child, target, velocityX, velocityY, consumed);
        mScrollDirection = velocityY > 0 ? ScrollDirection.SCROLL_DIRECTION_UP : ScrollDirection.SCROLL_DIRECTION_DOWN;
        return onNestedDirectionFling(coordinatorLayout, child, target, velocityX, velocityY, mScrollDirection);
    }

    protected abstract boolean onNestedDirectionFling(CoordinatorLayout coordinatorLayout, V child, View target, float velocityX, float velocityY, @ScrollDirection int scrollDirection);

    @Override
    public boolean onNestedPreFling(@NotNull CoordinatorLayout coordinatorLayout, @NotNull V child, @NotNull View target, float velocityX, float velocityY) {
        return super.onNestedPreFling(coordinatorLayout, child, target, velocityX, velocityY);
    }

    @NotNull
    @Override
    public WindowInsetsCompat onApplyWindowInsets(@NotNull CoordinatorLayout coordinatorLayout, @NotNull V child, @NotNull WindowInsetsCompat insets) {
        return super.onApplyWindowInsets(coordinatorLayout, child, insets);
    }

    @Override
    public Parcelable onSaveInstanceState(@NotNull CoordinatorLayout parent, @NotNull V child) {
        return super.onSaveInstanceState(parent, child);
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({ScrollDirection.SCROLL_DIRECTION_UP, ScrollDirection.SCROLL_DIRECTION_DOWN})
    public @interface ScrollDirection {
        int SCROLL_DIRECTION_UP = 1;
        int SCROLL_DIRECTION_DOWN = -1;
        int SCROLL_NONE = 0;
    }
}
