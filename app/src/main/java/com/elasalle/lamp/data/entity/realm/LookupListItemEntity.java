package com.elasalle.lamp.data.entity.realm;

import com.elasalle.lamp.data.entity.Entity;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

@Entity
public class LookupListItemEntity extends RealmObject {
    @PrimaryKey
    public String id;
    public String value;
}