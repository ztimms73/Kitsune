package org.xtimms.kitsune.ui.tools.settings.providers;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceFragment;

import androidx.annotation.Nullable;

import org.xtimms.kitsune.source.MangaProvider;

@SuppressWarnings("ALL")
public class ProviderPreferencesFragment extends PreferenceFragment {

    public void onCreate(@Nullable Bundle paramBundle) {
        super.onCreate(paramBundle);
        Context context = getActivity();
        String str = getArguments().getString("provider.cname");
        assert str != null;
        MangaProvider mProvider = MangaProvider.get(context, str);
    }
}
