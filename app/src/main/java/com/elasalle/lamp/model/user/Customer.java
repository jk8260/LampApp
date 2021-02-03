package com.elasalle.lamp.model.user;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Generated;

import com.elasalle.lamp.R;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
        "token",
        "customerId",
        "name",
        "logoUrl",
        "fields",
        "privileges",
        "searchTypes"
})
public class Customer {

    @JsonProperty("token")
    private String token;
    @JsonProperty("customerId")
    private String customerId;
    @JsonProperty("name")
    private String name;
    @JsonProperty("logoUrl")
    private String logoUrl;
    @JsonProperty("fields")
    private List<Field> fields = new ArrayList<>();
    @JsonProperty("privileges")
    private List<String> privileges = new ArrayList<>();
    @JsonProperty("searchTypes")
    private List<SearchType> searchTypes = new ArrayList<>();
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<>();

    /**
     * @return The token
     */
    @JsonProperty("token")
    public String getToken() {
        return token;
    }

    /**
     * @param token The token
     */
    @JsonProperty("token")
    public void setToken(String token) {
        this.token = token;
    }

    /**
     * @return The customerId
     */
    @JsonProperty("customerId")
    public String getCustomerId() {
        return customerId;
    }

    /**
     * @param customerId The customerId
     */
    @JsonProperty("customerId")
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
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
     * @return The logoUrl
     */
    @JsonProperty("logoUrl")
    public String getLogoUrl() {
        return logoUrl;
    }

    /**
     * @param logoUrl The logoUrl
     */
    @JsonProperty("logoUrl")
    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    /**
     * @return The fields
     */
    @JsonProperty("fields")
    public List<Field> getFields() {
        return fields;
    }

    /**
     * @param fields The fields
     */
    @JsonProperty("fields")
    public void setFields(List<Field> fields) {
        this.fields = fields;
    }

    /**
     * @return The privileges
     */
    @JsonProperty("privileges")
    public List<String> getPrivileges() {
        return privileges;
    }

    /**
     * @param privileges The privileges
     */
    @JsonProperty("privileges")
    public void setPrivileges(List<String> privileges) {
        this.privileges = privileges;
    }

    /**
     * @return The searchTypes
     */
    @JsonProperty("searchTypes")
    public List<SearchType> getSearchTypes() {
        return searchTypes;
    }

    /**
     * @param searchTypes The searchTypes
     */
    @JsonProperty("searchTypes")
    public void setSearchTypes(List<SearchType> searchTypes) {
        this.searchTypes = searchTypes;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public List<CharSequence> getSearchLabels(Context context) {
        List<CharSequence> searchLabels = new ArrayList<>();
        String prefix = context.getString(R.string.search_filter_prefix) + " ";
        searchLabels.add(prefix + context.getString(R.string.search_filter_everything));
        for (SearchType searchType : searchTypes) {
            searchLabels.add(prefix + searchType.getContentTypeLabel());
        }
        return searchLabels;
    }

    public String removeToken() {
        String token = getToken();
        setToken("");
        return token;
    }

}