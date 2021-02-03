package com.elasalle.lamp.model.scan;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This class holds the scan set data.
 * <p>This class is used in conjunction with {@link com.elasalle.lamp.scan.model.NewScanSet NewScanSet},
 * in particular when a scan set is edited or when an asset is added.
 * <p>TODO: Refactor so that {@link com.elasalle.lamp.scan.model.NewScanSet NewScanSet} is a configuration field of
 * {@link com.elasalle.lamp.model.scan.ScanSet ScanSet}.
 */
public class ScanSet implements Parcelable {

    private String id;
    private String customerId;
    private String name;
    private List<Asset> assets;
    private Date modifiedDate;
    private Date syncDate;

    public ScanSet() {
    }

    public int getNumberOfAssets() {
        return assets == null ? 0 : assets.size();
    }

    public String getName() {
        return name;
    }

    public Date getModifiedDate() {
        return modifiedDate;
    }

    public Date getSyncDate() {
        return syncDate;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public void setSyncDate(Date syncDate) {
        this.syncDate = syncDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setAssets(List<Asset> assets) {
        this.assets = assets;
    }

    public List<Asset> getAssets() {
        return assets;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public ScanSet(Parcel in) {
        id = in.readString();
        customerId = in.readString();
        name = in.readString();
        long modifiedDateLongValue = in.readLong();
        if (modifiedDateLongValue == 0) {
            modifiedDate = null;
        } else {
            modifiedDate = new Date(modifiedDateLongValue);
        }
        long syncDateLongValue = in.readLong();
        if (syncDateLongValue == 0) {
            syncDate = null;
        } else {
            syncDate = new Date(syncDateLongValue);
        }
        assets = new ArrayList<>();
        in.readList(assets, Asset.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(customerId);
        dest.writeString(name);
        dest.writeLong(modifiedDate != null ? modifiedDate.getTime() : 0L);
        dest.writeLong(syncDate != null ? syncDate.getTime() : 0L);
        dest.writeList(assets);
    }

    public static final Creator<ScanSet> CREATOR = new Creator<ScanSet>() {
        @Override
        public ScanSet createFromParcel(Parcel in) {
            return new ScanSet(in);
        }

        @Override
        public ScanSet[] newArray(int size) {
            return new ScanSet[size];
        }
    };
}
