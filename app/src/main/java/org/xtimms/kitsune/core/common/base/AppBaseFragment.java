package org.xtimms.kitsune.core.common.base;

import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;

@SuppressWarnings("deprecation")
public abstract class AppBaseFragment extends Fragment {

    protected View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @LayoutRes int resource) {
        return inflater.inflate(resource, container, false);
    }

    public void scrollToTop() {
    }
}
