package org.xtimms.kitsune.source;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.text.Html;
import android.text.TextUtils;

import org.jetbrains.annotations.NotNull;
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

import java.net.URLEncoder;
import java.util.ArrayList;

public final class MangaReader extends MangaProvider {

    public static final String CNAME = "network/mangareader.net";
    public static final String DNAME = "MangaReader";

    public MangaReader(Context context) {
        super(context);
    }

    @NonNull
    @Override
    public ArrayList<MangaHeader> query(@Nullable String search, int page, int sortOrder, int additionalSortOrder, @NonNull String[] genres, @NonNull String[] types) throws Exception {
        if (!TextUtils.isEmpty(search))
            return simpleSearch(search, page);
        String[] genre = genres;
        if (genres.length == 0)
            genre = null;
        return getList(page, sortOrder, genre);
    }

    private ArrayList<MangaHeader> simpleSearch(@NonNull String search, int page) throws Exception {
        final Element body = NetworkUtils.getDocument("https://www.mangareader.net/search/?w=" + URLEncoder.encode(search, "UTF-8"));
        final Elements elements = body.select("div.mangaresultinner");
        final ArrayList<MangaHeader> list = new ArrayList<>(elements.size());
        for (Element e : elements) {
            final Element a = e.selectFirst("a.thumbnail");
            list.add(new MangaHeader(
                    e.select("h3").first().text(),
                    e.select("div.author_name").first().text(),
                    e.select("div.manga_genre").first().text(),
                    "", //concatUrl("https://www.mangareader.net/", e.select("a").first().attr("href")),
                    e.select("div.imgsearchresults").first().attr("style").substring('\'', '\''),
                    CNAME,
                    MangaStatus.STATUS_UNKNOWN,
                    (short) 0
            ));
        }
        return list;
    }

    protected ArrayList<MangaHeader> getList(int page, int sort, String[] genre) throws Exception {
        final Element body = NetworkUtils.getDocument("https://www.mangareader.net/popular/" + page * 30);
        final Elements elements = body.select("div.mangaresultinner");
        final ArrayList<MangaHeader> list = new ArrayList<>(elements.size());
        for (Element e : elements) {
            final String a = e.select("div.imgsearchresults").first().attr("style");
            list.add(new MangaHeader(
                    e.select("h3").first().text(),
                    e.select("div.author_name").first().text(),
                    e.select("div.manga_genre").first().text(),
                    "", //concatUrl("https://www.mangareader.net/", e.select("a").first().attr("href")),
                    a.substring(a.indexOf('\'') + 1, a.lastIndexOf('\'')),
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
        Element e = doc.body();
        String descr = e.select("table").first().html();
        int p = descr.indexOf(">Tweet");
        if (p > 0)
            descr = descr.substring(0, p);
        String description = Html.fromHtml(descr).toString().trim();
        String preview = e.getElementById("mangaimg").child(0).attr("src");
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
                preview,
                "",
                new MangaChaptersList()
        );
        e = e.getElementById("listing");
        final Elements ch = e.select("a");
        final String domain = NetworkUtils.getDomainWithScheme(header.url);
        final int len = ch.size();
        for (int i = 0; i < len; i++) {
            Element o = ch.get(len - i - 1);
            assert header.provider != null;
            details.chapters.add(0, new MangaChapter(
                    o.text() + o.parent().ownText(),
                    i,
                    "", //concatUrl("https://www.mangareader.net/", o.attr("href")),
                    header.provider,
                    "",
                    0
            ));
        }
        return details;
    }

    @Override
    @NonNull
    public ArrayList<MangaPage> getPages(String chapterUrl) {
        try {
            ArrayList<MangaPage> pages = new ArrayList<>();
            //Document document = getPage(chapterUrl);
            //Element e = document.body().getElementById("selectpage");
            MangaPage page;
            //for (Element o : e.select("option")) {
            //    page = new MangaPage(url("https://www.mangareader.net", o.attr("value")), CNAME);
            //    getCName();
            //    pages.add(page);
            //}
            return pages;
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new RuntimeException("No reader script found");
    }

    @Override
    public String getPageImage(MangaPage mangaPage) {
        return mangaPage.url;
    }

    @NotNull
    @Override
    public String getCName() {
        return MangaReader.CNAME;
    }
}
