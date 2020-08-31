package org.xtimms.kitsune.ui.tools;

final class StorageStats {

	public long cacheSize;
	public long savedSize;
	public long otherSize;

	public long total() {
		return cacheSize + savedSize + otherSize;
	}
}
