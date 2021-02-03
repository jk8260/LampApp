package com.elasalle.lamp.model.search;

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
        "sectionNameId",
        "sectionName",
        "rowDesc",
        "data"
})
public class Section {
    @JsonProperty("sectionNameId")
    public String sectionNameId;
    @JsonProperty("sectionName")
    public String sectionName;
    @JsonProperty("rowDesc")
    public String rowDesc;
    @JsonProperty("data")
    public List<Datum> data = new ArrayList<>();
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
}
