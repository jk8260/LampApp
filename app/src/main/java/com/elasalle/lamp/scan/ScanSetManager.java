package com.elasalle.lamp.scan;

import android.support.annotation.NonNull;

import com.elasalle.lamp.LampApp;
import com.elasalle.lamp.data.repository.NewScanSetRepository;
import com.elasalle.lamp.data.repository.ScanSetRepository;
import com.elasalle.lamp.model.scan.ScanSet;
import com.elasalle.lamp.model.user.User;
import com.elasalle.lamp.scan.model.NewScanSet;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

public class ScanSetManager {

    private final ScanSetRepository scanSetRepository;
    private final NewScanSetRepository newScanSetRepository;

    @Inject
    public ScanSetManager(ScanSetRepository scanSetRepository, NewScanSetRepository newScanSetRepository) {
        this.scanSetRepository = scanSetRepository;
        this.newScanSetRepository = newScanSetRepository;
    }

    public void saveNewScanSet(@NonNull NewScanSet newScanSet) {
        this.newScanSetRepository.save(newScanSet);
        ScanSet scanSet = newScanSetToScanSet(newScanSet);
        saveScanSet(scanSet);
    }

    private void saveScanSet(ScanSet scanSet) {
        this.scanSetRepository.save(scanSet);
    }

    private ScanSet newScanSetToScanSet(NewScanSet newScanSet) {
        ScanSet scanSet = new ScanSet();
        scanSet.setId(newScanSet.getId());
        scanSet.setCustomerId(newScanSet.getCustomerId());
        scanSet.setName(newScanSet.getName());
        scanSet.setModifiedDate(new Date());
        // No assets or sync date at time of new scan set creation
        // but there should be here in edit mode so copy anyhow
        scanSet.setAssets(newScanSet.getAssets());
        return scanSet;
    }

    public List<ScanSet> getScanSets() {
        final User user = LampApp.getSessionManager().user();
        return this.scanSetRepository.findAllByCustomerId(user.getCustomerId());
    }

    public String getCustomerId() {
        final User user = LampApp.getSessionManager().user();
        return user.isGuest() ? user.getEmail() : user.getCustomerId();
    }

    public NewScanSet getScanSet(String id) {
        return this.newScanSetRepository.findById(id);
    }

    public void delete(ScanSet scanSet) {
        NewScanSet newScanSet = newScanSetRepository.findById(scanSet.getId());
        scanSetRepository.deleteOne(scanSet);
        newScanSetRepository.deleteOne(newScanSet);
    }
}
