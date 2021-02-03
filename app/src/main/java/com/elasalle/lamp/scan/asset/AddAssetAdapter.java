package com.elasalle.lamp.scan.asset;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.elasalle.lamp.R;
import com.elasalle.lamp.model.user.Field;
import com.elasalle.lamp.scan.model.ScanSetField;
import com.elasalle.lamp.util.ResourcesUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddAssetAdapter extends RecyclerView.Adapter<AddAssetAdapter.ViewHolder> {

    private final List<ScanSetField> scanSetFields;
    private OnInputFieldUserInteractionListener onInputFieldUserInteractionListener;

    public AddAssetAdapter(List<ScanSetField> scanSetFields) {
        this.scanSetFields = scanSetFields;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext().getApplicationContext()).inflate(R.layout.scan_add_asset, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (holder != null) {
            final ScanSetField scanSetField = scanSetFields.get(position);
            holder.fieldLabel.setText(scanSetField.getScanFieldLabel());
            Field field = scanSetField.getField();
            if (Field.LOOKUP_LIST_TYPE.equals(field.getType())) {
                setupForLookupList(holder, scanSetField);
            } else {
                holder.listIcon.setVisibility(View.INVISIBLE);
                holder.fieldValue.setHint(ResourcesUtil.getString(R.string.scan_add_asset_enter_hint));
            }
            if (position < scanSetFields.size() - 1) {
                holder.fieldValue.setImeOptions(EditorInfo.IME_ACTION_NEXT);
            } else {
                holder.fieldValue.setImeOptions(EditorInfo.IME_ACTION_DONE);
            }
            setValues(holder, field);
        }
    }

    private void setValues(ViewHolder holder, Field field) {
        final String text;
        if (Field.LOOKUP_LIST_TYPE.equals(field.getType())) {
            text = (String) field.getAdditionalProperties().get(Field.KEY_LOOKUP_LIST_VALUE_DISPLAY);
        } else {
            text = (String) field.getAdditionalProperties().get(Field.KEY_FIELD_VALUE);
        }
        if (!TextUtils.isEmpty(text)) {
            holder.fieldValue.setText(text);
        }
    }

    private void setupForLookupList(final ViewHolder holder, final ScanSetField scanSetField) {
        holder.listIcon.setVisibility(View.VISIBLE);
        holder.fieldValue.setHint(ResourcesUtil.getString(R.string.scan_add_asset_select_hint));
        holder.fieldValue.setFocusable(false);
        holder.fieldValue.setClickable(true);
        holder.fieldValue.setLongClickable(false);
        holder.listIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onInputFieldUserInteractionListener != null) {
                    onInputFieldUserInteractionListener.onFocusOrClick(scanSetField, holder.fieldValue);
                }
            }
        });
        holder.fieldValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onInputFieldUserInteractionListener != null) {
                    onInputFieldUserInteractionListener.onFocusOrClick(scanSetField, holder.fieldValue);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return scanSetFields.size();
    }

    public ScanSetField getItemAtPosition(final int position) {
        return scanSetFields.get(position);
    }

    public void setValueForField(@NonNull final String value, @NonNull final String fieldId, @Nullable final String lookupListItemId) {
        for (final ScanSetField scanSetField : scanSetFields) {
            final Field field = scanSetField.getField();
            if (fieldId.equals(field.getFieldId())) {
                if (Field.LOOKUP_LIST_TYPE.equals(field.getType())) {
                    field.setAdditionalProperty(Field.KEY_LOOKUP_LIST_VALUE_ID, lookupListItemId);
                    field.setAdditionalProperty(Field.KEY_LOOKUP_LIST_VALUE_DISPLAY, value);
                } else {
                    field.setAdditionalProperty(Field.KEY_FIELD_VALUE, value);
                }
            }
        }
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.field_list)
        ImageView listIcon;
        @BindView(R.id.field_label)
        TextView fieldLabel;
        @BindView(R.id.field_value)
        EditText fieldValue;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            if (onInputFieldUserInteractionListener != null) {
                this.fieldValue.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        final int position = getAdapterPosition();
                        final ScanSetField scanSetField = getItemAtPosition(position);
                        if (hasFocus) {
                            onInputFieldUserInteractionListener.onFocusOrClick(scanSetField, fieldValue);
                        } else {
                            onInputFieldUserInteractionListener.onLostFocus(scanSetField);
                        }
                    }
                });
            }
            this.fieldValue.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    final int position = getAdapterPosition();
                    final ScanSetField scanSetField = getItemAtPosition(position);
                    scanSetField.getField().setAdditionalProperty(Field.KEY_FIELD_VALUE, s.toString());
                }
            });
        }

        public void clear(final int position) {
            final ScanSetField scanSetField = getItemAtPosition(position);
            if (!scanSetField.isCarryForward()) {
                if (fieldValue != null) {
                    fieldValue.setText("");
                    scanSetField.getField().setAdditionalProperty(Field.KEY_FIELD_VALUE, "");
                    scanSetField.getField().setAdditionalProperty(Field.KEY_LOOKUP_LIST_VALUE_DISPLAY, "");
                    scanSetField.getField().setAdditionalProperty(Field.KEY_LOOKUP_LIST_VALUE_ID, "");
                }
            }
        }

        public boolean setInitialFocus(final int position) {
            final ScanSetField scanSetField = getItemAtPosition(position);
            final Field field = scanSetField.getField();
            if (Field.LOOKUP_LIST_TYPE.equals(field.getType())) {
                return false;
            } else {
                this.fieldValue.requestFocus();
                return true;
            }
        }
    }

    interface OnInputFieldUserInteractionListener {
        void onFocusOrClick(ScanSetField scanSetField, View view);
        void onLostFocus(ScanSetField scanSetField);
    }

   void setOnInputFieldUserInteractionListener(OnInputFieldUserInteractionListener listener) {
        this.onInputFieldUserInteractionListener = listener;
    }
}
