package com.elasalle.lamp.model.search;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.ALWAYS)
@JsonPropertyOrder({
        "id",
        "target",
        "type",
        "attributes"
})
public class Datum implements Parcelable {
    @JsonProperty("id")
    public String id;
    @JsonProperty("target")
    public String target;
    @JsonProperty("type")
    public String type;
    @JsonProperty("attributes")
    public List<Attribute> attributes = new ArrayList<>();
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<>();

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public Datum() {
    }

    public Datum(Parcel in) {
        String[] values = new String[3];
        in.readStringArray(values);
        this.id = values[0];
        this.target = values[1];
        this.type = values[2];
        attributes = new ArrayList<>();
        in.readList(attributes, Attribute.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray( new String[] {
                id,
                target,
                type
        });
        dest.writeList(attributes);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Datum createFromParcel(Parcel in) {
            return new Datum(in);
        }

        public Datum[] newArray(int size) {
            return new Datum[size];
        }
    };

}
