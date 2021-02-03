package com.elasalle.lamp.model.search;

import java.util.HashMap;

public class SearchItem {

    private boolean isHeader;
    private HashMap<String, String> fields;
    private Datum data;

    public HashMap<String, String> getFields() {
        return fields;
    }

    public void setFields(HashMap<String, String> fields) {
        this.fields = fields;
        this.isHeader = false;
    }

    public boolean isHeader() {
        return isHeader;
    }

    public void setHeader(HashMap<String, String> header) {
        this.fields = header;
        this.isHeader = true;
    }

    public void setData(Datum data) {
        this.data = data;
    }

    public Datum getData() {
        return data;
    }
}
