package com.elasalle.lamp.data.entity.realm;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RealmMap extends RealmObject {
    @PrimaryKey
    public String id;
    public RealmList<RealmKeyValuePair> map;
}
