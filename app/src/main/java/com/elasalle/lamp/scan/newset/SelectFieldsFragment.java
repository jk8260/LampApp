package com.elasalle.lamp.scan.newset;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.elasalle.lamp.R;
import com.elasalle.lamp.recyclerview.SimpleDividerItemDecoration;
import com.elasalle.lamp.scan.model.ScanSetField;

public class SelectFieldsFragment extends NewScanSetBaseFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        setupList();
        setHeader();
        return view;
    }

    private void setupList() {
        SelectFieldsAdapter adapter = new SelectFieldsAdapter(newScanSetInterfaceWeakReference.get().getAllFields());
        adapter.setOnItemClickListener(new SelectFieldsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ScanSetField scanSetField, boolean isSelected) {
                if (isSelected) {
                    newScanSetInterfaceWeakReference.get().addField(scanSetField);
                } else {
                    newScanSetInterfaceWeakReference.get().removeField(scanSetField);
                }
            }
        });
        list.setAdapter(adapter);
        SimpleDividerItemDecoration decoration = new SimpleDividerItemDecoration();
        final float inset = getResources().getDimension(R.dimen.default_title_margin);
        decoration.setInset((int) inset);
        decoration.setTabletOverride(true);
        list.addItemDecoration(decoration);
    }

    private void setHeader() {
        headerOne.setText(getString(R.string.scan_field_required));
        headerTwo.setVisibility(View.INVISIBLE);
    }

    public SelectFieldsAdapter getAdapter() {
        return (SelectFieldsAdapter) list.getAdapter();
    }

}
