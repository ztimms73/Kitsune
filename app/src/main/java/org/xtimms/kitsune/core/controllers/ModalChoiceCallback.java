package org.xtimms.kitsune.core.controllers;

import android.view.ActionMode;

public interface ModalChoiceCallback extends ActionMode.Callback {
    void onChoiceChanged(ActionMode actionMode, ModalChoiceController controller, int count);
}
