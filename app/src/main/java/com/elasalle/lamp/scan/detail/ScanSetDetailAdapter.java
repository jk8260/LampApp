package com.elasalle.lamp.scan.detail;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.elasalle.lamp.R;
import com.elasalle.lamp.model.scan.Asset;
import com.elasalle.lamp.model.user.Field;
import com.elasalle.lamp.scan.model.NewScanSet;
import com.elasalle.lamp.scan.model.ScanSetField;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ScanSetDetailAdapter extends RecyclerView.Adapter<ScanSetDetailAdapter.ViewHolder> {

    private NewScanSet scanSet;
    private OnAssetRemovedListener assetRemovedListener;
    private OnAssetClickListener assetClickListener;

    public ScanSetDetailAdapter(NewScanSet scanSet) {
        this.scanSet = scanSet;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext().getApplicationContext()).inflate(R.layout.scan_set_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (holder != null && holder.container != null) {
            holder.container.removeAllViews();
            final Asset asset = scanSet.getAssets().get(position);
            for (final Field field : asset.getFields()) {
                final String fieldId = field.getFieldId();
                if (TextUtils.isEmpty(fieldId)) {
                    continue;
                }
                final ScanSetField scanSetField = getScanSetField(fieldId);
                if (scanSetField != null && scanSetField.isShowField()) {
                    LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(holder.itemView.getContext()).inflate(R.layout.scan_set_asset_field, null);
                    TextView name = (TextView) linearLayout.findViewById(R.id.field_name);
                    TextView value = (TextView) linearLayout.findViewById(R.id.field_value);
                    name.setText(field.getName());
                    if (Field.LOOKUP_LIST_TYPE.equals(field.getType())) {
                        value.setText(asset.getFieldIdValueMap().get(fieldId + Field.LOOKUP_LIST_FIELD_ID_DISPLAY_SUFFIX));
                    } else {
                        value.setText(asset.getFieldIdValueMap().get(fieldId));
                    }
                    holder.container.addView(linearLayout);
                }
            }
        }
    }

    private ScanSetField getScanSetField(String fieldId) {
        ScanSetField field = null;
        for (ScanSetField scanSetField : scanSet.getScanSetFields()) {
            if (fieldId.equals(scanSetField.getField().getFieldId())) {
                field = scanSetField;
                break;
            }
        }
        return field;
    }

    @Override
    public int getItemCount() {
        return scanSet.getAssets().size();
    }

    public void remove(int position) {
        scanSet.getAssets().remove(position);
        notifyItemRemoved(position);
        if (assetRemovedListener != null) {
            assetRemovedListener.onAssetRemoved(scanSet);
        }
    }

    public void setAssetRemovedListener(OnAssetRemovedListener assetRemovedListener) {
        this.assetRemovedListener = assetRemovedListener;
    }

    public void updateData(NewScanSet scanSet) {
        this.scanSet = scanSet;
        notifyDataSetChanged();
    }

    public void setAssetClickListener(OnAssetClickListener assetClickListener) {
        this.assetClickListener = assetClickListener;
    }

    public interface OnAssetRemovedListener {
        void onAssetRemoved(NewScanSet scanSet);
    }

    public interface OnAssetClickListener {
        void onItemClick(Asset asset);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.container) LinearLayout container;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            if (assetClickListener != null) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final int position = getAdapterPosition();
                        final Asset asset = scanSet.getAssets().get(position);
                        assetClickListener.onItemClick(asset);
                    }
                });
            }
        }
    }
}
