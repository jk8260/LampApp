package com.elasalle.lamp.scan.asset;

import com.elasalle.lamp.data.repository.NewScanSetRepository;
import com.elasalle.lamp.data.repository.ScanSetRepository;
import com.elasalle.lamp.model.scan.ScanSet;
import com.elasalle.lamp.scan.model.NewScanSet;

import java.util.Date;

import javax.inject.Inject;

public class AddAssetManager {

    private final ScanSetRepository scanSetRepository;
    private final NewScanSetRepository newScanSetRepository;

    @Inject
    public AddAssetManager(ScanSetRepository scanSetRepository, NewScanSetRepository newScanSetRepository) {
        this.scanSetRepository = scanSetRepository;
        this.newScanSetRepository = newScanSetRepository;
    }

    NewScanSet getConfiguredScanSet(String id) {
        return newScanSetRepository.findById(id);
    }

    public void updateScanSet(NewScanSet newScanSet) {
        ScanSet scanSet = scanSetRepository.findById(newScanSet.getId());
        scanSet.setAssets(newScanSet.getAssets());
        scanSet.setModifiedDate(new Date());
        newScanSetRepository.save(newScanSet);
        scanSetRepository.save(scanSet);
    }
}
