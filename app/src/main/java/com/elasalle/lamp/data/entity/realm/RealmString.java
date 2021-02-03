package com.elasalle.lamp.data.entity.realm;

import io.realm.RealmObject;

public class RealmString extends RealmObject {

    public String value;

    public RealmString() {
    }

    public RealmString(String value) {
        this.value = value;
    }

}
