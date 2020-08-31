package org.xtimms.kitsune.core.storage;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.text.TextUtils;
import android.util.SparseBooleanArray;

import org.xtimms.kitsune.source.MangaFox;
import org.xtimms.kitsune.source.Remanga;
import org.xtimms.kitsune.utils.CollectionsUtils;
import org.xtimms.kitsune.core.models.ProviderHeader;
import org.xtimms.kitsune.source.Desu;
import org.xtimms.kitsune.source.MangaChan;
import org.xtimms.kitsune.source.MintManga;
import org.xtimms.kitsune.source.ReadManga;
import org.xtimms.kitsune.source.SelfManga;

import java.util.ArrayList;
import java.util.Iterator;

public final class ProvidersStore {

	private static final ProviderHeader[] sProviders = new ProviderHeader[]{
			new ProviderHeader(Remanga.CNAME, Remanga.DNAME, "Yet another manga catalog..."),
			new ProviderHeader(ReadManga.CNAME, ReadManga.DNAME, "Один из самых популярных источников манги в СНГ."),						//0
			new ProviderHeader(MintManga.CNAME, MintManga.DNAME, "Источник манги для взрослых. Присутствует яой. Пожалуйста, отключите этот источник если вам меньше 18 лет. Также рекомендуется его отключить, если в рекомендациях попадается яой манга."),						//1
			new ProviderHeader(SelfManga.CNAME, SelfManga.DNAME, "На этом источнике размещается только русская авторская манга и журналы о манге."),						//2
			new ProviderHeader(Desu.CNAME, Desu.DNAME, "Один из лучших каталогов манги. Хорош тем, что на сайт быстро заливают новые главы."),									//3
			new ProviderHeader(MangaChan.CNAME, MangaChan.DNAME, "Тоже хороший источник. Но часто падает, из-за чего бывает недоступен от нескольких часов до пары дней."),
			//new ProviderHeader(MangaLib.CNAME, MangaLib.DNAME, "test"),				//5
			new ProviderHeader(MangaFox.CNAME, MangaFox.DNAME, "English manga catalog"),
			//new ProviderHeader(MangaRaw.CNAME, MangaRaw.DNAME),
			//new ProviderHeader(MangaTown.CNAME, MangaTown.DNAME, "test"),
			//new ProviderHeader(Anibel.CNAME, Anibel.DNAME, "test")
	};

	private final SharedPreferences mPreferences;

	public ProvidersStore(Context context) {
		mPreferences = context.getSharedPreferences("providers", Context.MODE_PRIVATE);
	}

	public ArrayList<ProviderHeader> getAllProvidersSorted() {
		final ArrayList<ProviderHeader> list = new ArrayList<>(sProviders.length);
		final int[] order = CollectionsUtils.convertToInt(mPreferences.getString("order", "").split("\\|"), -1);
		for (int o : order) {
			ProviderHeader h = CollectionsUtils.getOrNull(sProviders, o);
			if (h != null) {
				list.add(h);
			}
		}
		for (ProviderHeader h : sProviders) {
			if (!list.contains(h)) {
				list.add(h);
			}
		}
		return list;
	}

	public ArrayList<ProviderHeader> getUserProviders() {
		final ArrayList<ProviderHeader> list = getAllProvidersSorted();
		final int[] disabled = getDisabledIds();
		Iterator<ProviderHeader> iterator = list.iterator();
		while (iterator.hasNext()) {
			ProviderHeader h = iterator.next();
			if (CollectionsUtils.indexOf(disabled, h.hashCode()) != -1) {
				iterator.remove();
			}
		}
		return list;
	}

	@Nullable
	public static ProviderHeader getByCName(String cName) {
		for (ProviderHeader o : sProviders) {
			if (cName.equals(o.cName)) {
				return o;
			}
		}
		return null;
	}

	public void save(ArrayList<ProviderHeader> providers, SparseBooleanArray enabled) {
		final Integer[] order = new Integer[providers.size()];
		for (int i = 0; i < sProviders.length; i++) {
			ProviderHeader h = sProviders[i];
			int p = providers.indexOf(h);
			if (p != -1) {
				order[i] = p;
			}
		}
		final ArrayList<Integer> disabled = new ArrayList<>();
		for (int i=0;i<providers.size();i++) {
			if (!enabled.get(i, true)) {
				disabled.add(providers.get(i).hashCode());
			}
		}
		mPreferences.edit()
				.putString("order", TextUtils.join("|", order))
				.putString("disabled", TextUtils.join("|", disabled))
				.apply();
	}

	@NonNull
	public int[] getDisabledIds() {
		return CollectionsUtils.convertToInt(mPreferences.getString("disabled", "").split("\\|"), -1);
	}
}
