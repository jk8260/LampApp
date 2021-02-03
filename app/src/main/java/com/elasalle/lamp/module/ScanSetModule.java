package com.elasalle.lamp.module;

import com.elasalle.lamp.client.LampRestClient;
import com.elasalle.lamp.data.repository.NewScanSetRepository;
import com.elasalle.lamp.data.repository.ScanSetRepository;
import com.elasalle.lamp.scan.ScanSetAdapter;
import com.elasalle.lamp.scan.ScanSetManager;
import com.elasalle.lamp.scan.asset.AddAssetManager;
import com.elasalle.lamp.scan.detail.ScanSetDetailManager;
import com.elasalle.lamp.scan.detail.ScanSetSyncManager;
import com.elasalle.lamp.scan.newset.NewScanSetDialogFragment;
import com.elasalle.lamp.scan.newset.RenameScanSetDialogFragment;
import com.elasalle.lamp.security.TokenManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
@Singleton
public class ScanSetModule {

    @Provides
    ScanSetManager scanSetManager(ScanSetRepository scanSetRepository, NewScanSetRepository newScanSetRepository) {
        return new ScanSetManager(scanSetRepository, newScanSetRepository);
    }

    @Provides
    ScanSetAdapter scanSetAdapter() {
        return new ScanSetAdapter();
    }

    @Provides
    NewScanSetDialogFragment newScanSetDialogFragment() {
        return new NewScanSetDialogFragment();
    }

    @Provides
    RenameScanSetDialogFragment renameScanSetDialogFragment() {
        return new RenameScanSetDialogFragment();
    }

    @Provides
    ScanSetDetailManager scanSetDetailManager(ScanSetRepository scanSetRepository, NewScanSetRepository newScanSetRepository) {
        return new ScanSetDetailManager(scanSetRepository, newScanSetRepository);
    }

    @Provides
    AddAssetManager addAssetManager(ScanSetRepository scanSetRepository, NewScanSetRepository newScanSetRepository) {
        return new AddAssetManager(scanSetRepository, newScanSetRepository);
    }

    @Provides
    ScanSetSyncManager scanSetSyncManager(NewScanSetRepository newScanSetRepository, ScanSetRepository scanSetRepository, LampRestClient client, TokenManager tokenManager) {
        return new ScanSetSyncManager(newScanSetRepository, scanSetRepository, client, tokenManager);
    }
}
