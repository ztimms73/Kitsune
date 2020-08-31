package org.xtimms.kitsune.core.common.transformers;

import android.view.View;

import androidx.viewpager.widget.ViewPager;

public class DefaultPageTransformer implements ViewPager.PageTransformer {

    @Override
    public void transformPage(View view, float position) {
        view.setTranslationX(view.getWidth() * -position);
        float yPosition = position * view.getHeight();
        view.setTranslationY(yPosition);
    }

}
