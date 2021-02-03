package com.elasalle.lamp.scan.detail;

import com.elasalle.lamp.model.scan.Asset;
import com.elasalle.lamp.model.scan.ScanSet;
import com.elasalle.lamp.scan.model.NewScanSet;

public interface ScanSetDetailInterface {
    ScanSet getScanSet();
    NewScanSet getConfiguredScanSet();
    void onAssetRemoved(NewScanSet newScanSet);
    void onAssetClick(Asset asset);
}
