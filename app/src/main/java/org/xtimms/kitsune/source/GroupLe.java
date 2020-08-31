package org.xtimms.kitsune.source;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.TextUtils;

import org.json.JSONArray;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xtimms.kitsune.core.common.StringJoinerCompat;
import org.xtimms.kitsune.utils.FileLogger;
import org.xtimms.kitsune.utils.network.NetworkUtils;
import org.xtimms.kitsune.core.MangaStatus;
import org.xtimms.kitsune.core.models.MangaChapter;
import org.xtimms.kitsune.core.models.MangaChaptersList;
import org.xtimms.kitsune.core.models.MangaDetails;
import org.xtimms.kitsune.core.models.MangaHeader;
import org.xtimms.kitsune.core.models.MangaPage;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

abstract class GroupLe extends MangaProvider {

	public GroupLe(Context context) {
		super(context);
	}

	@NonNull
	@Override
	public ArrayList<MangaHeader> query(@Nullable String search, int page, int sortOrder, int additionalSortOrder, @NonNull String[] genres, @NonNull String[] types) throws Exception {
		boolean hasQuery = !TextUtils.isEmpty(search);
		boolean multipleGenres = genres.length >= 1;
		boolean multipleTypes = types.length >= 1;
		if (multipleGenres || multipleTypes || hasQuery ) {
			return page != 0 ? EMPTY_HEADERS : advancedSearch(org.xtimms.kitsune.utils.TextUtils.notNull(search), genres, types);
		} else {
			return getList(page, sortOrder, additionalSortOrder, search, search);
		}
	}

	@NonNull
	protected abstract ArrayList<MangaHeader> getList(int page, int sortOrder, int additionalSortOrder, @Nullable String genre, @Nullable String type) throws Exception;

	@NonNull
	protected abstract ArrayList<MangaHeader> simpleSearch(@NonNull String search, int page) throws Exception;

	@NonNull
	protected abstract ArrayList<MangaHeader> advancedSearch(@NonNull String search, @NonNull String[] genres, @NonNull String[] types) throws Exception;

	protected final ArrayList<MangaHeader> parseList(Elements elements, String domain) {
		final ArrayList<MangaHeader> list = new ArrayList<>(elements.size());
		for (Element e : elements) {
			if (!e.select(".fa-external-link").isEmpty()) {
				continue;
			}
			final Element title = e.selectFirst("h3").child(0);
			final Element rating = e.selectFirst("div.rating");
			final Element author = e.selectFirst("div.tile-info").child(0);
			int status = MangaStatus.STATUS_UNKNOWN;
			if (!e.select("span.mangaCompleted").isEmpty()) {
				status = MangaStatus.STATUS_COMPLETED;
			} else if (!e.select("span.mangaForSale").isEmpty()) {
				status = MangaStatus.STATUS_LICENSED;
			}
			final Element subtitle = e.selectFirst("h4");
			final Element img = e.selectFirst("img.lazy");
			list.add(new MangaHeader(
					title.text(),
					subtitle == null ? "" : subtitle.text(),
					parseGenres(e.select(".element-link"), ""),
					url(domain, title.attr("href")),
					img == null ? "" : img.attr("data-original"),
					Objects.requireNonNull(getCName()),
					status,
					rating == null ? 0 : parseRating(rating.attr("title"))
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

	@NonNull
	@Override
	public MangaDetails getDetails(MangaHeader header) throws Exception {
		assert header.url != null;
		final Document doc = NetworkUtils.getDocument(header.url);
		Element root = doc.body().getElementById("mangaBox");
		final Element description = root.selectFirst(".manga-description");
		final Element author = root.selectFirst(".elem_author");
		assert header.genres != null;
		final MangaDetails details = new MangaDetails(
				header.id,
				header.name,
				header.summary,
				parseGenres(root.select(".elem_genre a"),header.genres),
				header.url,
				header.thumbnail,
				header.provider,
				header.status,
				header.rating,
				description == null ? "" : description.html(),
				root.selectFirst("div.picture-fotorama").child(0).attr("data-full"),
				author == null ? "" : author.child(0).text(),
				new MangaChaptersList()
		);
		root = root.selectFirst("div.chapters-link");
		if (root == null) {
			return details;
		}
		root = root.selectFirst("tbody");
		final Elements ch = root.select("a");
		final String domain = NetworkUtils.getDomainWithScheme(header.url);
		final int len = ch.size();
		for (int i = 0; i < len; i++) {
			Element o = ch.get(len - i - 1);
			String it = root.select("td.hidden-xxs").get(len - i - 1).text();
			long date;
			try {
				date = Objects.requireNonNull(new SimpleDateFormat("dd.MM.yy", Locale.US).parse(it)).getTime();
			} catch (ParseException e) {
				date = Objects.requireNonNull(new SimpleDateFormat("dd.MM.yy", Locale.US).parse("01.01.1970")).getTime();
			}
			details.chapters.add(new MangaChapter(
					o.text(),
					i,
					url(domain, o.attr("href") + "?mtr=1"),
					header.provider,
					"",
					date
			));
		}
		return details;
	}

	@SuppressWarnings("deprecation")
	@NonNull
	@Override
	public ArrayList<MangaPage> getPages(String chapterUrl) {
		try
		{
			final Elements scripts = NetworkUtils.getDocument(chapterUrl).select("script");
			final String domain = NetworkUtils.getDomainWithScheme(chapterUrl);
			final ArrayList<MangaPage> pages = new ArrayList<>();
			for (Element script : scripts) {
				String s = script.html();
				int beginIndex = s.indexOf("rm_h.init( [");
				if (beginIndex == -1) {
					continue;
				}
				beginIndex += 10;
				int endIndex = s.indexOf(");", beginIndex);
				s = s.substring(beginIndex, endIndex);
				final JSONArray array = new JSONArray(s);
				for (int i = 0; i < array.length(); i++) {
					JSONArray item = array.getJSONArray(i);
					pages.add(new MangaPage(
							item.getString(0) + item.getString(2),
							Objects.requireNonNull(getCName())
					));
				}
				return pages;
			}
		}
		catch (Exception e) {
			FileLogger.getInstance().report(e);
		}
		throw new RuntimeException("No reader script found");
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
