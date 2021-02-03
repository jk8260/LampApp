package com.elasalle.lamp.model.user;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Generated;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
        "contentTypeCode",
        "contentTypeLabel"
})
public class SearchType {

    @JsonProperty("contentTypeCode")
    private String contentTypeCode;
    @JsonProperty("contentTypeLabel")
    private String contentTypeLabel;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * @return The contentTypeCode
     */
    @JsonProperty("contentTypeCode")
    public String getContentTypeCode() {
        return contentTypeCode;
    }

    /**
     * @param contentTypeCode The contentTypeCode
     */
    @JsonProperty("contentTypeCode")
    public void setContentTypeCode(String contentTypeCode) {
        this.contentTypeCode = contentTypeCode;
    }

    /**
     * @return The contentTypeLabel
     */
    @JsonProperty("contentTypeLabel")
    public String getContentTypeLabel() {
        return contentTypeLabel;
    }

    /**
     * @param contentTypeLabel The contentTypeLabel
     */
    @JsonProperty("contentTypeLabel")
    public void setContentTypeLabel(String contentTypeLabel) {
        this.contentTypeLabel = contentTypeLabel;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}