package org.xtimms.kitsune.core.common.transformers;

import android.view.View;

import androidx.viewpager.widget.ViewPager;

public class VerticalPageTransformer implements ViewPager.PageTransformer {

    @Override
    public void transformPage(View view, float position) {
        final int pageWidth = view.getWidth();
        final int pageHeight = view.getHeight();
        if (position < -1) {
            // This page is way off-screen to the left.
            view.setAlpha(0);
        } else if (position <= 1) {
            view.setAlpha(1);
            // Counteract the default slide transition
            view.setTranslationX(pageWidth * -position);
            // set Y position to swipe in from top
            float yPosition = position * pageHeight;
            view.setTranslationY(yPosition);
        } else {
            // This page is way off-screen to the right.
            view.setAlpha(0);
        }
    }

}
