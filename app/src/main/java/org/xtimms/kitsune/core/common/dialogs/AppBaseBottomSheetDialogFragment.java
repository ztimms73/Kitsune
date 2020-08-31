package org.xtimms.kitsune.core.common.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.xtimms.kitsune.utils.ThemeUtils;

public abstract class AppBaseBottomSheetDialogFragment extends AppCompatDialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Context context = getContext();
        assert context != null;
        return new BottomSheetDialog(context, ThemeUtils.getBottomSheetTheme(getContext()));
    }
}
