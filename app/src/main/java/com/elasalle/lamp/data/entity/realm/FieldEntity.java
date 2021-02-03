package com.elasalle.lamp.data.entity.realm;

import com.elasalle.lamp.data.entity.Entity;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

@Entity
public class FieldEntity extends RealmObject {
    @PrimaryKey
    public String fieldId;
    public String name;
    public String type;
    public RealmList<RealmString> options;
    public boolean isRequired;
    public String target;
    public String lastUpdate;
}