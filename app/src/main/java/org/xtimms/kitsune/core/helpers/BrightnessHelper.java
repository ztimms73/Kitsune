package org.xtimms.kitsune.core.helpers;

import android.view.Window;
import android.view.WindowManager;

public class BrightnessHelper {

    private final Window mWindow;
    private final float mLastValue;

    public BrightnessHelper(Window window) {
        this.mWindow = window;
        mLastValue = mWindow.getAttributes().screenBrightness;
    }

    public void setBrightness(int percent) {
        WindowManager.LayoutParams lp = mWindow.getAttributes();
        lp.screenBrightness = percent / 100f;
        mWindow.setAttributes(lp);
    }

    public void reset() {
        WindowManager.LayoutParams lp = mWindow.getAttributes();
        lp.screenBrightness = mLastValue;
        mWindow.setAttributes(lp);
    }
}
