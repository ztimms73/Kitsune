package org.xtimms.kitsune.ui.tools.settings.providers;

import android.os.Bundle;

import org.xtimms.kitsune.core.common.base.AppBaseActivity;
import org.xtimms.kitsune.R;
import org.xtimms.kitsune.source.MangaProvider;

import java.util.Objects;

@SuppressWarnings("deprecation")
public class ProviderPreferencesActivity extends AppBaseActivity {

    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.activity_provider_import);
        setSupportActionBar(R.id.toolbar);
        enableHomeAsUp();
        MangaProvider mProvider = MangaProvider.get(this, Objects.requireNonNull(getIntent().getStringExtra("provider.cname")));
        setTitle(mProvider.getName());
        ProviderPreferencesFragment providerPreferencesFragment = new ProviderPreferencesFragment();
        providerPreferencesFragment.setArguments(getIntent().getExtras());
        getFragmentManager().beginTransaction().replace(R.id.frame, providerPreferencesFragment).commit();
    }
}

