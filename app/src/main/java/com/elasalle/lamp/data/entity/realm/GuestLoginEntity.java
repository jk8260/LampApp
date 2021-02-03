package com.elasalle.lamp.data.entity.realm;

import com.elasalle.lamp.data.entity.Entity;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

@Entity
public class GuestLoginEntity extends RealmObject {
    @PrimaryKey
    public String timestamp;
    public String email;
    public String platform;
    public String appVersion;
}
