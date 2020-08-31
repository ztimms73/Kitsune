package org.xtimms.kitsune.source;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;
import org.xtimms.kitsune.utils.network.NetworkUtils;
import org.xtimms.kitsune.core.MangaStatus;
import org.xtimms.kitsune.core.models.MangaDetails;
import org.xtimms.kitsune.core.models.MangaHeader;
import org.xtimms.kitsune.core.models.MangaPage;

import java.io.IOException;
import java.util.ArrayList;

public final class BoredomSociety extends MangaProvider {

    public static final String CNAME = "network/boredomsociety.xyz";

    public BoredomSociety(Context context) {
        super(context);
    }

    @NonNull
    @Override
    public ArrayList<MangaHeader> query(@Nullable String search, int page, int sortOrder, int additionalSortOrder, @NonNull String[] genres, @NonNull String[] types) throws Exception {
        String[] genre = genres;
        if (genres.length == 0)
            genre = null;
        return getList();
    }

    protected ArrayList<MangaHeader> getList() throws IOException, JSONException {
        JSONObject ja = NetworkUtils.getJSONObject("https://boredomsociety.xyz/api/titles/");
        ArrayList<MangaHeader> list = new ArrayList<>(ja.length());
        for (int i = 0; i < ja.length(); i++) {
            // TODO
            JSONObject jo = ja.getJSONObject(String.valueOf(i));
            list.add(new MangaHeader(
                    jo.getString("title_name"),
                    "",
                    "",
                    "",
                    jo.getString("cover_url"),
                    CNAME,
                    MangaStatus.STATUS_UNKNOWN,
                    (byte) 0
            ));
        }
        return list;
    }

    @NonNull
    @Override
    public MangaDetails getDetails(MangaHeader header) {
        //noinspection ConstantConditions
        return null;
    }

    @NonNull
    @Override
    public ArrayList<MangaPage> getPages(String chapterUrl) {
        //noinspection ConstantConditions
        return null;
    }

    @Override
    public String getPageImage(MangaPage mangaPage) {
        return mangaPage.url;
    }

}
