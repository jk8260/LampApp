package com.elasalle.lamp.component;

import com.elasalle.lamp.module.DataModule;
import com.elasalle.lamp.module.ScanSetModule;
import com.elasalle.lamp.module.ServicesModule;
import com.elasalle.lamp.module.UserModule;
import com.elasalle.lamp.scan.asset.AddAssetActivity;
import com.elasalle.lamp.scan.asset.AddAssetLookupListDialogFragment;
import com.elasalle.lamp.scan.detail.ScanSetDetailActivity;
import com.elasalle.lamp.scan.newset.NewScanSetActivity;
import com.elasalle.lamp.scan.ScanFragment;

import javax.inject.Singleton;

import dagger.Component;

@Component(modules = { ScanSetModule.class, DataModule.class, UserModule.class, ServicesModule.class})
@Singleton
public interface ScanSetComponent {
    void inject(ScanFragment scanFragment);
    void inject(NewScanSetActivity newScanSetActivity);
    void inject(ScanSetDetailActivity scanSetDetailActivity);
    void inject(AddAssetActivity addAssetActivity);
    void inject(AddAssetLookupListDialogFragment addAssetLookupListDialogFragment);
}
