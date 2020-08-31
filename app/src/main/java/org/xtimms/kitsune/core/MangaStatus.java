package org.xtimms.kitsune.core;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@IntDef({MangaStatus.STATUS_UNKNOWN, MangaStatus.STATUS_COMPLETED, MangaStatus.STATUS_ONGOING, MangaStatus.STATUS_LICENSED})
public @interface MangaStatus {

	int STATUS_UNKNOWN = 0;
	int STATUS_COMPLETED = 1;
	int STATUS_ONGOING = 2;
	int STATUS_LICENSED = 3;
}
