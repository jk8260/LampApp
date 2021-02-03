package com.elasalle.lamp.search;

import android.support.v7.widget.SearchView;
import android.widget.Filterable;

public class SearchOnQueryTextListener implements SearchView.OnQueryTextListener {

    private Filterable filterable;

    @Override
    public boolean onQueryTextSubmit(String query) {
        filterable.getFilter().filter(query);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        filterable.getFilter().filter(newText);
        return true;
    }

    public void setFilterable(Filterable filterableAdapter) {
        this.filterable = filterableAdapter;
    }
}
