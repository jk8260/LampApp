package com.elasalle.lamp.data.entity.realm;

import com.elasalle.lamp.data.entity.Entity;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

@Entity
public class NotificationEntity extends RealmObject {
    @PrimaryKey
    public String id;
    public String title;
    public String createdDate;
    public String customer;
    public String target;
    public Boolean read;
    public Boolean deleted;
    public String dateRead;
}
