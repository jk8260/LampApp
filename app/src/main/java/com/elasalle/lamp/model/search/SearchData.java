package com.elasalle.lamp.model.search;

import android.text.TextUtils;

import com.elasalle.lamp.LampApp;
import com.elasalle.lamp.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class SearchData {

    private final List<SearchItem> results;
    private final String footerText;

    public SearchData(SearchResponse searchResponse, String filter) {
        this.footerText = searchResponse.footerText;
        this.results = new ArrayList<>();
        map(searchResponse, filter);
    }

    private void map(SearchResponse searchResponse, String filter) {
        if (TextUtils.isEmpty(filter) || LampApp.getInstance().getString(R.string.search_filter_everything).equals(filter)) {
            mapForEverythingFilter(searchResponse);
        } else {
            addFilterAsHeader(filter);
            if (searchResponse != null && searchResponse.sections != null) {
                for (final Section section : searchResponse.sections) {
                    addSectionFields(section);
                }
            }
        }
    }

    private void addFilterAsHeader(final String filter) {
        addSectionHeader(filter);
    }

    private void mapForEverythingFilter(final SearchResponse searchResponse) {
        if (searchResponse != null && searchResponse.sections != null) {
            for (final Section section : searchResponse.sections) {
                addSection(section);
            }
        }
    }

    private void addSection(final Section section) {
        String description = section.rowDesc != null ? " (" + section.rowDesc + ")" : "";
        addSectionHeader(section.sectionName + description);
        addSectionFields(section);
    }

    private void addSectionFields(final Section section) {
        for (final Datum datum : section.data) {
            final SearchItem searchItem = new SearchItem();
            final HashMap<String, String> fields = new LinkedHashMap<>();
            for (final Attribute attribute : datum.attributes) {
                fields.put(attribute.label, attribute.value);
            }
            searchItem.setFields(fields);
            searchItem.setData(datum);
            results.add(searchItem);
        }
    }

    private void addSectionHeader(final String sectionName) {
        final SearchItem searchItemHeader = new SearchItem();
        final HashMap<String, String> header = new LinkedHashMap<>();
        header.put("header", sectionName);
        searchItemHeader.setHeader(header);
        results.add(searchItemHeader);
    }

    public List<SearchItem> getResults() {
        return Collections.unmodifiableList(results);
    }

    public String getFooterText() {
        return footerText;
    }
}
