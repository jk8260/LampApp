package com.elasalle.lamp.data.entity.realm;

import com.elasalle.lamp.data.entity.Entity;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

@Entity
public class AssetEntity extends RealmObject {
    @PrimaryKey
    public String id;
    public RealmList<FieldEntity> fields;
    public RealmMap fieldIdValueMap;
}
