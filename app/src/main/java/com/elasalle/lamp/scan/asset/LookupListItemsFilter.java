package com.elasalle.lamp.scan.asset;

import android.text.TextUtils;
import android.widget.Filter;

import com.elasalle.lamp.model.user.LookupListItem;

import java.util.ArrayList;
import java.util.List;

public class LookupListItemsFilter extends Filter {

    private final List<LookupListItem> lookupListItems;
    private final LookupListAdapter adapter;

    public LookupListItemsFilter(List<LookupListItem> lookupListItems, LookupListAdapter adapter) {
        this.lookupListItems = lookupListItems;
        this.adapter = adapter;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults filterResults = new FilterResults();
        if (TextUtils.isEmpty(constraint)) {
            filterResults.values = this.lookupListItems;
        } else {
            List<LookupListItem> filteredItems = new ArrayList<>();
            for (LookupListItem item : lookupListItems) {
                if (item.getValue().toLowerCase().contains(constraint.toString().toLowerCase())) {
                    filteredItems.add(item);
                }
            }
            this.lookupListItems.clear();
            this.lookupListItems.addAll(filteredItems);
            filterResults.values = this.lookupListItems;
        }
        return filterResults;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        adapter.notifyDataSetChanged();
    }
}
