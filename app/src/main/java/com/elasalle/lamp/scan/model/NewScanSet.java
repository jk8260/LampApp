package com.elasalle.lamp.scan.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.elasalle.lamp.model.scan.Asset;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * This class holds the configuration for a scan set and it is
 * used when a new scan set is created.
 * <p>This class is used in conjunction with {@link com.elasalle.lamp.model.scan.ScanSet ScanSet},
 * in particular when a scan set is edited or when an asset is added.
 * <p>TODO: Refactor so that {@link com.elasalle.lamp.scan.model.NewScanSet NewScanSet} is a configuration field of
 * {@link com.elasalle.lamp.model.scan.ScanSet ScanSet}.
 */
public class NewScanSet implements Parcelable {

    private final String id;
    private final String customerId;
    private String name;
    private List<ScanSetField> scanSetFields;
    private List<Asset> assets;

    public NewScanSet(String name, String customerId) {
        this.name = name;
        this.scanSetFields = new LinkedList<>();
        this.id = UUID.randomUUID().toString();
        this.customerId = customerId;
        this.assets = new ArrayList<>();
    }

    public NewScanSet(String name, String id, String customerId) {
        this.name = name;
        this.id = id;
        this.scanSetFields = new LinkedList<>();
        this.customerId = customerId;
        this.assets = new ArrayList<>();
    }

    public NewScanSet(Parcel in) {
        name = in.readString();
        scanSetFields = new LinkedList<>();
        in.readList(scanSetFields, ScanSetField.class.getClassLoader());
        id = in.readString();
        customerId = in.readString();
        assets = new ArrayList<>();
        in.readList(assets, Asset.class.getClassLoader());
    }

    public List<ScanSetField> getScanSetFields() {
        return scanSetFields;
    }

    public void setScanSetFields(List<ScanSetField> scanSetFields) {
        this.scanSetFields = scanSetFields;
    }

    public void addScanSetField(ScanSetField scanSetField) {
        this.scanSetFields.add(scanSetField);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public List<Asset> getAssets() {
        return assets;
    }

    public void setAssets(List<Asset> assets) {
        this.assets = assets;
    }

    public void addAsset(Asset asset) {
        if (assets == null) {
            assets = new ArrayList<>();
        }
        if (asset != null) {
            assets.add(asset);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeList(scanSetFields);
        dest.writeString(id);
        dest.writeString(customerId);
        dest.writeList(assets);
    }

    public static final Creator<NewScanSet> CREATOR = new Creator<NewScanSet>() {
        @Override
        public NewScanSet createFromParcel(Parcel in) {
            return new NewScanSet(in);
        }

        @Override
        public NewScanSet[] newArray(int size) {
            return new NewScanSet[size];
        }
    };
}
