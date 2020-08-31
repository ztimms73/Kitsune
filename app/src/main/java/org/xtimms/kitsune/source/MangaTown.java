package org.xtimms.kitsune.source;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xtimms.kitsune.R;
import org.xtimms.kitsune.core.MangaStatus;
import org.xtimms.kitsune.core.models.MangaChapter;
import org.xtimms.kitsune.core.models.MangaChaptersList;
import org.xtimms.kitsune.core.models.MangaDetails;
import org.xtimms.kitsune.core.models.MangaHeader;
import org.xtimms.kitsune.core.models.MangaPage;
import org.xtimms.kitsune.utils.network.NetworkUtils;

import java.net.URLEncoder;
import java.util.ArrayList;

@SuppressWarnings("ALL")
public class MangaTown extends MangaProvider {

    public static final String CNAME = "network/mangatown.com";
    public static final String DNAME = "MangaTown";

    public MangaTown(Context context) {
        super(context);
    }

    private final int[] mSorts = new int[] {
            R.string.sort_alphabetical,
            R.string.sort_popular,
            R.string.sort_rating,
            R.string.sort_updated
    };

    private final String[] mSortValues = {
            "?name.az",
            "",
            "?rating.za",
            "?last_chapter_time.za"
    };

    @NonNull
    @Override
    public ArrayList<MangaHeader> query(@Nullable String search, int page, int sortOrder, int additionalSortOrder, @NonNull String[] genres, @NonNull String[] types) throws Exception {
        boolean hasQuery = !TextUtils.isEmpty(search);
        if (hasQuery) {
            return searchList(search, page);
        }
        String[] genre = genres;
        if (genres.length == 0)
            genre = null;
        return getList(page, sortOrder, genre);
    }

    protected ArrayList<MangaHeader> getList(int page, int sort, String[] genres) throws Exception {
        final Element body = NetworkUtils.getDocument("http://www.mangatown.com/directory/"
                + (page + 1) + ".htm"
                + "?"
                + mSortValues[sort]);
        final Elements elements = body.select("ul.manga_pic_list li");
        final ArrayList<MangaHeader> list = new ArrayList<>(elements.size());
        for (Element e: elements) {
            final Element title = e.select("p.title").first();
            final Element summary = e.select("p.view").get(0);
            final Element thumbnail = e.selectFirst("img");
            final Element status = e.select("p.view").get(1);
            list.add(new MangaHeader(
                    title.text(),
                    summary.text().replace("Author: ", ""),
                    "",
                    url("http://mangatown.com", e.select("a").first().attr("href")),
                    thumbnail == null ? "" : thumbnail.attr("src"),
                    CNAME,
                    parseStatus(status.text()),
                    (short) parseRating(e.select("p").get(1).text())
            ));
        }
        return list;
    }

    @SuppressLint({"DefaultLocale"})
    @NonNull
    protected ArrayList<MangaHeader> searchList(@NonNull String search, int page) throws Exception {
        if (page > 0) {
            return EMPTY_HEADERS;
        }
        final Element body = NetworkUtils.getDocument("http://www.mangatown.com/search.php?name=" + URLEncoder.encode(search, "UTF-8") + "");
        Elements ul = body.select("ul.manga_pic_list");
        ArrayList<MangaHeader> arrayList = new ArrayList<>(ul.size());
        for (Element o : ul.select("li")) {
            Element el = o.select("a").first();
            final String title = el.attr("title");
            final Element thumbnail = el.selectFirst("img");
            final Element status = o.select("p.view").get(1);
            arrayList.add(new MangaHeader(
                    title,
                    o.select("p.view").get(0).text().replace("Author: ", ""),
                    "",
                    url("http://mangatown.com", o.select("a").first().attr("href")),
                    thumbnail == null ? "" : thumbnail.attr("src"),
                    CNAME,
                    parseStatus(status.text()),
                    (short)parseRating(o.select("p").get(1).text())
            ));
        }
        return arrayList;
    }

    private short parseRating(String title) {
        try {
            return (Short.parseShort(title.substring(0, title.indexOf('.') + 2).replace(".", "")));
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private int parseStatus(String s) {
        int status = MangaStatus.STATUS_UNKNOWN;
        if (s.contains("Status: Completed")) {
            status = MangaStatus.STATUS_COMPLETED;
        } else if (s.contains("Status: Ongoing")) {
            status = MangaStatus.STATUS_ONGOING;
        }
        return status;
    }

    @NonNull
    @Override
    public MangaDetails getDetails(MangaHeader header) throws Exception {
        assert header.url != null;
        final Document doc = NetworkUtils.getDocument(header.url);
        Element e = doc.body();
        String description = doc.select("span#show").text().replace("HIDE", "");
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
        e = e.selectFirst("ul.chapter_list");
        final Elements ch = e.select("li");
        final int len = ch.size();
        for (int i = 0; i < len; i++) {
            Element o = ch.get(len - i - 1);
            details.chapters.add(new MangaChapter(
                    o.select("a").first().text() + " " + o.select("span").get(0).text(),
                    i,
                    url("http://mangatown.com/", o.select("a").first().attr("href")),
                    header.provider,
                    "",
                    0L
            ));
        }
        return details;
    }

    private static String appendProtocol(String protocol, String url) {
        return null != url && url.startsWith("//") ? protocol + url : url;
    }

    @NonNull
    @Override
    public ArrayList<MangaPage> getPages(String chapterUrl) {
        ArrayList<MangaPage> pages = new ArrayList<>();
        try {
            Document document = getPage(chapterUrl);
            Element e = document.body().selectFirst("div.page_select");
            for (Element o : e.selectFirst("select").select("option")) {
                String href = o.attr("value");
                /*if (href.endsWith("featured.html")) {
                    return null;
                }*/
                pages.add(new MangaPage(
                        href.hashCode(),
                        href,
                        CNAME
                ));
            }
            return pages;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @NonNull
    @Override
    public String getImageUrl(@NotNull @NonNull MangaPage page) {
        try {
            Document document = getPage(page.url);
            return document.body().getElementById("image").attr("src");
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String getPageImage(MangaPage mangaPage) {
        return mangaPage.url;
    }

    @Override
    public int[] getAvailableSortOrders() {
        return mSorts;
    }

}
