package org.xtimms.kitsune.ui.shelf;

import androidx.annotation.IdRes;

public interface OnTipsActionListener {

	void onTipActionClick(@IdRes int actionId);

	void onTipDismissed(@IdRes int actionId);
}
