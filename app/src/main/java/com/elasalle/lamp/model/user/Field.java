package com.elasalle.lamp.model.user;

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

import javax.annotation.Generated;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
        "fieldId",
        "name",
        "type",
        "options",
        "isRequired",
        "target",
        "lastUpdate"
})
public class Field implements Parcelable {

    public static final String KEY_FIELD_VALUE = "fieldValue";
    public static final String LOOKUP_LIST_TYPE = "lookuplist";
    public static final String KEY_LOOKUP_LIST_VALUE_ID = "lookup list value id";
    public static final String KEY_LOOKUP_LIST_VALUE_DISPLAY = "lookup list value display";
    public static final String LOOKUP_LIST_FIELD_ID_DISPLAY_SUFFIX = "-display";

    @JsonProperty("fieldId")
    private String fieldId;
    @JsonProperty("name")
    private String name;
    @JsonProperty("type")
    private String type;
    @JsonProperty("options")
    private List<Object> options = new ArrayList<Object>();
    @JsonProperty("isRequired")
    private Boolean isRequired;
    @JsonProperty("target")
    private String target;
    @JsonProperty("lastUpdate")
    private String lastUpdate;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public Field() {
    }

    public Field(Parcel in) {
        fieldId = in.readString();
        name = in.readString();
        type = in.readString();
        target = in.readString();
        lastUpdate = in.readString();
        isRequired = in.readByte() != 0;
        options = new ArrayList<>();
        in.readList(options, Object.class.getClassLoader());
    }

    /**
     * @return The fieldId
     */
    @JsonProperty("fieldId")
    public String getFieldId() {
        return fieldId;
    }

    /**
     * @param fieldId The fieldId
     */
    @JsonProperty("fieldId")
    public void setFieldId(String fieldId) {
        this.fieldId = fieldId;
    }

    /**
     * @return The name
     */
    @JsonProperty("name")
    public String getName() {
        return name;
    }

    /**
     * @param name The name
     */
    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return The type
     */
    @JsonProperty("type")
    public String getType() {
        return type;
    }

    /**
     * @param type The type
     */
    @JsonProperty("type")
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return The options
     */
    @JsonProperty("options")
    public List<Object> getOptions() {
        return options;
    }

    /**
     * @param options The options
     */
    @JsonProperty("options")
    public void setOptions(List<Object> options) {
        this.options = options;
    }

    /**
     * @return The isRequired
     */
    @JsonProperty("isRequired")
    public Boolean isRequired() {
        return isRequired;
    }

    /**
     * @param isRequired The isRequired
     */
    @JsonProperty("isRequired")
    public void setRequired(Boolean isRequired) {
        this.isRequired = isRequired;
    }

    /**
     * @return The target
     */
    @JsonProperty("target")
    public String getTarget() {
        return target;
    }

    /**
     * @param target The target
     */
    @JsonProperty("target")
    public void setTarget(String target) {
        this.target = target;
    }

    /**
     * @return The lastUpdate
     */
    @JsonProperty("lastUpdate")
    public String getLastUpdate() {
        return lastUpdate;
    }

    /**
     * @param lastUpdate The lastUpdate
     */
    @JsonProperty("lastUpdate")
    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(fieldId);
        dest.writeString(name);
        dest.writeString(type);
        dest.writeString(target);
        dest.writeString(lastUpdate);
        dest.writeByte((byte) (isRequired ? 1 : 0));
        dest.writeList(options);
    }

    public static final Creator<Field> CREATOR = new Creator<Field>() {
        @Override
        public Field createFromParcel(Parcel in) {
            return new Field(in);
        }

        @Override
        public Field[] newArray(int size) {
            return new Field[size];
        }
    };
}