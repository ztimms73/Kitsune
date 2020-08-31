package org.xtimms.kitsune.source;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import android.util.LruCache;

import org.jsoup.nodes.Document;
import org.xtimms.kitsune.core.models.MangaType;
import org.xtimms.kitsune.utils.network.NetworkUtils;
import org.xtimms.kitsune.core.models.MangaDetails;
import org.xtimms.kitsune.core.models.MangaGenre;
import org.xtimms.kitsune.core.models.MangaHeader;
import org.xtimms.kitsune.core.models.MangaPage;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public abstract class MangaProvider {

	private static final HashMap<String, String> sDomainsMap = new HashMap<>();
	protected static final ArrayList<MangaHeader> EMPTY_HEADERS = new ArrayList<>(0);

	protected final Context mContext;

	public MangaProvider(Context context) {
		mContext = context;
	}

	protected SharedPreferences getPreferences() {
		return mContext.getSharedPreferences("prov_" + Objects.requireNonNull(this.getCName()).replace('/','_'), Context.MODE_PRIVATE);
	}

	/**
	 *
	 * @param search - search query or null
	 * @param page - from 0 to infinity
	 * @param sortOrder - index of {@link #getAvailableSortOrders()} or -1
	 * @param genres - array of values from {@link #getAvailableGenres()}
	 * @return list
	 * @throws Exception if anything wrong
	 */
	@NonNull
	public abstract ArrayList<MangaHeader> query(@Nullable String search, int page, int sortOrder, int additionalSortOrder, @NonNull String[] genres, @NonNull String[] types) throws Exception;

	@NonNull
	public abstract MangaDetails getDetails(MangaHeader header) throws Exception;

	@NonNull
	public abstract ArrayList<MangaPage> getPages(String chapterUrl) throws Exception;

	@NonNull
	public String getImageUrl(@NonNull MangaPage page) throws Exception {
		return page.url;
	}

	public boolean signIn(String login, String password) throws Exception {
		return false;
	}

	protected void setAuthCookie(@Nullable String cookie) {
		getPreferences().edit()
				.putString("_cookie", cookie)
				.apply();
	}

	@Nullable
	protected String getAuthCookie() {
		return getPreferences().getString("_cookie", null);
	}

	public abstract String getPageImage(MangaPage mangaPage);

	public boolean isSearchSupported() {
		return true;
	}

	public boolean isMultipleGenresSupported() {
		return true;
	}

	public boolean isAuthorizationSupported() {
		return false;
	}

	@Nullable
	@SuppressLint("WrongConstant")
	public String authorize(@NonNull String login, @NonNull String password) throws Exception {
		throw new UnsupportedOperationException("Authorization not supported for " + getCName());
	}

	public MangaGenre[] getAvailableGenres() {
		return new MangaGenre[0];
	}

	public MangaType[] getAvailableTypes() {
		return new MangaType[0];
	}

	public int[] getAvailableSortOrders() {
		return new int[0];
	}

	public int[] getAvailableAdditionalSortOrders() {
		return new int[0];
	}

	public final boolean isAuthorized() {
		return !android.text.TextUtils.isEmpty(getAuthCookie());
	}

	@Nullable
	public String getName() {
		try {
			return ((String)this.getClass().getField("DNAME").get(this));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@CName
	@Nullable
	public String getCName() {
		try {
			return ((String)this.getClass().getField("CNAME").get(this));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	static String concatUrl(String root, String url) {
		return url == null || url.startsWith("http://") || url.startsWith("https://") ? url : root + (url.startsWith("/") ? url.substring(1) : url);
	}

	protected final Document getPage(String url) throws IOException, NoSuchAlgorithmException, KeyManagementException {
		return NetworkUtils.httpGet(url, getAuthCookie());
	}

	private static final LruCache<String,MangaProvider> sProviderCache = new LruCache<>(5);

	@NonNull
	public static MangaProvider get(Context context, @NonNull @CName String cName) throws AssertionError {
		MangaProvider provider = sProviderCache.get(cName);
		if (provider != null) {
			return NetworkUtils.isNetworkAvailable(context) ? provider : new OfflineManga(context, provider);
		}
		switch (cName) {
			case ReadManga.CNAME:
				provider = new ReadManga(context);
				break;
			case MintManga.CNAME:
				provider = new MintManga(context);
				break;
			case SelfManga.CNAME:
				provider = new SelfManga(context);
				break;
			case Desu.CNAME:
				provider = new Desu(context);
				break;
			case MangaChan.CNAME:
				provider = new MangaChan(context);
				break;
			case MangaLib.CNAME:
				provider = new MangaLib(context);
				break;
			case Remanga.CNAME:
				provider = new Remanga(context);
				break;
			case MangaFox.CNAME:
				provider = new MangaFox(context);
				break;
			case MangaTown.CNAME:
				provider = new MangaTown(context);
				break;
			case Anibel.CNAME:
				provider = new Anibel(context);
				break;
			case ZipArchive.CNAME:
				provider = new ZipArchive(context);
				sProviderCache.put(cName, provider);
				return provider;
			default:
				throw new AssertionError(String.format("Provider %s not registered", cName));
		}
		sProviderCache.put(cName, provider);
		return NetworkUtils.isNetworkAvailable(context) ? provider : new OfflineManga(context, provider);
	}

	@Nullable
	public static MangaGenre findGenre(MangaProvider provider, @StringRes int genreNameRes) {
		MangaGenre[] genres = provider.getAvailableGenres();
		for (MangaGenre o : genres) {
			if (o.nameId == genreNameRes) {
				return o;
			}
		}
		return null;
	}

	public static int findSortIndex(MangaProvider provider, @StringRes int sortOrder) {
		int[] sorts = provider.getAvailableSortOrders();
		for (int i = 0; i < sorts.length; i++) {
			if (sorts[i] == sortOrder) {
				return i;
			}
		}
		return -1;
	}

	@NonNull
	public static String getDomain(@CName String cName) {
		if (sDomainsMap.isEmpty()) {
			//init
			sDomainsMap.put(Remanga.CNAME, "remanga.org");
			sDomainsMap.put(ReadManga.CNAME, "readmanga.live");
			sDomainsMap.put(MintManga.CNAME, "mintmanga.live");
			sDomainsMap.put(SelfManga.CNAME, "selfmanga.ru");
			sDomainsMap.put(Desu.CNAME, "desu.me");
			sDomainsMap.put(MangaChan.CNAME, "manga-chan.me");
			sDomainsMap.put(MangaLib.CNAME, "mangalib.me");
			sDomainsMap.put(MangaFox.CNAME, "fanfox.net");
			sDomainsMap.put(MangaRaw.CNAME, "mangaraw.xyz");
			sDomainsMap.put(MangaTown.CNAME, "mangatown.com");
			sDomainsMap.put(Anibel.CNAME, "anibel.net");
		}
		return Objects.requireNonNull(sDomainsMap.get(cName));
	}

	public static SharedPreferences getSharedPreferences(@NonNull Context context, @NonNull @CName String cName) {
		return context.getSharedPreferences("prov_" + cName.replace('/','_'), Context.MODE_PRIVATE);
	}

	@Nullable
	public static String getCookie(@NonNull Context context, @NonNull @CName String cName) {
		return getSharedPreferences(context, cName).getString("_cookie", null);
	}

	protected static String url(@NonNull String domain, String subj) {
		return subj.charAt(0) == '/' ? domain + subj : subj;
	}

	protected static String url(@NonNull String domain, String subj1, String subj2) {
		return subj1.charAt(0) == '/' && subj2.charAt(0) == '/' ? domain + subj1 + subj2 : subj1 + subj2;
	}

	@NonNull
	static String urlEncode(@Nullable String text) {
		if (android.text.TextUtils.isEmpty(text)) {
			return "";
		}
		try {
			return URLEncoder.encode(text, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return text;
		}
	}
}
