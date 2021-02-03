package com.elasalle.lamp.scan.newset;

import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.elasalle.lamp.R;
import com.elasalle.lamp.scan.model.ScanSetField;
import com.elasalle.lamp.util.ResourcesUtil;

import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SelectFieldsAdapter extends RecyclerView.Adapter<SelectFieldsAdapter.ViewHolder> implements Filterable {

    private final List<ScanSetField> scanSetFields;
    private final List<ScanSetField> filteredFields;
    private SelectFieldsAdapter.OnItemClickListener onItemClickListener;

    public SelectFieldsAdapter(List<ScanSetField> scanSetFieldList) {
        scanSetFields = scanSetFieldList;
        filteredFields = new LinkedList<>(scanSetFieldList);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.scan_new_scan_select_fields, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ScanSetField scanSetField = filteredFields.get(position);

        if (scanSetField.isRequired()) {
            Spannable spannable = new SpannableString(scanSetField.getScanFieldLabel() + "*");
            spannable.setSpan(new ForegroundColorSpan(ResourcesUtil.getColor(R.color.primary, null)),
                    scanSetField.getScanFieldLabel().length(),
                    scanSetField.getScanFieldLabel().length() + 1,
                    Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            holder.label.setText(spannable);
        } else {
            holder.label.setText(scanSetField.getScanFieldLabel());
        }
        if (scanSetField.isRequired() || holder.isSelected || scanSetField.isSelected()) {
            holder.setImageForSelected();
        } else {
            holder.setImageForNotSelected();
        }
    }

    @Override
    public int getItemCount() {
        return filteredFields.size();
    }

    public ScanSetField getScanSetFieldAtPosition(int position) {
        return this.filteredFields.get(position);
    }

    @Override
    public Filter getFilter() {
        this.filteredFields.clear();
        this.filteredFields.addAll(scanSetFields);
        return new ScanSetFieldFilter(this.filteredFields, this);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.fieldLabel) TextView label;
        @BindView(R.id.fieldSelection) ImageView selection;

        boolean isSelected = false;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int position = getAdapterPosition();
                    final ScanSetField scanSetField = getScanSetFieldAtPosition(position);
                    isSelected = scanSetField.isSelected();
                    if (scanSetField.isRequired()) {
                        isSelected = true;
                        scanSetField.setSelected(true);
                    } else  if (onItemClickListener != null) {
                        isSelected = !isSelected;
                        if (isSelected) {
                            setImageForSelected();
                        } else {
                            setImageForNotSelected();
                        }
                        scanSetField.setSelected(isSelected);
                        onItemClickListener.onItemClick(scanSetField, isSelected);
                    }
                }
            });
        }

        void setImageForSelected() {
            selection.setImageDrawable(ResourcesUtil.getDrawable(R.drawable.list_check, null));
            selection.setColorFilter(ResourcesUtil.getColor(R.color.primary, null));
        }

        void setImageForNotSelected() {
            selection.setImageDrawable(ResourcesUtil.getDrawable(R.drawable.list_check_empty, null));
            selection.setColorFilter(ResourcesUtil.getColor(R.color.grey5, null));
        }
    }

    public interface OnItemClickListener {
        void onItemClick(ScanSetField scanSetField, boolean isSelected);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }
}
