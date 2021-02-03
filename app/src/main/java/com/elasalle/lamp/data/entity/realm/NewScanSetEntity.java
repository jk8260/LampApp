package com.elasalle.lamp.data.entity.realm;

import com.elasalle.lamp.data.entity.Entity;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

@Entity
public class NewScanSetEntity extends RealmObject {
    @PrimaryKey
    public String id;
    public String customerId;
    public String name;
    public RealmList<ScanSetFieldEntity> scanSetFields;
    public RealmList<AssetEntity> assets;
}
