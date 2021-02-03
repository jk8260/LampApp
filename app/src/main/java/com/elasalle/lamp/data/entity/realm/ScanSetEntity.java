package com.elasalle.lamp.data.entity.realm;

import com.elasalle.lamp.data.entity.Entity;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

@Entity
public class ScanSetEntity extends RealmObject {
    @PrimaryKey
    public String id;
    public String customerId;
    public String name;
    public RealmList<AssetEntity> assets;
    public Date modifiedDate;
    public Date syncDate;
}
