package com.elasalle.lamp.component;

import com.elasalle.lamp.lookup.LookupFragment;
import com.elasalle.lamp.module.SearchModule;
import com.elasalle.lamp.module.ServicesModule;
import com.elasalle.lamp.module.UIModule;
import com.elasalle.lamp.module.UserModule;
import com.elasalle.lamp.search.AssetDetailActivity;
import com.elasalle.lamp.search.AssetListActivity;
import com.elasalle.lamp.search.SearchFragment;

import javax.inject.Singleton;

import dagger.Component;

@Component(modules = {SearchModule.class, ServicesModule.class, UserModule.class, UIModule.class})
@Singleton
public interface SearchComponent {
    void inject(SearchFragment searchFragment);
    void inject(LookupFragment lookupFragment);
    void inject(AssetDetailActivity assetDetailActivity);
    void inject(AssetListActivity assetListActivity);
}
