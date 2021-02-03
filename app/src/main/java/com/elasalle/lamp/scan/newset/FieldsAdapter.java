package com.elasalle.lamp.scan.newset;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.elasalle.lamp.R;
import com.elasalle.lamp.scan.model.ScanSetField;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FieldsAdapter extends RecyclerView.Adapter<FieldsAdapter.ViewHolder> {

    private final List<ScanSetField> scanSetFields;
    private FieldsAdapter.OnItemClickListener onItemClickListener;

    public FieldsAdapter(List<ScanSetField> scanSetFieldList) {
        scanSetFields = scanSetFieldList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.scan_new_scan_set_fields, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ScanSetField scanSetField = scanSetFields.get(position);
        holder.label.setText(scanSetField.getScanFieldLabel());
        holder.value.setText(scanSetField.getScanFieldValue());
    }

    @Override
    public int getItemCount() {
        return scanSetFields.size();
    }

    public ScanSetField getScanSetFieldAtPosition(int position) {
        return this.scanSetFields.get(position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.fieldLabel) TextView label;
        @BindView(R.id.fieldValue) TextView value;
        @BindView(R.id.fieldSettings) ImageView settings;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        final int position = getAdapterPosition();
                        final ScanSetField scanSetField = getScanSetFieldAtPosition(position);
                        onItemClickListener.onItemClick(scanSetField);
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(ScanSetField scanSetField);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }
}
