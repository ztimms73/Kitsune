package org.xtimms.kitsune.source;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xtimms.kitsune.core.MangaStatus;
import org.xtimms.kitsune.core.common.StringJoinerCompat;
import org.xtimms.kitsune.core.models.MangaChapter;
import org.xtimms.kitsune.core.models.MangaChaptersList;
import org.xtimms.kitsune.core.models.MangaDetails;
import org.xtimms.kitsune.core.models.MangaHeader;
import org.xtimms.kitsune.core.models.MangaPage;
import org.xtimms.kitsune.utils.network.NetworkUtils;

import java.util.ArrayList;

public class Anibel extends MangaProvider {

    public static final String CNAME = "network/anibel";
    public static final String DNAME = "Anibel";

    public Anibel(Context context) {
        super(context);
    }

    @NonNull
    @Override
    public ArrayList<MangaHeader> query(@Nullable String search, int page, int sortOrder, int additionalSortOrder, @NonNull String[] genres, @NonNull String[] types) throws Exception {
        return getList(page);
    }

    protected ArrayList<MangaHeader> getList(int page) throws Exception {
        final Element body = NetworkUtils.getDocument("https://anibel.net/manga/?page=" + (page + 1));
        final Elements elements = body.select("div.anime-card");
        final ArrayList<MangaHeader> list = new ArrayList<>(elements.size());
        for (Element e: elements) {
            final Element title = e.selectFirst("h1.anime-card-title");
            /*final Element summary = e.select("p.view").get(0);
            final Element thumbnail = e.selectFirst("img");
            final Element status = e.select("p.view").get(1);*/
            list.add(new MangaHeader(
                    title.text(),
                    "",
                    parseGenres(e.select("p.tupe.tag")),
                    url("https://anibel.net", e.select("a").attr("href")),
                    url("https://anibel.net", e.select("img").first().attr("data-src")),
                    CNAME,
                    parseStatus(e.select("tr").get(2).text()),
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
        Element e = doc.body();
        String description = e.select("div.manga-block.grid-12").select("p").text();
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
                description,
                header.thumbnail,
                header.summary,
                new MangaChaptersList()
        );
        e = e.selectFirst("ul.series");
        final Elements ch = e.select("li");
        final int len = ch.size();
        for (int i = 0; i < len; i++) {
            Element o = ch.get(len - i - 1);
            details.chapters.add(new MangaChapter(
                    o.select("a").first().text(),
                    i,
                    url("https://anibel.net", o.select("a").first().attr("href")),
                    header.provider,
                    "",
                    0L
            ));
        }
        return details;
    }

    @NonNull
    @Override
    public ArrayList<MangaPage> getPages(String chapterUrl) throws Exception {
        Document document = getPage(chapterUrl);
        Element e = document.body().selectFirst("ul.images");
        ArrayList<MangaPage> pages = new ArrayList<>();
        for (Element o : e.select("li")) {
            pages.add(new MangaPage(
                    o.select("img").first().attr("src"),
                    CNAME
            ));
        }
        return pages;
    }

    @Override
    public String getPageImage(MangaPage mangaPage) {
        return null;
    }

    @NonNull
    private String parseGenres(@Nullable Elements elements) {
        if (elements == null || elements.isEmpty()) {
            return "";
        }
        StringJoinerCompat joiner = new StringJoinerCompat(", ");
        for (Element o : elements) {
            joiner.add(o.text());
        }
        return joiner.toString();
    }

    private int parseStatus(String element) {
        int status = MangaStatus.STATUS_UNKNOWN;
        if (element.contains("завершанае")) {
            status = MangaStatus.STATUS_COMPLETED;
        } else if (element.contains("выпускаецца")) {
            status = MangaStatus.STATUS_ONGOING;
        }
        return status;
    }
}
