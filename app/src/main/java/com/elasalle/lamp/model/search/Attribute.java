package com.elasalle.lamp.model.search;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.ALWAYS)
@JsonPropertyOrder({
        "label",
        "value",
        "decoration",
        "display"
})
public class Attribute implements Parcelable {

    @JsonProperty("label")
    public String label;
    @JsonProperty("value")
    public String value;
    @JsonProperty("decoration")
    public String decoration;
    @JsonProperty("display")
    public boolean display;
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

    public Attribute() {
    }

    public Attribute(Parcel in) {
        String[] values = new String[3];
        in.readStringArray(values);
        this.label = values[0];
        this.value = values[1];
        this.decoration = values[2];
        this.display = in.readByte() == 1;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray( new String[] {
                label,
                value,
                decoration
        });
        dest.writeByte((byte) (display ? 1 : 0));
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Attribute createFromParcel(Parcel in) {
            return new Attribute(in);
        }

        public Attribute[] newArray(int size) {
            return new Attribute[size];
        }
    };
}
