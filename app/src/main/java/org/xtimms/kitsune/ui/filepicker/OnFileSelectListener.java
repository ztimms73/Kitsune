package org.xtimms.kitsune.ui.filepicker;

import androidx.annotation.NonNull;

import java.io.File;

interface OnFileSelectListener {

	void onFileSelected(@NonNull File file);
}
