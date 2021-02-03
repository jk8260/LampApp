package com.elasalle.lamp.scan.newset;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.elasalle.lamp.R;
import com.elasalle.lamp.recyclerview.SimpleDividerItemDecoration;

public class ReorderFieldsFragment extends NewScanSetBaseFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        setHeader();
        setupList();
        return view;
    }

    private void setupList() {
        ReorderFieldsAdapter adapter = new ReorderFieldsAdapter(newScanSetInterfaceWeakReference.get().getFields());
        list.setAdapter(adapter);
        SimpleDividerItemDecoration decoration = new SimpleDividerItemDecoration();
        final float inset = getResources().getDimension(R.dimen.default_title_margin);
        decoration.setInset((int) inset);
        decoration.setTabletOverride(true);
        list.addItemDecoration(decoration);

        ItemTouchHelper.Callback callback = new ReorderFieldsTouchHelper(adapter);
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(list);
    }

    private void setHeader() {
        headerOne.setText(getString(R.string.scan_reorder_header));
        headerTwo.setVisibility(View.INVISIBLE);
    }
}
