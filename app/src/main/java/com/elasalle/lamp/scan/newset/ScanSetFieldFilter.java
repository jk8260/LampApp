package com.elasalle.lamp.scan.newset;

import android.text.TextUtils;
import android.widget.Filter;

import com.elasalle.lamp.scan.model.ScanSetField;

import java.util.LinkedList;
import java.util.List;

public class ScanSetFieldFilter extends Filter {

    private final List<ScanSetField> fields;
    private final SelectFieldsAdapter adapter;

    public ScanSetFieldFilter(List<ScanSetField> filteredFields, SelectFieldsAdapter selectFieldsAdapter) {
        this.fields = filteredFields;
        this.adapter = selectFieldsAdapter;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults filterResults = new FilterResults();
        if (TextUtils.isEmpty(constraint)) {
            filterResults.values = this.fields;
        } else {
            List<ScanSetField> filteredFields = new LinkedList<>();
            for (ScanSetField scanSetField : fields) {
                if (scanSetField.getScanFieldLabel().toLowerCase().contains(constraint.toString().toLowerCase())) {
                    filteredFields.add(scanSetField);
                }
            }
            this.fields.clear();
            this.fields.addAll(filteredFields);
            filterResults.values = this.fields;
        }
        return filterResults;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        adapter.notifyDataSetChanged();
    }
}
