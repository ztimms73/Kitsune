package org.xtimms.kitsune.source;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xtimms.kitsune.utils.network.NetworkUtils;
import org.xtimms.kitsune.core.MangaStatus;
import org.xtimms.kitsune.core.models.MangaChapter;
import org.xtimms.kitsune.core.models.MangaChaptersList;
import org.xtimms.kitsune.core.models.MangaDetails;
import org.xtimms.kitsune.core.models.MangaHeader;
import org.xtimms.kitsune.core.models.MangaPage;

import java.util.ArrayList;

/**
 * TODO
 */
@SuppressWarnings("ALL")
public final class MangaRaw extends MangaProvider {

	public static final String CNAME = "network/mangaraw";
	public static final String DNAME = "MangaRaw";

	public MangaRaw(Context context) {
		super(context);
	}

	@NonNull
	@Override
	@SuppressLint("DefaultLocale")
	public ArrayList<MangaHeader> query(@Nullable String search, int page, int sortOrder, int additionalSortOrder, @NonNull String[] genres, @NonNull String[] types) throws Exception {
		final Element body = NetworkUtils.getDocument(String.format("http://mangaraw.xyz/popular-manga?page=%d", page+1));
		final Elements elements = body.select("div.anipost");
		final ArrayList<MangaHeader> list = new ArrayList<>(elements.size());
		for (Element e : elements) {
			final Element a = e.selectFirst("a.thumbnail");
			list.add(new MangaHeader(
					e.select("h3.title_mg").text(),
					"",
					"",
					"",//url("http://mangaraw.xyz", a.attr("href")),
					concatUrl("http:", e.selectFirst("img").text()),//"http:" + e.selectFirst("img").text(),//url("http://mangaraw.xyz",a.selectFirst("img").attr("src")),
					CNAME,
					MangaStatus.STATUS_UNKNOWN,
					(short) 0
			));
		}
		return list;
	}

	@NonNull
	@Override
	public MangaDetails getDetails(MangaHeader header) throws Exception {
		assert header.url != null;
		final Document doc = NetworkUtils.getDocument(header.url);
		Element root = doc.body();
		final Element dlh = root.selectFirst(".dl-horizontal");
		final MangaDetails details = new MangaDetails(
				header.id,
				header.name,
				header.summary,
				header.genres,
				header.url,
				header.thumbnail,
				header.provider,
				header.status,
				header.rating,
				"",
				root.selectFirst(".img-responsive").attr("src"),
				"",
				new MangaChaptersList()
		);
		root = root.selectFirst("ul.chapters");
		final Elements ch = root.select("li h5 a");
		final String domain = "http://mangaraw.xyz";
		final int len = ch.size();
		for (int i = 0; i < len; i++) {
			Element o = ch.get(len - i - 1);
			details.chapters.add(new MangaChapter(
					o.text(),
					i,
					url(domain, o.attr("href")),
					header.provider,
					"",
					0
			));
		}
		return details;
	}

	@NonNull
	@Override
	public ArrayList<MangaPage> getPages(String chapterUrl) {
		return null;
	}

	@Override
	public String getPageImage(MangaPage mangaPage) {
		return mangaPage.url;
	}

}
