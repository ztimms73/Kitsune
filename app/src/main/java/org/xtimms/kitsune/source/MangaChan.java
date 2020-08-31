package org.xtimms.kitsune.source;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xtimms.kitsune.R;
import org.xtimms.kitsune.core.common.StringJoinerCompat;
import org.xtimms.kitsune.utils.network.NetworkUtils;
import org.xtimms.kitsune.core.MangaStatus;
import org.xtimms.kitsune.core.models.MangaChapter;
import org.xtimms.kitsune.core.models.MangaChaptersList;
import org.xtimms.kitsune.core.models.MangaDetails;
import org.xtimms.kitsune.core.models.MangaGenre;
import org.xtimms.kitsune.core.models.MangaHeader;
import org.xtimms.kitsune.core.models.MangaPage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public final class MangaChan extends MangaProvider {

    public static final String CNAME = "network/manga-chan.me";
    public static final String DNAME = "Манга-тян";

    @NonNull
    private final String mDomain = "http://manga-chan.me";

    private static String sAuthCookie = null;

    private final int[] mSorts = new int[] {
            R.string.sort_popular,
            R.string.sort_alphabetical,
            R.string.sort_latest,
            R.string.chapters_count
    };

    private final String[] mSortValues = {
            "favdesc",
            "abcasc",
            "datedesc",
            "chdesc"
    };

    private final MangaGenre[] mGenres = new MangaGenre[]{
            new MangaGenre(R.string.genre_art, "арт"),
            new MangaGenre(R.string.genre_action, "боевик"),
            new MangaGenre(R.string.genre_martialarts, "боевые_искусства"),
            new MangaGenre(R.string.genre_vampires, "вампиры"),
            new MangaGenre(R.string.web, "веб"),
            new MangaGenre(R.string.genre_harem, "гарем"),
            new MangaGenre(R.string.genre_genderbender, "гендерная_интрига"),
            new MangaGenre(R.string.genre_hero_fantasy, "героическое_фэнтези"),
            new MangaGenre(R.string.genre_detective, "детектив"),
            new MangaGenre(R.string.genre_josei, "дзёсэй"),
            new MangaGenre(R.string.genre_doujinshi, "додзинси"),
            new MangaGenre(R.string.genre_drama, "драма"),
            new MangaGenre(R.string.genre_game, "игра"),
            new MangaGenre(R.string.genre_historical, "история"),
            new MangaGenre(R.string.genre_cyberpunk, "киберпанк"),
            new MangaGenre(R.string.genre_codomo, "кодомо"),
            new MangaGenre(R.string.genre_comedy, "комедия"),
            new MangaGenre(R.string.genre_maho_shoujo, "махо-сёдзё"),
            new MangaGenre(R.string.genre_mecha, "меха"),
            new MangaGenre(R.string.genre_mystery, "мистика"),
            new MangaGenre(R.string.genre_music, "музыка"),
            new MangaGenre(R.string.genre_sci_fi, "научная_фантастика"),
            new MangaGenre(R.string.genre_slice_of_life, "повседневность"),
            new MangaGenre(R.string.genre_postapocalipse, "постапокалиптика"),
            new MangaGenre(R.string.genre_adventure, "приключения"),
            new MangaGenre(R.string.genre_psychological, "психология"),
            new MangaGenre(R.string.genre_romance, "романтика"),
            new MangaGenre(R.string.genre_supernatural, "сверхъестественное"),
            new MangaGenre(R.string.genre_sports, "спорт"),
            new MangaGenre(R.string.genre_superpower, "супергерои"),
            new MangaGenre(R.string.genre_seinen, "сэйнэн"),
            new MangaGenre(R.string.genre_shoujo, "сёдзё"),
            new MangaGenre(R.string.genre_shoujo_ai, "сёдзё-ай"),
            new MangaGenre(R.string.genre_shounen, "сёнэн"),
            new MangaGenre(R.string.genre_shounen_ai, "сёнэн-ай"),
            new MangaGenre(R.string.genre_tragedy, "трагедия"),
            new MangaGenre(R.string.genre_thriller, "триллер"),
            new MangaGenre(R.string.genre_horror, "ужасы"),
            new MangaGenre(R.string.genre_fantastic, "фантастика"),
            new MangaGenre(R.string.genre_fantasy, "фэнтези"),
            new MangaGenre(R.string.genre_school, "школа"),
            new MangaGenre(R.string.genre_erotica, "эротика"),
            new MangaGenre(R.string.genre_yuri, "юри"),
            new MangaGenre(R.string.genre_yaoi, "яой")
    };

    public MangaChan(Context context) {
        super(context);
        if ("".equals(sAuthCookie)) {
            sAuthCookie = null;
        }
    }

    @NonNull
    @Override
    public ArrayList<MangaHeader> query(@Nullable String search, int page, int sortOrder, int additionalSortOrder, @NonNull String[] genres, @NonNull String[] types) throws Exception {
        boolean hasQuery = !TextUtils.isEmpty(search);
        if (hasQuery) {
            return simpleSearch(search, page);
        }
        String[] genre = genres;
        if (genres.length == 0)
            genre = null;
        return getList(page, sortOrder, genre);
    }

    @SuppressLint({"DefaultLocale"})
    @NonNull
    protected ArrayList<MangaHeader> simpleSearch(@NonNull String search, int page) throws Exception {
        if (page > 0) {
            return EMPTY_HEADERS;
        }
        String path = "http://manga-chan.me/?do=search&subaction=search&story=" +
                search;
        Elements elements = NetworkUtils.getDocument(path).body().select("div.content_row");
        ArrayList<MangaHeader> arrayList = new ArrayList<>(elements.size());
        for (Element o : elements) {
            Element a = o.select("h2").first().child(0);
            String title = a.text().replaceAll("\\(.*?\\)", "");
            String url = url(this.mDomain, a.attr("href"));
            String author = o.select("h3").first().child(0).text();
            String thumbnail = o.select("img").first().attr("src");
            arrayList.add(new MangaHeader(
                    title,
                    author,
                    "",
                    url,
                    thumbnail,
                    CNAME,
                    parseStatus(o.select("div.item2").text()),
                    (short)0)
            );
        }
        return arrayList;
    }

    @NonNull
    @Override
    public MangaDetails getDetails(MangaHeader header) throws Exception {
        Element body = NetworkUtils.getDocument(header.url).body();
        Element infoElement = body.select("table.mangatitle").first();
        Element descElement = body.select("div#description").first();
        Element imgElement = body.select("img#cover").first();
        final MangaDetails details = new MangaDetails(
                header.id,
                header.name,
                header.summary,
                parseGenres(body.select(".elem_genre a"), header.genres),
                header.url,
                header.thumbnail,
                header.provider,
                parseStatus(infoElement.select("tr:eq(3) > td:eq(1)").text()),
                header.rating,
                descElement.text().trim(),
                imgElement.attr("src"),
                infoElement.select("tr:eq(2) > td:eq(1)").text(),
                new MangaChaptersList()
        );
        Elements elements = body.select(".table_cha tbody a");
        if (elements == null)
            return details;
        String domain = NetworkUtils.getDomainWithScheme(details.url);
        int j = elements.size();
        for (int i = 0; i < j; i++) {
            Element element = elements.get(j - i - 1);
            Element timeElement = body.select(".table_cha tbody div.date").get(j - i - 1);
            long date = Objects.requireNonNull(new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(timeElement.text())).getTime();
            details.chapters.add(new MangaChapter(
                    element.text(),
                    i,
                    url(domain, element.attr("href")),
                    header.provider,
                    "",
                    date
            ));
        }
        return details;
    }

    private int parseStatus(String element) {
        int status = MangaStatus.STATUS_UNKNOWN;
        if (element.contains("выпуск завершен")) {
            status = MangaStatus.STATUS_COMPLETED;
        } else if (element.contains("выпуск продолжается")) {
            status = MangaStatus.STATUS_ONGOING;
        }
        return status;
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

    @NonNull
    @Override
    public ArrayList<MangaPage> getPages(String chapterUrl) throws Exception {
        ArrayList<MangaPage> pages = new ArrayList<>();
        for (Element element : NetworkUtils.getDocument(chapterUrl).select("script")) {
            String str = element.html();
            int i = str.indexOf("fullimg\":[");
            if (i != -1) {
                JSONArray jSONArray = new JSONArray(str.substring(i + 9, str.lastIndexOf("]") + 1));
                for (i = 0; i < jSONArray.length() - 1; i++)
                    pages.add(new MangaPage(jSONArray.getString(i), Objects.requireNonNull(getCName())));
                return pages;
            }
        }
        return pages;
    }

    @Override
    public String getPageImage(MangaPage mangaPage) {
        return mangaPage.url;
    }

    protected ArrayList<MangaHeader> getList(int page, int sort, String[] genres) throws Exception {
        String str1;
        String str2;
        StringJoinerCompat stringJoinerCompat = new StringJoinerCompat("+", "", "");
        if (genres != null) {
            int i;
            if (genres.length >= 1) {
                int j = genres.length;
                for (i = 0; i < j; i++)
                    stringJoinerCompat.add(genres[i]);
            }
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(mDomain);
        stringBuilder.append("/%s?&n=%s&offset=%d");
        String str3 = stringBuilder.toString();
        if (stringJoinerCompat.toString().equals("")) {
            str1 = "manga/new";
        } else {
            stringBuilder = new StringBuilder();
            stringBuilder.append("tags/");
            stringBuilder.append(stringJoinerCompat.toString());
            str1 = stringBuilder.toString();
        }
        if (sort == -1) {
            str2 = "";
        } else {
            str2 = mSortValues[sort];
        }
        Elements elements = NetworkUtils.getDocument(String.format(str3, str1, str2, page * 20)).body().select("div.content_row");
        ArrayList<MangaHeader> arrayList = new ArrayList<>(elements.size());
        for (Element o : elements) {
            Element element3 = o.select("h2").first().child(0);
            String title = element3.text().replaceAll("\\([^()]*\\)", "");
            Element element4 = o.select("div.manga_row2").select("h3.item2").first().child(0);
            String status = o.select("div.item2").text();
            String author = element4.text();
            String genre = parseGenres(o.select(".genre"), "");
            String url = url(this.mDomain, element3.attr("href"));
            String thumbnail = o.select("img").first().attr("src");
            arrayList.add(new MangaHeader(
                    title,
                    author == null ? "" : author,
                    genre,
                    url,
                    thumbnail,
                    CNAME,
                    parseStatus(status),
                    (short)0)
            );
        }
        return arrayList;
    }

    @Override
    public int[] getAvailableSortOrders() {
        return mSorts;
    }

    @Override
    public MangaGenre[] getAvailableGenres() {
        return mGenres;
    }

}