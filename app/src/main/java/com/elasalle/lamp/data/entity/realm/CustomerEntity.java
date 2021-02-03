package com.elasalle.lamp.data.entity.realm;

import com.elasalle.lamp.data.entity.Entity;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

@Entity
public class CustomerEntity extends RealmObject {
    @PrimaryKey
    public String customerId;
    public String name;
    public String logoUrl;
    public RealmList<FieldEntity> fields;
    public RealmList<RealmString> privileges;
    public RealmList<SearchTypeEntity> searchTypes;
}
