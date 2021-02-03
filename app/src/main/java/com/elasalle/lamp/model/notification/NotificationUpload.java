package com.elasalle.lamp.model.notification;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
        "id",
        "read",
        "dateRead",
        "deleted",
        "platform"
})
public class NotificationUpload {

    @JsonProperty("id")
    public String id;
    @JsonProperty("read")
    public Boolean read;
    @JsonProperty("dateRead")
    public String dateRead;
    @JsonProperty("deleted")
    public Boolean deleted;
    @JsonProperty("platform")
    public String platform = "Android";
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}