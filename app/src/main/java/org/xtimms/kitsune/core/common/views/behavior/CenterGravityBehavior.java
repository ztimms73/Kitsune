package org.xtimms.kitsune.core.common.views.behavior;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;

import com.google.android.material.appbar.AppBarLayout;

import org.jetbrains.annotations.NotNull;

@SuppressWarnings("deprecation")
public final class CenterGravityBehavior extends CoordinatorLayout.Behavior {

    private int scrollY = 0;
    private int maxScroll = 0;

    public CenterGravityBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child,
                                       @NonNull View directTargetChild, @NonNull View target, int nestedScrollAxes) {
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean onDependentViewChanged(@NotNull CoordinatorLayout parent, @NotNull View child, @NotNull View dependency) {
        handleScroll(parent, child, 0);
        return super.onDependentViewChanged(parent, child, dependency);
    }

    @Override
    public boolean layoutDependsOn(@NotNull CoordinatorLayout parent, @NotNull View child, @NotNull View dependency) {
        return dependency instanceof AppBarLayout;
    }

    @Override
    public void onNestedPreScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child,
                                  @NonNull View target, int dx, int dy, @NonNull int[] consumed) {
        handleScroll(coordinatorLayout, child, dy);
    }

    private void handleScroll(CoordinatorLayout coordinatorLayout, View child, int dy) {
        if (maxScroll == 0) {
            for (int i = 0; i < coordinatorLayout.getChildCount(); i++) {
                View o = coordinatorLayout.getChildAt(i);
                if (o instanceof AppBarLayout) {
                    maxScroll = ((AppBarLayout) o).getTotalScrollRange();
                    break;
                }
            }
        }
        if (maxScroll == 0) {
            return;
        }

        scrollY += dy;
        if (scrollY > maxScroll) {
            scrollY = maxScroll;
        } else if (scrollY < 0) {
            scrollY = 0;
        }
        int offset = (scrollY - maxScroll) / 2;
        child.setTranslationY(offset);
    }
}
