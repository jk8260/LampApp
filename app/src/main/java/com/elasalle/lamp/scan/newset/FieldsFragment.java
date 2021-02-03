package com.elasalle.lamp.scan.newset;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.elasalle.lamp.R;
import com.elasalle.lamp.recyclerview.FooterDecoration;
import com.elasalle.lamp.recyclerview.SimpleDividerItemDecoration;
import com.elasalle.lamp.scan.model.ScanSetField;

public class FieldsFragment extends NewScanSetBaseFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        setupList();
        setHeader();
        setListFooter();
        return view;
    }

    private void setupList() {
        FieldsAdapter adapter = new FieldsAdapter(newScanSetInterfaceWeakReference.get().getFields());
        adapter.setOnItemClickListener(new FieldsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ScanSetField scanSetField) {
                newScanSetInterfaceWeakReference.get().onFieldSettingsSelected(scanSetField);
            }
        });
        SimpleDividerItemDecoration decoration = new SimpleDividerItemDecoration();
        float inset = getResources().getDimension(R.dimen.default_title_margin);
        decoration.setTabletOverride(true);
        decoration.setInset((int) inset);
        list.addItemDecoration(decoration);
        list.setAdapter(adapter);
    }

    private void setListFooter() {
        list.addItemDecoration(new FooterDecoration());
    }

    private void setHeader() {
        headerOne.setText(getString(R.string.scan_field));
        headerTwo.setText(getString(R.string.scan_field_value));
    }
}
