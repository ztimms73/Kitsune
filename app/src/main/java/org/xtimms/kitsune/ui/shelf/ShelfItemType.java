package org.xtimms.kitsune.ui.shelf;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@IntDef({ShelfItemType.TYPE_ITEM_DEFAULT, ShelfItemType.TYPE_ITEM_SMALL, ShelfItemType.TYPE_HEADER, ShelfItemType.TYPE_TIP, ShelfItemType.TYPE_RECENT, ShelfItemType.TYPE_SAVED})
public @interface ShelfItemType {
	int TYPE_ITEM_DEFAULT = 0;
	int TYPE_ITEM_SMALL = 1;
	int TYPE_TIP = 2;
	int TYPE_HEADER = 3;
	int TYPE_RECENT = 4;
	int TYPE_SAVED = 5;
}