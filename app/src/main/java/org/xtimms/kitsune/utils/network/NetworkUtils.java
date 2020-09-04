package org.xtimms.kitsune.utils.network;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.xtimms.kitsune.BuildConfig;
import org.xtimms.kitsune.utils.FileLogger;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HttpsURLConnection;

import info.guardianproject.netcipher.NetCipher;
import info.guardianproject.netcipher.client.StrongOkHttpClientBuilder;
import info.guardianproject.netcipher.proxy.OrbotHelper;
import okhttp3.CacheControl;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import timber.log.Timber;

@SuppressWarnings("ALL")
public class NetworkUtils {

	public static final String HTTP_GET = "GET";
	public static final String HTTP_POST = "POST";
	public static final String HTTP_PUT = "PUT";
	public static final String HTTP_DELETE = "DELETE";

	public static final String HEADER_USER_AGENT = "User-Agent";
	public static final String HEADER_REFERER = "Referer";

	public static final String TAG = "NetworkUtils";
	public static final String TAG_REQUEST = TAG + "-request";
	public static final String TAG_RESPONSE = TAG + "-response";
	public static final String TAG_ERROR = TAG + "-error";

	public static final String USER_AGENT_DEFAULT = "Mozilla/5.0 (X11; Fedora; Linux x86_64; rv:57.0) Gecko/20100101 Firefox/57.0";

	private static final CacheControl CACHE_CONTROL_DEFAULT = new CacheControl.Builder().maxAge(10, TimeUnit.MINUTES).build();
	public static final Headers HEADERS_DEFAULT = new Headers.Builder()
			.add(HEADER_USER_AGENT, USER_AGENT_DEFAULT)
			.build();

	private static OkHttpClient sHttpClient = null;

	@NonNull
	private static OkHttpClient.Builder getClientBuilder() {
		return new OkHttpClient.Builder()
				/*.connectTimeout(1, TimeUnit.SECONDS)
				.readTimeout(1, TimeUnit.SECONDS)*/
				.addInterceptor(CookieStore.getInstance())
				.addInterceptor(new CloudflareInterceptor());
	}

	public static void init(Context context, boolean useTor) {
		OkHttpClient.Builder builder = getClientBuilder();
		if (useTor && OrbotHelper.get(context).init()) {
			try {
				StrongOkHttpClientBuilder.forMaxSecurity(context)
						.applyTo(builder, new Intent()
								.putExtra(OrbotHelper.EXTRA_STATUS, "ON")); //TODO wtf
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		sHttpClient = builder.build();
	}

	@NonNull
	public static String getString(@NonNull String url) throws IOException {
		return getString(url, HEADERS_DEFAULT);
	}

	@NonNull
	public static String getString(@NonNull String url, @NonNull Headers headers) throws IOException {
		Request.Builder builder = new Request.Builder()
				.url(url)
				.headers(headers)
				.cacheControl(CACHE_CONTROL_DEFAULT)
				.get();
		try (Response response = sHttpClient.newCall(builder.build()).execute()) {
			ResponseBody body = response.body();
			if (body == null) {
				throw new IOException("ResponseBody is null");
			} else {
				return body.string();
			}
		}
	}

	public static Document httpGet(@NonNull String url, @Nullable String cookie) throws IOException, KeyManagementException, NoSuchAlgorithmException {
		SSLSocketFactoryExtended factory = null;
		try {
			factory = new SSLSocketFactoryExtended();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		}
		final SSLSocketFactoryExtended finalFactory = factory;
		InputStream is = null;
		try {
			requestLog(url, cookie);
			HttpURLConnection con = NetCipher.getHttpURLConnection(url);
			if (con instanceof HttpsURLConnection) {
				((HttpsURLConnection) con).setDefaultSSLSocketFactory(finalFactory);
				con.setRequestProperty("charset", "utf-8");
			}
			//con.setDoOutput(true);
			if (!TextUtils.isEmpty(cookie)) {
				con.setRequestProperty("Cookie", cookie);
			}
			con.setConnectTimeout(15000);
			is = con.getInputStream();
			return parseHtml(url, is, con);
		} catch (Exception error) {
			Timber.tag(TAG_ERROR).e(error);
			FileLogger.getInstance().report("HTTP", error);
			throw error;
		} finally {
			if (is != null) {
				is.close();
			}
		}
	}

	public static Request httpPost(String url, Headers headers, RequestBody body) {
		return new Request.Builder()
				.url(url)
				.post(body)
				.headers(headers)
				.cacheControl(CACHE_CONTROL_DEFAULT)
				.build();
	}

	private static void requestLog(String url, String cookie) {
		Timber.tag(TAG_REQUEST).d("request: %s", url);
		Timber.tag(TAG_REQUEST).d("cookie: %s", cookie);
	}

	private static Document parseHtml(@NonNull final String url, final InputStream is,
									  final HttpURLConnection con) throws IOException {
		Document document = Jsoup.parse(is, con.getContentEncoding(), url);
		if (BuildConfig.DEBUG) {
			Timber.tag(TAG_RESPONSE).d(document.html());
		}
		return document;
	}

	@NonNull
	public static Document getDocument(@NonNull String url) throws IOException {
		return getDocument(url, HEADERS_DEFAULT);
	}

	@NonNull
	public static Document getDocument(@NonNull String url, @NonNull Headers headers) throws IOException {
		return Jsoup.parse(getString(url, headers), url);
	}

	@NonNull
	public static JSONObject getJSONObject(@NonNull String url) throws IOException, JSONException {
		return new JSONObject(getString(url));
	}


	@NonNull
	public static OkHttpClient getHttpClient() {
		return sHttpClient != null ? sHttpClient : getClientBuilder().build();
	}

	public static int getContentLength(Response response) {
		String header = response.header("content-length");
		if (header == null) {
			return -1;
		} else {
			try {
				return Integer.parseInt(header);
			} catch (NumberFormatException e) {
				e.printStackTrace();
				return -1;
			}
		}
	}

	public static boolean isNetworkAvailable(Context context) {
		return isNetworkAvailable(context, true);
	}

	public static boolean isNetworkAvailable(Context context, boolean allowMetered) {
		final ConnectivityManager manager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (manager == null) {
			return false;
		}
		final NetworkInfo network = manager.getActiveNetworkInfo();
		return network != null && network.isConnected() && (allowMetered || isNotMetered(network));
	}

	private static boolean isNotMetered(NetworkInfo networkInfo) {
		if(networkInfo.isRoaming()) return false;
		final int type = networkInfo.getType();
		return type == ConnectivityManager.TYPE_WIFI
				|| type == ConnectivityManager.TYPE_WIMAX
				|| type == ConnectivityManager.TYPE_ETHERNET;
	}

	@NonNull
	public static String getDomainWithScheme(@NonNull String url) {
		int p = url.indexOf('/', 10);
		return url.substring(0, p);
	}

	public static boolean checkConnection(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		assert cm != null;
		NetworkInfo ni = cm.getActiveNetworkInfo();
		return ni != null && ni.isAvailable() && ni.isConnected();
	}

	public static boolean checkConnection(Context context, boolean onlyWiFi) {
		ConnectivityManager cm = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cm == null) return false;
		NetworkInfo ni = cm.getActiveNetworkInfo();
		return ni != null && ni.isConnected() && (!onlyWiFi || ni.getType() == ConnectivityManager.TYPE_WIFI);
	}

	@Nullable
	public static CookieParser authorize(String url, String... data) {
		SSLSocketFactoryExtended factory = null;
		try {
			factory = new SSLSocketFactoryExtended();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		}
		final SSLSocketFactoryExtended finalFactory = factory;
		DataOutputStream out = null;
		try {
			HttpURLConnection con = NetCipher.getHttpURLConnection(url);
			if (con instanceof HttpsURLConnection) {
				((HttpsURLConnection) con).setDefaultSSLSocketFactory(finalFactory);
				con.setRequestProperty("charset", "utf-8");
			}
			con.setConnectTimeout(15000);
			con.setRequestMethod("POST");
			con.setDoOutput(true);
			con.setInstanceFollowRedirects(true);
			out = new DataOutputStream(con.getOutputStream());
			out.writeBytes(makeQuery(data));
			out.flush();
			con.connect();
			if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
				return new CookieParser(Objects.requireNonNull(con.getHeaderFields().get("Set-Cookie")));
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static RESTResponse restQuery(String url, @Nullable String token, String method, String... data) {
		SSLSocketFactoryExtended factory = null;
		try {
			factory = new SSLSocketFactoryExtended();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		}
		final SSLSocketFactoryExtended finalFactory = factory;
		BufferedReader reader = null;
		try {
			HttpURLConnection con = NetCipher.getHttpURLConnection(
					HTTP_GET.equals(method) ? url + "?" + makeQuery(data) : url
			);
			if (con instanceof HttpsURLConnection) {
				((HttpsURLConnection) con).setDefaultSSLSocketFactory(finalFactory);
				con.setRequestProperty("charset", "utf-8");
			}
			if (!TextUtils.isEmpty(token)) {
				con.setRequestProperty("X-AuthToken", token);
			}
			con.setConnectTimeout(15000);
			con.setRequestMethod(method);
			if (!HTTP_GET.equals(method)) {
				con.setDoOutput(true);
				DataOutputStream out = new DataOutputStream(con.getOutputStream());
				out.writeBytes(NetworkUtils.makeQuery(data));
				out.flush();
				out.close();
			}
			int respCode = con.getResponseCode();
			reader = new BufferedReader(new InputStreamReader(isOk(respCode) ? con.getInputStream() : con.getErrorStream()));
			StringBuilder out = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				out.append(line);
			}
			return new RESTResponse(new JSONObject(out.toString()), respCode);
		} catch (Exception e) {
			e.printStackTrace();
			return RESTResponse.fromThrowable(e);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@NonNull
	private static String makeQuery(@NonNull String[] data) throws UnsupportedEncodingException {
		StringBuilder query = new StringBuilder();
		for (int i = 0; i < data.length; i = i + 2) {
			query.append(URLEncoder.encode(data[i], "UTF-8")).append("=").append(URLEncoder.encode(data[i + 1], "UTF-8")).append("&");
		}
		if (query.length() > 1) {
			query.deleteCharAt(query.length()-1);
		}
		return query.toString();
	}

	private static boolean isOk(int responseCode) {
		return responseCode >= 200 && responseCode < 300;
	}
}
