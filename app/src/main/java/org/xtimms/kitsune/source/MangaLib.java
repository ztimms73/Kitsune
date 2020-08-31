package org.xtimms.kitsune.source;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONObject;
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

public class MangaLib extends MangaProvider {

    public static final String CNAME = "network/mangalib.me";
    public static final String DNAME = "MangaLib";

    public MangaLib(Context context) {
        super(context);
    }

    @NonNull
    @Override
    public ArrayList<MangaHeader> query(@Nullable String search, int page, int sortOrder, int additionalSortOrder, @NonNull String[] genres, @NonNull String[] types) throws Exception {
        return getList(page, sortOrder, genres);
    }

    @NonNull
    @Override
    public MangaDetails getDetails(MangaHeader header) throws Exception {
        assert header.url != null;
        final Document doc = NetworkUtils.getDocument(header.url);
        Element body = doc.select("div.section__body").first();
        String description = body.select(".info-desc__content").text();
        String thumbnail = body.select(".manga__cover").attr("src");
        String author = body.select(".info-list__row:nth-child(2) > a").text();
        String summary = body.select("h4.manga-bg__subtitle").text();
        assert header.genres != null;
        final MangaDetails details = new MangaDetails(
                header.id,
                header.name,
                summary,
                parseGenres(body.select("a.link-default"), header.genres),
                header.url,
                header.thumbnail,
                header.provider,
                header.status,
                header.rating,
                description,
                thumbnail,
                author,
                new MangaChaptersList()
        );
        body = doc.selectFirst("div.chapters-list");
        final Elements ch = body.select("div.chapter-item__name > a");
        final int len = ch.size();
        for (int i = 0; i < len; i++) {
            Element o = ch.get(len - i - 1);
            details.chapters.add(new MangaChapter(
                    o.attr("data-volume"),
                    len - i,
                    o.attr("href"),
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
        Elements scripts = document.head().select("script");
        ArrayList<MangaPage> list = new ArrayList<>();
        for (Element script : scripts) {
            String raw = script.html().trim();
            String pageUrl = "";
            if (raw.startsWith("window.__info")) {
                JSONObject json = new JSONObject(raw.substring(raw.indexOf("=") + 1, raw.indexOf(";") + 1));
                String domain = json.getJSONObject("servers").getString("main");
                String url = json.getJSONObject("img").getString("url");
                pageUrl = url(domain, url);
            }
            list.add(new MangaPage(
                    pageUrl,
                    CNAME
            ));
        }
        return list;
    }

    @Override
    public String getPageImage(MangaPage mangaPage) {
        return mangaPage.url;
    }

    protected ArrayList<MangaHeader> getList(int page, int sort, String[] genre) throws Exception {
        final Element body = NetworkUtils.getDocument("https://mangalib.me/manga-list?dir=desc&sort=views&types[]=1&page=" + page);
        final Elements elements = body.selectFirst("div.media-cards-grid").select("div.media-card-wrap");
        final ArrayList<MangaHeader> list = new ArrayList<>(elements.size());
        for (Element e : elements) {
            final Element a = e.selectFirst("a.media-card");
            list.add(new MangaHeader(
                    e.select("h3").first().text(),
                    "",
                    "",
                    concatUrl("https://mangalib.me/", a.attr("href")),
                    concatUrl("https://mangalib.me/", a.attr("data-src")),
                    CNAME,
                    MangaStatus.STATUS_UNKNOWN,
                    (short) 0
            ));
        }
        return list;
    }

    @NonNull
    private String parseGenres(@Nullable Elements elements, @NonNull String defValue) {
        if (elements == null || elements.isEmpty()) {
            return defValue;
        }
        StringJoinerCompat joiner = new StringJoinerCompat(", ");
        for (Element o : elements) {
            joiner.add(o.text());
        }
        return joiner.toString();
    }
}
