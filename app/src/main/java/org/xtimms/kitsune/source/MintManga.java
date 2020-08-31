package org.xtimms.kitsune.source;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.xtimms.kitsune.R;
import org.xtimms.kitsune.core.common.StringJoinerCompat;
import org.xtimms.kitsune.core.models.MangaPage;
import org.xtimms.kitsune.utils.network.NetworkUtils;
import org.xtimms.kitsune.core.models.MangaGenre;
import org.xtimms.kitsune.core.models.MangaHeader;

import java.util.ArrayList;

public final class MintManga extends GroupLe {

	public static final String CNAME = "network/mint";
	public static final String DNAME = "MintManga";

	private final int[] mSorts = new int[] {
			R.string.sort_popular,
			R.string.sort_rating,
			R.string.sort_latest,
			R.string.sort_updated
	};

	private final String[] mSortValues = new String[] {
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
			new MangaGenre(R.string.genre_bara, "bara"),
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
			new MangaGenre(R.string.genre_comedy, "comedy"),
			new MangaGenre(R.string.genre_mecha, "mecha"),
			new MangaGenre(R.string.genre_mystery, "mystery"),
			new MangaGenre(R.string.genre_sci_fi, "sci_fi"),
			new MangaGenre(R.string.genre_omegaverse, "omegaverse"),
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
			new MangaGenre(R.string.genre_fantastic, "fantastic"),
			new MangaGenre(R.string.genre_fantasy, "fantasy"),
			new MangaGenre(R.string.genre_school, "school"),
			new MangaGenre(R.string.genre_erotica, "erotica"),
			new MangaGenre(R.string.genre_ecchi, "ecchi"),
			new MangaGenre(R.string.genre_yuri, "yuri"),
			new MangaGenre(R.string.genre_yaoi, "yaoi")
	};

	private final String[] mTags = new String[] {
			"el_2220",
			"el_1353",
			"el_1346",
			"el_1334",
			"el_1339",
			"el_1333",
			"el_1347",
			"el_1337",
			"el_1343",
			"el_1349",
			"el_1332",
			"el_1310",
			"el_5229",
			"el_1311",
			"el_1351",
			"el_1328",
			"el_1318",
			"el_1324",
			"el_1325",
			"el_5676",
			"el_1327",
			"el_1342",
			"el_1322",
			"el_1335",
			"el_1313",
			"el_1316",
			"el_1350",
			"el_1314",
			"el_1320",
			"el_1326",
			"el_1330",
			"el_1321",
			"el_1329",
			"el_1344",
			"el_1341",
			"el_1317",
			"el_1331",
			"el_1323",
			"el_1319",
			"el_1340",
			"el_1354",
			"el_1315",
			"el_1336"
	};

	public MintManga(Context context) {
		super(context);
	}

	@Override
	public String getPageImage(MangaPage mangaPage) {
		return mangaPage.url;
	}

	@NonNull
	@SuppressLint("DefaultLocale")
	protected ArrayList<MangaHeader> getList(int page, int sortOrder, int additionalSortOrder, @Nullable String genre, @Nullable String type) throws Exception {
		String url = String.format(
				"https://mintmanga.live/list%s?lang=&sortType=%s&filter=%s&offset=%d&max=70",
				genre == null ? "" : "/genre/" + genre,
				sortOrder == -1 ? "rate" : mSortValues[sortOrder],
				additionalSortOrder == -1 ? "" : mAdditionalSortValues[additionalSortOrder],
				page * 70
		);
		Document doc = NetworkUtils.getDocument(url);
		Element root = doc.body().getElementById("mangaBox").selectFirst("div.tiles");
		return parseList(root.select(".tile"), "https://mintmanga.live/");
	}

	@NonNull
	@SuppressLint("DefaultLocale")
	protected ArrayList<MangaHeader> simpleSearch(@NonNull String search, int page) throws Exception {
		String url = String.format(
				"https://mintmanga.live/search?q=%s&offset=%d&max=50",
				search,
				page * 50
		);
		Document doc = NetworkUtils.getDocument(url);
		Element root = doc.body().getElementById("mangaResults").selectFirst("div.tiles");
		if (root == null) {
			return EMPTY_HEADERS;
		}
		return parseList(root.select(".tile"), "https://mintmanga.live/");
	}

	@NonNull
	@SuppressLint("DefaultLocale")
	protected ArrayList<MangaHeader> advancedSearch(@NonNull String search, @NonNull String[] genres, @NonNull String[] types) throws Exception {
		//StringJoinerCompat stringJoinerCompat = new StringJoinerCompat("&", "&", "");
		//int i = genres.length;
		//for (byte b = 0; b < i; b++ ) {
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
		//stringBuilder.append("http://mintmanga.live/search/advanced?q=");
		//stringBuilder.append(urlEncode(search));
		//stringBuilder.append(stringJoinerCompat.toString());
		//return parseList(NetworkUtils.getDocument(stringBuilder.toString()).body().getElementById("mangaResults").selectFirst("div.tiles").select(".tile"), "http://mintmanga.live");
		final StringJoinerCompat query = new StringJoinerCompat("&", "&", "");
		for (String o : genres) {
			int i = MangaGenre.indexOf(mGenres, o);
			if (i < 0 || i >= mTags.length) {
				continue;
			}
			String tag = mTags[i];
			query.add(tag + "=in");
		}
		Document doc = NetworkUtils.getDocument("https://mintmanga.live/search/advanced?q=" + urlEncode(search) + query.toString());
		Element root = doc.body().getElementById("mangaResults").selectFirst("div.tiles");
		return parseList(root.select(".tile"), "https://mintmanga.live/");
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

}
