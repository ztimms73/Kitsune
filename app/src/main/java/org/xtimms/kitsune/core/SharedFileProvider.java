package org.xtimms.kitsune.core;

import androidx.core.content.FileProvider;

import org.xtimms.kitsune.BuildConfig;

public final class SharedFileProvider extends FileProvider {
	public static final String AUTHORITY = BuildConfig.DEBUG ? "org.xtimms.kitsune.debug.files" : "org.xtimms.kitsune.files";
}
