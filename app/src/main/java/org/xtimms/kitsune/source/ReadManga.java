package org.xtimms.kitsune.source;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.xtimms.kitsune.R;
import org.xtimms.kitsune.core.common.StringJoinerCompat;
import org.xtimms.kitsune.core.models.MangaGenre;
import org.xtimms.kitsune.core.models.MangaHeader;
import org.xtimms.kitsune.core.models.MangaPage;
import org.xtimms.kitsune.utils.network.NetworkUtils;

import java.util.ArrayList;

public final class ReadManga extends GroupLe {

    public static final String CNAME = "network/readmanga.ru";
    public static final String DNAME = "ReadManga";

    private final int[] mSorts = new int[]{
            R.string.sort_popular,
            R.string.sort_rating,
            R.string.sort_latest,
            R.string.sort_updated
    };

    private final String[] mSortValues = new String[]{
            "rate",
            "votes",
            "created",
            "updated"
    };

    private final int[] mAdditionalSorts = new int[]{
            R.string.all,
            R.string.high_rate,
            R.string.single,
            R.string.mature,
            R.string.status_completed_not_caps,
            R.string.translated,
            R.string.many_chapters,
            R.string.wait_upload
    };

    private final String[] mAdditionalSortValues = new String[]{
            "",
            "high_rate",
            "single",
            "mature",
            "completed",
            "translated",
            "many_chapters",
            "wait_upload"
    };

    private final MangaGenre[] mGenres = new MangaGenre[]{
            new MangaGenre(R.string.genre_art, "art"),
            new MangaGenre(R.string.genre_action, "action"),
            new MangaGenre(R.string.genre_martialarts, "martial_arts"),
            new MangaGenre(R.string.genre_vampires, "vampires"),
            new MangaGenre(R.string.genre_harem, "harem"),
            new MangaGenre(R.string.genre_genderbender, "hender_intriga"),
            new MangaGenre(R.string.genre_hero_fantasy, "heroic_fantasy"),
            new MangaGenre(R.string.genre_detective, "detective"),
            new MangaGenre(R.string.genre_josei, "josei"),
            new MangaGenre(R.string.genre_doujinshi, "doujinshi"),
            new MangaGenre(R.string.genre_drama, "drama"),
            new MangaGenre(R.string.genre_game, "game"),
            new MangaGenre(R.string.genre_historical, "historical"),
            new MangaGenre(R.string.genre_cyberpunk, "cyberpunk"),
            new MangaGenre(R.string.genre_codomo, "codomo"),
            new MangaGenre(R.string.genre_comedy, "comedy"),
            new MangaGenre(R.string.genre_maho_shoujo, "maho_shoujo"),
            new MangaGenre(R.string.genre_mecha, "mecha"),
            new MangaGenre(R.string.genre_sci_fi, "sci_fi"),
            new MangaGenre(R.string.genre_natural, "natural"),
            new MangaGenre(R.string.genre_postapocalipse, "postapocalypse"),
            new MangaGenre(R.string.genre_adventure, "adventure"),
            new MangaGenre(R.string.genre_psychological, "psychological"),
            new MangaGenre(R.string.genre_romance, "romance"),
            new MangaGenre(R.string.genre_samurai, "samurai"),
            new MangaGenre(R.string.genre_supernatural, "supernatural"),
            new MangaGenre(R.string.genre_shoujo, "shoujo"),
            new MangaGenre(R.string.genre_shoujo_ai, "shoujo_ai"),
            new MangaGenre(R.string.genre_shounen, "shounen"),
            new MangaGenre(R.string.genre_shounen_ai, "shounen_ai"),
            new MangaGenre(R.string.genre_sports, "sport"),
            new MangaGenre(R.string.genre_seinen, "seinen"),
            new MangaGenre(R.string.genre_tragedy, "tragedy"),
            new MangaGenre(R.string.genre_thriller, "thriller"),
            new MangaGenre(R.string.genre_horror, "horror"),
            new MangaGenre(R.string.genre_fantasy, "fantasy"),
            new MangaGenre(R.string.genre_school, "school"),
            new MangaGenre(R.string.genre_ecchi, "ecchi"),
            new MangaGenre(R.string.genre_yuri, "yuri")
    };

    private final String[] mTags = new String[]{
            "el_5685",
            "el_2155",
            "el_2143",
            "el_2148",
            "el_2142",
            "el_2156",
            "el_2146",
            "el_2152",
            "el_2158",
            "el_2141",
            "el_2118",
            "el_2154",
            "el_2119",
            "el_8032",
            "el_2137",
            "el_2136",
            "el_2147",
            "el_2126",
            "el_2132",
            "el_2133",
            "el_2135",
            "el_2151",
            "el_2130",
            "el_2144",
            "el_2121",
            "el_2124",
            "el_2159",
            "el_2122",
            "el_2128",
            "el_2134",
            "el_2139",
            "el_2129",
            "el_2138",
            "el_2153",
            "el_2150",
            "el_2125",
            "el_2140",
            "el_2131",
            "el_2127",
            "el_2149",
            "el_2123"
    };

    public ReadManga(Context context) {
        super(context);
    }

    @NonNull
    @Override
    @SuppressLint("DefaultLocale")
    protected ArrayList<MangaHeader> getList(int page, int sortOrder, int additionalSortOrder, @Nullable String genre, @Nullable String type) throws Exception {
        String url = String.format(
                "https://readmanga.live/list%s?lang=&sortType=%s&filter=%s&offset=%d&max=70",
                genre == null ? "" : "/genre/" + genre,
                sortOrder == -1 ? "rate" : mSortValues[sortOrder],
                additionalSortOrder == -1 ? "" : mAdditionalSortValues[additionalSortOrder],
                page * 70
        );
        Document doc = NetworkUtils.getDocument(url);
        Element root = doc.body().getElementById("mangaBox").selectFirst("div.tiles");
        return parseList(root.select(".tile"), "https://readmanga.live/");
    }

    @NonNull
    @Override
    @SuppressLint("DefaultLocale")
    protected ArrayList<MangaHeader> simpleSearch(@NonNull String search, int page) throws Exception {
        //Element element = NetworkUtils.getDocument(String.format("https://readmanga.me/search?q=%s&offset=%d&max=50",
        //		new Object[] {search, Integer.valueOf(page * 50)}))
        //		.body().getElementById("mangaResults").selectFirst("div.tiles");
        //return (element == null) ? EMPTY_HEADERS : parseList(element.select(".tile"),
        //		"http://readmanga.me");
        final String url = String.format(
                "https://readmanga.live/search?q=%s&offset=%d&max=50",
                search,
                page * 50
        ); //TODO fix "nothing found" problem
        final Document doc = NetworkUtils.getDocument(url);
        Element root = doc.body().getElementById("mangaResults").selectFirst("div.tiles");
        if (root == null) {
            return EMPTY_HEADERS;
        }
        return parseList(root.select(".tile"), "https://readmanga.live/");
    }

    @NonNull
    @Override
    @SuppressLint("DefaultLocale")
    protected ArrayList<MangaHeader> advancedSearch(@NonNull String search, @NonNull String[] genres, @NonNull String[] types) throws Exception {
        //StringJoinerCompat stringJoinerCompat = new StringJoinerCompat("&", "&", "");
        //int i = genres.length;
        //for (byte b = 0; b < i; b++) {
        //	String str = genres[b];
        //	int j = MangaGenre.indexOf(this.mGenres, str);
        //	if (j >= 0 && j < this.mTags.length) {
        //		str = this.mTags[j];
        //		StringBuilder stringBuilder1 = new StringBuilder();
        //		stringBuilder1.append(str);
        //		stringBuilder1.append("=in");
        //		stringJoinerCompat.add(stringBuilder1.toString());
        //	}
        //}
        //StringBuilder stringBuilder = new StringBuilder();
        //stringBuilder.append("http://readmanga.me/search/advanced?q=");
        //stringBuilder.append(urlEncode(search));
        //stringBuilder.append(stringJoinerCompat.toString());
        //return parseList(NetworkUtils.getDocument(stringBuilder.toString()).body().getElementById("mangaResults").selectFirst("div.tiles").select(".tile"), "http://readmanga.me");
        final StringJoinerCompat query = new StringJoinerCompat("&", "&", "");
        for (String o : genres) {
            int i = MangaGenre.indexOf(mGenres, o);
            if (i < 0 || i >= mTags.length) {
                continue;
            }
            String tag = mTags[i];
            query.add(tag + "=in");
        }
        final Document doc = NetworkUtils.getDocument("https://readmanga.live/search/advanced?q=" + urlEncode(search) + query.toString());
        final Element root = doc.body().getElementById("mangaResults").selectFirst("div.tiles");
        return parseList(root.select(".tile"), "https://readmanga.live/");
    }

    @CName
    public String getCName() {
        return CNAME;
    }

    @Override
    public MangaGenre[] getAvailableGenres() {
        return mGenres;
    }

    @Override
    public int[] getAvailableSortOrders() {
        return mSorts;
    }

    @Override
    public int[] getAvailableAdditionalSortOrders() {
        return mAdditionalSorts;
    }

    /*@Override
    public void importBookmarks(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_import, null);
        final EditText etUser = (EditText)view.findViewById(R.id.edit_id);
        builder.setView(view).setTitle("Импорт закладок")
                .setNegativeButton("Отмена", null)
                .setCancelable(true)
                .setPositiveButton("Импортировать", (dialogInterface, param1Int) -> {
                    String str = etUser.getText().toString();
                    if (str.length() > 0) {
                        (new SyncTask(context)).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, str);
                        return;
                    }
                    Toast.makeText(context, "Вы должны указать идентификатор для переноса закладок", Toast.LENGTH_SHORT).show();
                }).create().show();
    }*/

    /*@SuppressLint({"StaticFieldLeak"})
    private class SyncTask extends AsyncTask<String, Integer, Boolean> {
        private Context mContext;

        private final ProgressDialog processDialog;

        private SyncTask(Context context) {
            mContext = context;
            processDialog = new ProgressDialog(mContext);
            processDialog.setMessage("Производится импорт закладок...");
            processDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            processDialog.setCancelable(false);
        }

        protected Boolean doInBackground(String... what) {
            processDialog.setMessage("Производится импорт закладок...");
            try {
                String path = "https://grouple.co/user/" +
                        what[0] +
                        "/bookmarks";
                Elements elements = NetworkUtils.getDocument(path).body().select("a.site-element site_1");
                CategoriesRepository categoriesRepository = CategoriesRepository.get(this.mContext);
                FavouritesRepository favouritesRepository = FavouritesRepository.get(this.mContext);
                Category category = categoriesRepository.query((new CategoriesSpecification()).orderByDate(false)).get(0);
                processDialog.setMax(elements.size());
                if (elements.size() > 0) {
                    int a = 0;
                    for (Element o : elements) {
                        int b = a + 1;
                        MangaHeader mangaHeader = new MangaHeader(
                                o.select("sup").remove().html(),
                                "",
                                "",
                                url("https://readmanga.live/", o.attr("href")),
                                o.attr("rel"),
                                getCName(),
                                MangaStatus.STATUS_UNKNOWN,
                                (short)0);
                        MangaDetails mangaDetails = getDetails(mangaHeader);
                        MangaFavourite mangaFavourite = MangaFavourite.from(
                                mangaDetails,
                                category.id,
                                mangaDetails.chapters.size());
                        if (!favouritesRepository.update(mangaFavourite)) {
                            favouritesRepository.add(mangaFavourite);
                        }
                        publishProgress(++b);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }

        protected void onPostExecute(Boolean bool) {
            super.onPostExecute(bool);
            this.processDialog.dismiss();
            if (bool) {
                (new AlertDialog.Builder(this.mContext))
                        .setMessage("Закладки успешно перенесены в Kitsune")
                        .setTitle("Импорт закладок")
                        .setPositiveButton("Готово", null).create().show();
                return;
            }
            (new AlertDialog.Builder(this.mContext))
                    .setMessage("Импорт закладок")
                    .setTitle("Произошла ошибка")
                    .setPositiveButton("Готово", null).create().show();
        }

        protected void onPreExecute() {
            super.onPreExecute();
            this.processDialog.show();
        }

        protected void onProgressUpdate(Integer... integers) {
            super.onProgressUpdate(integers);
            this.processDialog.setProgress(integers[0]);
        }
    }*/

    @Override
    public String getPageImage(MangaPage mangaPage) {
        return mangaPage.url;
    }

}
