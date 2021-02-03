package com.elasalle.lamp.scan.asset;

import com.elasalle.lamp.scan.model.NewScanSet;

public interface AddAssetInterface {
    NewScanSet getScanSet();
    void onScanSetUpdatedWithNewAsset();
    void onScanSetAssetUpdated();
    void onInputFieldSelected(String fieldName);
    void onInputFieldDeselected();
    boolean isAssetEdit();
    String getAssetEditId();
}
