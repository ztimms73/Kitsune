package org.xtimms.kitsune.source;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@StringDef({
		BoredomSociety.CNAME,
		Desu.CNAME,
		ReadManga.CNAME,
		MintManga.CNAME,
		SelfManga.CNAME,
		MangaReader.CNAME,
		MangaFox.CNAME,
		MangaRaw.CNAME,
		MangaChan.CNAME,
		Remanga.CNAME,
		MangaTown.CNAME,
		MangaLib.CNAME,
		Anibel.CNAME
})
public @interface CName {
}
