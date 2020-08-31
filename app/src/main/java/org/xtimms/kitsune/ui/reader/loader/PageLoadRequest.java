package org.xtimms.kitsune.ui.reader.loader;

import org.xtimms.kitsune.core.models.MangaPage;

import java.io.File;

final class PageLoadRequest {

	public final MangaPage page;
	public final File destination;

	PageLoadRequest(MangaPage page, File destination) {
		this.page = page;
		this.destination = destination;
	}
}
