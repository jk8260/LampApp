package com.elasalle.lamp.data.entity.realm;

import com.elasalle.lamp.data.entity.Entity;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

@Entity
public class FieldLookupListEntity extends RealmObject {
    @PrimaryKey
    public String id;
    public FieldEntity field;
    public RealmList<LookupListItemEntity> lookupListItems;
}
