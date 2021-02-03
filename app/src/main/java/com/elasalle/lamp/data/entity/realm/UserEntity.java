package com.elasalle.lamp.data.entity.realm;

import com.elasalle.lamp.data.entity.Entity;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

@Entity
public class UserEntity extends RealmObject {
    @PrimaryKey
    public String username;
    public String email;
    public String name;
    public String customerId;
    public RealmList<CustomerEntity> customers;
    public boolean guest;
}
