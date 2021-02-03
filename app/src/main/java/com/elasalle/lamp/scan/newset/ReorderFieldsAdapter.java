package com.elasalle.lamp.scan.newset;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.elasalle.lamp.R;
import com.elasalle.lamp.scan.model.ScanSetField;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReorderFieldsAdapter extends RecyclerView.Adapter<ReorderFieldsAdapter.ViewHolder> {

    List<ScanSetField> scanSetFields;

    public ReorderFieldsAdapter(List<ScanSetField> scanSetFields) {
        this.scanSetFields = scanSetFields;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.scan_new_scan_reorder_fields, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ScanSetField scanSetField = scanSetFields.get(position);
        holder.fieldLabel.setText(scanSetField.getScanFieldLabel());
    }

    @Override
    public int getItemCount() {
        return scanSetFields.size();
    }

    public void swap(int firstPosition, int secondPosition){
        Collections.swap(scanSetFields, firstPosition, secondPosition);
        notifyItemMoved(firstPosition, secondPosition);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.fieldLabel) TextView fieldLabel;

        public ViewHolder(View view){
            super(view);
            ButterKnife.bind(this, view);
        }

    }
}
