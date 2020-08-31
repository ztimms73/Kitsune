package org.xtimms.kitsune.core.common;

import android.view.View;

import com.nostra13.universalimageloader.core.assist.ImageSize;

import org.jetbrains.annotations.NotNull;

public class ThumbSize {
    public static ThumbSize THUMB_SIZE_LIST;
    public static ThumbSize THUMB_SIZE_SMALL;
    public static ThumbSize THUMB_SIZE_MEDIUM;
    public static ThumbSize THUMB_SIZE_LARGE;

    private final int mWidth;
    private final int mHeight;

    public ThumbSize(int width, int height) {
        mHeight = height;
        mWidth = width;
    }

    public ThumbSize(int width, float aspectRatio) {
        mWidth = width;
        mHeight = Math.round(width * aspectRatio);
    }

    public ThumbSize(View view) {
        mHeight = view.getMeasuredHeight();
        mWidth = view.getMeasuredWidth();
    }

    public int getWidth() {
        return mWidth;
    }

    public int getHeight() {
        return mHeight;
    }

    @NotNull
    @Override
    public String toString() {
        return "_" + mHeight + "x" + mWidth;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof ThumbSize &&
                ((ThumbSize)o).mHeight == mHeight &&
                ((ThumbSize)o).mWidth == mWidth;
    }

    public ImageSize toImageSize() {
        return new ImageSize(mWidth, mHeight);
    }
}
