package org.xtimms.kitsune.source;

import org.xtimms.kitsune.core.models.MangaHeader;

import java.util.ArrayList;

public interface RelativeMangaProvider {

	ArrayList<MangaHeader> getRelativeManga(MangaHeader manga);
}
