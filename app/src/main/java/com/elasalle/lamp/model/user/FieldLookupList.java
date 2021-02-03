package com.elasalle.lamp.model.user;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class FieldLookupList implements Parcelable {

    private final String id;
    private final Field field;
    private List<LookupListItem> lookupListItems;

    public FieldLookupList(Field field) {
        this.field = field;
        this.id = field.getFieldId();
        this.lookupListItems = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public Field getField() {
        return field;
    }

    public List<LookupListItem> getLookupListItems() {
        return lookupListItems;
    }

    public void setLookupListItems(List<LookupListItem> lookupListItems) {
        this.lookupListItems = lookupListItems;
    }

    public FieldLookupList(Parcel in) {
        id = in.readString();
        field = in.readParcelable(Field.class.getClassLoader());
        lookupListItems = new ArrayList<>();
        in.readList(lookupListItems, LookupListItem.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeParcelable(field, 0);
        dest.writeList(lookupListItems);
    }

    public static final Creator<FieldLookupList> CREATOR = new Creator<FieldLookupList>() {
        @Override
        public FieldLookupList createFromParcel(Parcel in) {
            return new FieldLookupList(in);
        }

        @Override
        public FieldLookupList[] newArray(int size) {
            return new FieldLookupList[size];
        }
    };
}
