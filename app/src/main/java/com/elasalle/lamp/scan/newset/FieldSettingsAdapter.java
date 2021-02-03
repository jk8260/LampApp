package com.elasalle.lamp.scan.newset;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.elasalle.lamp.R;
import com.elasalle.lamp.scan.model.ScanSetField;
import com.elasalle.lamp.util.ResourcesUtil;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FieldSettingsAdapter extends RecyclerView.Adapter<FieldSettingsAdapter.ViewHolder> {

    private static final String KEY_CARRY_FORWARD = "carryForward";
    private static final String KEY_SHOW_FIELD = "showField";
    private final ScanSetField scanSetField;
    private Map<String, Boolean> settings;

    public FieldSettingsAdapter(ScanSetField scanSetField) {
        settings = new HashMap<>();
        settings.put(KEY_CARRY_FORWARD, scanSetField.isCarryForward());
        settings.put(KEY_SHOW_FIELD, scanSetField.isShowField());
        this.scanSetField = scanSetField;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.scan_new_scan_set_field_settings, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (position == 0) {
            holder.label.setText(ResourcesUtil.getString(R.string.carry_forward_label));
            holder.description.setText(ResourcesUtil.getString(R.string.carry_forward_description));
            holder.fieldSettingsSwitch.setChecked(scanSetField.isCarryForward());
        } else {
            holder.label.setText(ResourcesUtil.getString(R.string.show_field_label));
            holder.description.setText(ResourcesUtil.getString(R.string.show_field_description));
            holder.fieldSettingsSwitch.setChecked(scanSetField.isShowField());
        }
    }

    @Override
    public int getItemCount() {
        return settings.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.field_setting_label) TextView label;
        @BindView(R.id.field_setting_description) TextView description;
        @BindView(R.id.field_setting_switch) Switch fieldSettingsSwitch;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            fieldSettingsSwitch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (label.getText().toString().contains(ResourcesUtil.getString(R.string.carry_forward_label))) {
                        scanSetField.setCarryForward(fieldSettingsSwitch.isChecked());
                    } else {
                        scanSetField.setShowField(fieldSettingsSwitch.isChecked());
                    }
                }
            });
        }

    }

}
