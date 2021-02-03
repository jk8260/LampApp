package com.elasalle.lamp.data.entity.realm;

import com.elasalle.lamp.data.entity.Entity;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

@Entity
public class ScanSetFieldEntity extends RealmObject {
    @PrimaryKey
    public String id;
    public FieldEntity field;
    public String scanFieldLabel;
    public String scanFieldValue;
    public boolean isCarryForward;
    public boolean isShowField;
    public int order;
    public boolean isSelected;
}
