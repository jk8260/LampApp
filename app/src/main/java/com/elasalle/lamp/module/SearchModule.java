package com.elasalle.lamp.module;

import android.content.Context;

import com.elasalle.lamp.client.LampRestClient;
import com.elasalle.lamp.lookup.LookupManager;
import com.elasalle.lamp.search.AssetDetailManager;
import com.elasalle.lamp.search.SearchAdapter;
import com.elasalle.lamp.search.SearchManager;
import com.elasalle.lamp.security.TokenManager;

import java.lang.ref.WeakReference;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;

@Module
@Singleton
public class SearchModule {

    private final WeakReference<Context> context;

    public SearchModule(Context context) {
        this.context = new WeakReference<>(context);
    }

    @Provides
    SearchAdapter searchAdapter() {
        return new SearchAdapter();
    }

    @Provides
    SearchManager searchManager(LampRestClient lampRestClient, TokenManager tokenManager, AssetDetailManager assetDetailManager) {
        return new SearchManager(lampRestClient, tokenManager, assetDetailManager);
    }

    @Provides
    LookupManager lookupManager(LampRestClient client, TokenManager tokenManager) {
        return new LookupManager(client, tokenManager);
    }

    @Provides
    AssetDetailManager assetDetailManager(TokenManager tokenManager, OkHttpClient client) {
        return new AssetDetailManager(tokenManager, client);
    }
}
