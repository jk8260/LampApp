package com.elasalle.lamp.model.scan;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.elasalle.lamp.model.user.Field;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Asset implements Parcelable {

    private String id;
    private List<Field> fields;
    private Map<String, String> fieldIdValueMap;

    public Asset() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Field> getFields() {
        return fields;
    }

    public void addField(Field field) {
        if (fields == null) {
            fields = new LinkedList<>();
        }
        fields.add(field);
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }

    public Map<String, String> getFieldIdValueMap() {
        return fieldIdValueMap;
    }

    public void setFieldIdValueMap(Map<String, String> fieldIdValueMap) {
        this.fieldIdValueMap = fieldIdValueMap;
    }

    public void addFieldValue(String fieldId, String fieldValue) {
        if (fieldIdValueMap == null) {
            fieldIdValueMap = new HashMap<>();
        }
        fieldIdValueMap.put(fieldId, fieldValue);
    }

    public Asset(Parcel in) {
        id = in.readString();
        fields = new ArrayList<>();
        in.readList(fields, Field.class.getClassLoader());
        @SuppressLint("ParcelClassLoader") Bundle bundle = in.readBundle();
        //noinspection unchecked
        fieldIdValueMap = (HashMap<String, String>)bundle.getSerializable("map");
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeList(fields);
        Bundle bundle = new Bundle();
        bundle.putSerializable("map", (HashMap<String, String>)fieldIdValueMap);
        dest.writeBundle(bundle);
    }

    public static final Creator<Asset> CREATOR = new Creator<Asset>() {
        @Override
        public Asset createFromParcel(Parcel in) {
            return new Asset(in);
        }

        @Override
        public Asset[] newArray(int size) {
            return new Asset[size];
        }
    };
}
