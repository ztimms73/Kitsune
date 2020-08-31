package org.xtimms.kitsune.core.common.base;

import android.view.ViewGroup;
import android.view.Window;

import androidx.appcompat.app.AppCompatDialogFragment;

import java.util.Objects;

public abstract class AppBaseDialogFragment extends AppCompatDialogFragment {

    @Override
    public void onResume() {
        super.onResume();
        Window window = Objects.requireNonNull(getDialog()).getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }
}
