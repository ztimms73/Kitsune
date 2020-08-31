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
import org.xtimms.kitsune.R;
import org.xtimms.kitsune.core.models.MangaChaptersList;
import org.xtimms.kitsune.utils.network.NetworkUtils;
import org.xtimms.kitsune.core.MangaStatus;
import org.xtimms.kitsune.core.models.MangaChapter;
import org.xtimms.kitsune.core.models.MangaDetails;
import org.xtimms.kitsune.core.models.MangaGenre;
import org.xtimms.kitsune.core.models.MangaHeader;
import org.xtimms.kitsune.core.models.MangaPage;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;

@SuppressWarnings("ConstantConditions")
public final class MangaFox extends MangaProvider {

	public static final String CNAME = "network/fanfox.net";
	public static final String DNAME = "MangaFox";

	private final int[] mSorts = new int[]{
			R.string.sort_updated,
			R.string.sort_rating,
			R.string.sort_popular,
			R.string.sort_alphabetical
	};

	private final String[] mSortValues = new String[]{
			"?latest",
			"?rating",
			"",
			"?az"
	};

	private final String[] mSortValuesAdv = new String[]{
			"last_chapter_time",
			"rating",
			"views",
			"name"
	};

	private final MangaGenre[] mGenres = new MangaGenre[]{
			new MangaGenre(R.string.genre_action, "action"),
			new MangaGenre(R.string.genre_adult, "adult"),
			new MangaGenre(R.string.genre_adventure, "adventure"),
			new MangaGenre(R.string.genre_comedy, "comedy"),
			new MangaGenre(R.string.genre_doujinshi, "doujinshi"),
			new MangaGenre(R.string.genre_drama, "drama"),
			new MangaGenre(R.string.genre_ecchi, "ecchi"),
			new MangaGenre(R.string.genre_fantasy, "fantasy"),
			new MangaGenre(R.string.genre_genderbender, "gender-bender"),
			new MangaGenre(R.string.genre_harem, "harem"),
			new MangaGenre(R.string.genre_historical, "historical"),
			new MangaGenre(R.string.genre_horror, "horror"),
			new MangaGenre(R.string.genre_josei, "josei"),
			new MangaGenre(R.string.genre_martialarts, "martial-arts"),
			new MangaGenre(R.string.genre_mature, "mature"),
			new MangaGenre(R.string.genre_mecha, "mecha"),
			new MangaGenre(R.string.genre_mystery, "mystery"),
			new MangaGenre(R.string.genre_oneshot, "one-shot"),
			new MangaGenre(R.string.genre_psychological, "psychological"),
			new MangaGenre(R.string.genre_romance, "romance"),
			new MangaGenre(R.string.genre_school, "school-life"),
			new MangaGenre(R.string.genre_sci_fi, "sci-fi"),
			new MangaGenre(R.string.genre_seinen, "seinen"),
			new MangaGenre(R.string.genre_shoujo, "shoujo"),
			new MangaGenre(R.string.genre_shoujo_ai, "shoujo-ai"),
			new MangaGenre(R.string.genre_shounen, "shounen"),
			new MangaGenre(R.string.genre_shounen_ai, "shounen-ai"),
			new MangaGenre(R.string.genre_slice_of_life, "slice-of-life"),
			new MangaGenre(R.string.genre_smut, "smut"),
			new MangaGenre(R.string.genre_sports, "sports"),
			new MangaGenre(R.string.genre_supernatural, "supernatural"),
			new MangaGenre(R.string.genre_tragedy, "tragedy"),
			new MangaGenre(R.string.web, "webtoons"),
			new MangaGenre(R.string.genre_yaoi, "yaoi"),
			new MangaGenre(R.string.genre_yuri, "yuri"),

	};

	public MangaFox(Context context) {
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
		if (page > 0) {
			return new ArrayList<>();
		}
		final Element body = NetworkUtils.getDocument("http://m.fanfox.net/search?k=" + URLEncoder.encode(search, "UTF-8"));
		final Elements elements = body.select("ul.post-list").select("li");
		Element r;
		final ArrayList<MangaHeader> list = new ArrayList<>(elements.size());
		for (Element o : elements) {
			r = o.select("div.cover-info").first();
			list.add(new MangaHeader(
					r.child(0).text(),
					r.attr("title"),
					"",
					o.selectFirst("a").attr("href"),
					o.select("img").first().attr("src"),
					CNAME,
					MangaStatus.STATUS_UNKNOWN,
					(short) 0
			));
		}
		return list;
	}

	protected ArrayList<MangaHeader> getList(int page, int sort, String[] genre) throws Exception {
		Document document = getPage("http://fanfox.net/directory/"
				+ (genre == null ? "" : Arrays.toString(genre) + "/")
				+ (page + 1) + ".html" + mSortValues[sort]);
		Element root = document.body().selectFirst("ul.manga-list-1-list");
		final ArrayList<MangaHeader> list = new ArrayList<>(root.children().size());
		for (Element o : root.children()) {
			final Element e = o.selectFirst("a");
			list.add(new MangaHeader(
					e.attr("title"),
					e.attr("title"),
					"",
					"http://m.fanfox.net" + e.attr("href"),
					e.selectFirst("img").attr("src"),
					CNAME,
					parseStatus(e),
					(short) 0
			));
		}
		return list;
	}

	private short parseRating(String title) {
		try {
			return Short.parseShort(title.substring(0, title.indexOf('.') + 2).replace(".", ""));
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	private int parseStatus(Element element) {
		int status = MangaStatus.STATUS_UNKNOWN;
		if (!element.select("img.logo-complete").isEmpty()) {
			status = MangaStatus.STATUS_COMPLETED;
		}
		return status;
	}

	@NonNull
	@Override
	public MangaDetails getDetails(MangaHeader header) throws Exception {
		assert header.url != null;
		final Document doc = NetworkUtils.getDocument(header.url);
		Element e = doc.body();
		String author = e.select("p").select("a").get(0).text();
		String description = Html.fromHtml(e.select(".manga-summary").html()).toString().trim();
		StringBuilder sb = new StringBuilder();
		for (Element gnr : e.select("div.manga-genres a")) {
			sb.append(",").append(gnr.text().trim());
		}
		final MangaDetails details = new MangaDetails(
				header.id,
				header.name,
				header.summary,
				sb.length() > 0 ? sb.substring(1) : "",
				header.url,
				header.thumbnail,
				header.provider,
				header.status,
				header.rating,
				description,
				header.thumbnail,
				author,
				new MangaChaptersList()
		);
		final Elements ch = e.select("dd.chlist a");
		final int len = ch.size();
		for (int i = 0; i < len; i++) {
			Element o = ch.get(len - i - 1);
			details.chapters.add(new MangaChapter(
					o.text().trim(),
					i,
					"http:" + o.attr("href"),
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
		ArrayList<MangaPage> pages = new ArrayList<>();
		try {
			Document document = getPage(chapterUrl);
			Element e = document.body().select("select.mangaread-page").first();
			for (Element o : e.select("option")) {
				pages.add(new MangaPage(
						"http:" + o.attr("value"),
						CNAME
				));
			}
			return pages;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@NonNull
	@Override
	public String getImageUrl(@NotNull @NonNull MangaPage page) throws Exception {
		return NetworkUtils.getDocument(page.url).getElementById("image").attr("src");
	}

	@Override
	public String getPageImage(MangaPage mangaPage) {
		return mangaPage.url;
	}

	@Override
	public int[] getAvailableSortOrders() {
		return mSorts;
	}

	@NotNull
	@Override
	public String getCName() {
		return MangaFox.CNAME;
	}
}
