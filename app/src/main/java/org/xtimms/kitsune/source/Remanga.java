package org.xtimms.kitsune.source;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xtimms.kitsune.R;
import org.xtimms.kitsune.core.MangaStatus;
import org.xtimms.kitsune.core.models.MangaChapter;
import org.xtimms.kitsune.core.models.MangaChaptersList;
import org.xtimms.kitsune.core.models.MangaDetails;
import org.xtimms.kitsune.core.models.MangaGenre;
import org.xtimms.kitsune.core.models.MangaHeader;
import org.xtimms.kitsune.core.models.MangaPage;
import org.xtimms.kitsune.utils.network.NetworkUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.Response;

@SuppressWarnings("ALL")
public final class Remanga extends MangaProvider {

    public static final String CNAME = "network/remanga.org";
    public static final String DNAME = "Remanga";

    private final MediaType MEDIA_TYPE = MediaType.parse("application/json; charset=utf-8");

    Remanga(Context context) {
        super(context);
    }

    private final int[] mSorts = new int[] {
            R.string.sort_latest,
            R.string.sort_updated,
            R.string.sort_popular,
            R.string.sort_likes,
            R.string.sort_watches,
            R.string.chapters_count
    };

    private final String[] mSortValues = new String[] {
            "-id",
            "-chapter_date",
            "-rating",
            "-votes",
            "-views",
            "-count_chapters"
    };

    private final MangaGenre[] mGenres = new MangaGenre[]{
            new MangaGenre(R.string.genre_action, "2"),
            new MangaGenre(R.string.genre_martialarts, "3"),
            new MangaGenre(R.string.genre_harem, "5"),
            new MangaGenre(R.string.genre_genderbender, "6"),
            new MangaGenre(R.string.genre_hero_fantasy, "7"),
            new MangaGenre(R.string.genre_detective, "8"),
            new MangaGenre(R.string.genre_josei, "9"),
            new MangaGenre(R.string.genre_doujinshi, "10"),
            new MangaGenre(R.string.genre_drama, "11"),
            new MangaGenre(R.string.genre_game, "12"),
            new MangaGenre(R.string.genre_historical, "13"),
            new MangaGenre(R.string.genre_cyberpunk, "14"),
            new MangaGenre(R.string.genre_codomo, "15"),
            new MangaGenre(R.string.genre_comedy, "16"),
            new MangaGenre(R.string.genre_maho_shoujo, "17"),
            new MangaGenre(R.string.genre_mecha, "18"),
            new MangaGenre(R.string.genre_mystery, "19"),
            new MangaGenre(R.string.genre_sci_fi, "20"),
            new MangaGenre(R.string.genre_slice_of_life, "21"),
            new MangaGenre(R.string.genre_postapocalipse, "22"),
            new MangaGenre(R.string.genre_adventure, "23"),
            new MangaGenre(R.string.genre_psychological, "24"),
            new MangaGenre(R.string.genre_romance, "25"),
            new MangaGenre(R.string.genre_supernatural, "27"),
            new MangaGenre(R.string.genre_shoujo, "28"),
            new MangaGenre(R.string.genre_shoujo_ai, "29"),
            new MangaGenre(R.string.genre_shounen, "30"),
            new MangaGenre(R.string.genre_shounen_ai, "31"),
            new MangaGenre(R.string.genre_sports, "32"),
            new MangaGenre(R.string.genre_seinen, "33"),
            new MangaGenre(R.string.genre_tragedy, "34"),
            new MangaGenre(R.string.genre_thriller, "35"),
            new MangaGenre(R.string.genre_horror, "36"),
            new MangaGenre(R.string.genre_fantastic, "37"),
            new MangaGenre(R.string.genre_fantasy, "38"),
            new MangaGenre(R.string.genre_school, "39"),
            new MangaGenre(R.string.genre_erotica, "42"),
            new MangaGenre(R.string.genre_ecchi, "40"),
            new MangaGenre(R.string.genre_yuri, "41"),
            new MangaGenre(R.string.genre_yaoi, "43")
    };

    @NonNull
    @Override
    public ArrayList<MangaHeader> query(@Nullable String search, int page, int sortOrder, int additionalSortOrder, @NonNull String[] genres, @NonNull String[] types) throws Exception {
        @SuppressLint("DefaultLocale") String url = String.format(
                "https://api.remanga.org/api/search%s/?genres=%s&ordering=%s&page=%d&query=%s",
                search == null ? "/catalog" : "",
                TextUtils.join("&genres=", genres),
                sortOrder == -1 ? "-rating" : mSortValues[sortOrder],
                page + 1,
                search == null ? "" : search
        );
        JSONArray ja = NetworkUtils.getJSONObject(url).getJSONArray("content");
        ArrayList<MangaHeader> list = new ArrayList<>(ja.length());
        for (int i = 0; i < ja.length(); i++) {
            JSONObject jo = ja.getJSONObject(i);
            list.add(new MangaHeader(
                    jo.getString("en_name"),
                    jo.getString("rus_name"),
                    "",
                    "https://api.remanga.org/api/titles/" + jo.getString("dir"),
                    "https://api.remanga.org" + jo.getJSONObject("img").getString("high"),
                    CNAME,
                    MangaStatus.STATUS_UNKNOWN,
                    (byte) (jo.getDouble("avg_rating") * 10)
            ));
        }
        return list;
    }

    private int parseStatus(int status) {
        int e = MangaStatus.STATUS_UNKNOWN;
        if (status == 0) {
            e = MangaStatus.STATUS_COMPLETED;
        } else if (status == 1) {
            e = MangaStatus.STATUS_ONGOING;
        }
        return e;
    }

    @NonNull
    @Override
    public MangaDetails getDetails(MangaHeader header) throws Exception {
        assert header.url != null;
        JSONObject jo = NetworkUtils.getJSONObject(header.url).getJSONObject("content");
        MangaDetails details = new MangaDetails(
                header.id,
                header.name,
                header.summary,
                header.genres,
                header.url,
                header.thumbnail,
                header.provider,
                parseStatus(jo.getJSONObject("status").getInt("id")),
                header.rating,
                jo.getString("description"),
                "https://api.remanga.org" + jo.getJSONObject("img").getString("high"),
                "",
                new MangaChaptersList()
        );
        JSONObject branch = jo.getJSONArray("branches").getJSONObject(0);
        JSONArray ja = NetworkUtils.getJSONObject("https://api.remanga.org/api/titles/chapters/?branch_id=" + branch.getInt("id")).getJSONArray("content");
        final int total = ja.length();
        for (int i = 0; i < total; i++) {
            JSONObject chapter = ja.getJSONObject(total - i - 1);
            final String ch = chapter.getString("chapter");
            final String tome = chapter.getString("tome");
            String scanlator = null;
            JSONArray publArray = chapter.getJSONArray("publishers");
            for(int j = 0; j < publArray.length(); j++)
            {
                JSONObject json = publArray.getJSONObject(j);
                scanlator = json.getString("name");
            }
            details.chapters.add(new MangaChapter(
                    "Том " + tome + ". " + "Глава " + ch,
                    i,
                    "https://api.remanga.org/api/titles/chapters/" + chapter.getInt("id"),
                    CNAME,
                    scanlator,
                    parseDate(chapter.getString("upload_date"))
            ));
        }
        return details;
    }

    @NonNull
    @Override
    public ArrayList<MangaPage> getPages(String chapterUrl) throws Exception {
        JSONObject jo = NetworkUtils.getJSONObject(chapterUrl).getJSONObject("content");
        JSONArray ja = jo.getJSONArray("pages");
        ArrayList<MangaPage> pages = new ArrayList<>(ja.length());
        for (int i = 0; i < ja.length(); i++) {
            jo = ja.getJSONObject(i);
            pages.add(new MangaPage(
                    jo.getString("link"),
                    CNAME
            ));
        }
        return pages;
    }

    private long parseDate(String date) {
        long time;
        if (date == null){
            time = System.currentTimeMillis();
        } else {
            try {
                time = Objects.requireNonNull(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).parse(date)).getTime();
            } catch (Exception e) {
                try {
                    time = Objects.requireNonNull(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S", Locale.US).parse(date)).getTime();
                } catch (Exception ex)
                {
                    time = System.currentTimeMillis();
                }
            }
        }
        return time;
    }

    private String login(Interceptor.Chain chain, String username, String password) throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("user", username);
        jsonObject.put("password", password);
        RequestBody body = RequestBody.create(MEDIA_TYPE, jsonObject.toString());
        Response response = chain.proceed(NetworkUtils.httpPost("https://api.remanga.org/api/users/login/", NetworkUtils.HEADERS_DEFAULT, body));
        if (response.code() == 400) {
            throw new Exception("Failed to login");
        }
        return jsonObject.getJSONObject("content").getString("access-token");
    }

    @Override
    public int[] getAvailableSortOrders() {
        return mSorts;
    }

    @Override
    public MangaGenre[] getAvailableGenres() {
        return mGenres;
    }

    @Override
    public String getPageImage(MangaPage mangaPage) {
        return mangaPage.url;
    }
}
