package com.elasalle.lamp.search;

import android.text.TextUtils;
import android.widget.Filter;

import com.elasalle.lamp.model.search.Attribute;
import com.elasalle.lamp.model.search.Datum;

import java.util.ArrayList;
import java.util.List;

class AssetListFilter extends Filter {

    private final List<Datum> datumList;
    private final AssetListAdapter adapter;

    public AssetListFilter(List<Datum> datumList, AssetListAdapter adapter) {
        this.datumList = datumList;
        this.adapter = adapter;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults filterResults = new FilterResults();
        if (TextUtils.isEmpty(constraint)) {
            filterResults.values = this.datumList;
        } else {
            List<Datum> filteredData = new ArrayList<>();
            for (Datum datum : datumList) {
                for (Attribute attribute : datum.attributes) {
                    if (attribute.value.toLowerCase().contains(constraint.toString().toLowerCase())) {
                        if (!filteredData.contains(datum)) {
                            filteredData.add(datum);
                            break;
                        }
                    }
                }
            }
            this.datumList.clear();
            this.datumList.addAll(filteredData);
            filterResults.values = this.datumList;
        }
        return filterResults;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        adapter.notifyDataSetChanged();
    }
}
