package com.elasalle.lamp.scan.asset;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.elasalle.lamp.LampApp;
import com.elasalle.lamp.R;
import com.elasalle.lamp.model.scan.Asset;
import com.elasalle.lamp.model.user.Field;
import com.elasalle.lamp.model.user.LookupListItem;
import com.elasalle.lamp.recyclerview.SimpleDividerItemDecoration;
import com.elasalle.lamp.scan.model.NewScanSet;
import com.elasalle.lamp.scan.model.ScanSetField;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddAssetFragment extends Fragment {

    private static final String TAG = AddAssetFragment.class.getSimpleName();

    private WeakReference<AddAssetInterface> mAddAssetInterface;
    private boolean isSetupForAssetEdit = false;

    @BindView(R.id.header) TextView header;
    @BindView(R.id.list) RecyclerView list;
    @BindView(R.id.fab_check) FloatingActionButton fabAddAssetAndContinue;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scan_add_asset, container, false);
        ButterKnife.bind(this, view);
        NewScanSet scanSet = mAddAssetInterface.get().getScanSet();
        header.setText(scanSet.getName());
        setupList();
        setupFab();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        setupForAssetEdit();
    }

    private void setupForAssetEdit() {
        if (!isSetupForAssetEdit) {
            isSetupForAssetEdit = true;
            final boolean isAssetEdit = mAddAssetInterface.get().isAssetEdit();
            if (isAssetEdit) {
                final Asset assetToEdit = getAssetToEdit();
                populateFields(assetToEdit);
            }
        }
    }

    @Nullable
    private Asset getAssetToEdit() {
        final NewScanSet scanSet = mAddAssetInterface.get().getScanSet();
        final List<Asset> assets = scanSet.getAssets();
        final String assetEditId = mAddAssetInterface.get().getAssetEditId();
        Asset assetToEdit = null;
        for (final Asset asset : assets) {
            if (assetEditId.equals(asset.getId())) {
                assetToEdit = asset;
                break;
            }
        }
        return assetToEdit;
    }

    private void populateFields(Asset asset) {
        final AddAssetAdapter adapter = (AddAssetAdapter) list.getAdapter();
        for (final Field field : asset.getFields()) {
            final String key;
            if (Field.LOOKUP_LIST_TYPE.equals(field.getType())) {
               key = field.getFieldId() + Field.LOOKUP_LIST_FIELD_ID_DISPLAY_SUFFIX;
            } else {
                key = field.getFieldId();
            }
            final String value = asset.getFieldIdValueMap().get(key);
            if (adapter != null) {
                adapter.setValueForField(value, field.getFieldId(), (String) field.getAdditionalProperties().get(Field.KEY_LOOKUP_LIST_VALUE_ID));
            }
        }
    }

    private void setupFab() {
        fabAddAssetAndContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processAssetAndContinue();
            }
        });
    }

    private void setupList() {
        setListLayoutManager();
        NewScanSet scanSet = mAddAssetInterface.get().getScanSet();
        final AddAssetAdapter adapter = new AddAssetAdapter(scanSet.getScanSetFields());
        adapter.setOnInputFieldUserInteractionListener(getOnInputFieldFocusListener());
        list.setAdapter(adapter);
    }

    @NonNull
    private AddAssetAdapter.OnInputFieldUserInteractionListener getOnInputFieldFocusListener() {
        return new AddAssetAdapter.OnInputFieldUserInteractionListener() {
            @Override
            public void onFocusOrClick(ScanSetField scanSetField, View view) {
                final Field field = scanSetField.getField();
                final String fieldType = field.getType();
                if (fieldType != null && !Field.LOOKUP_LIST_TYPE.equalsIgnoreCase(fieldType)) {
                    mAddAssetInterface.get().onInputFieldSelected(scanSetField.getScanFieldLabel());
                } else if (fieldType != null && Field.LOOKUP_LIST_TYPE.equalsIgnoreCase(fieldType)) {
                    displayLookupList(scanSetField, view);
                }
            }

            @Override
            public void onLostFocus(ScanSetField scanSetField) {
                final Field field = scanSetField.getField();
                final String fieldType = field.getType();
                if (fieldType != null && !Field.LOOKUP_LIST_TYPE.equalsIgnoreCase(fieldType)) {
                    mAddAssetInterface.get().onInputFieldDeselected();
                }
            }
        };
    }

    private void displayLookupList(final ScanSetField scanSetField, final View view) {
        AddAssetLookupListDialogFragment dialogFragment = new AddAssetLookupListDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(AddAssetLookupListDialogFragment.KEY_SCAN_SET_FIELD, scanSetField);
        dialogFragment.setArguments(bundle);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment previousFragment = getFragmentManager().findFragmentByTag("dialog");
        if (previousFragment != null) {
            ft.remove(previousFragment);
        }
        dialogFragment.setOnListItemSelectedListener(new AddAssetLookupListDialogFragment.OnListItemSelectedListener() {
            @Override
            public void onListItemSelected(LookupListItem lookupListItem) {
                if (view instanceof EditText) {
                    final EditText editText = (EditText) view;
                    //noinspection ConstantConditions
                    if (editText != null && lookupListItem != null) {
                        editText.setText(lookupListItem.getValue());
                        scanSetField.getField().setAdditionalProperty(Field.KEY_LOOKUP_LIST_VALUE_ID, lookupListItem.getId());
                        scanSetField.getField().setAdditionalProperty(Field.KEY_LOOKUP_LIST_VALUE_DISPLAY, lookupListItem.getValue());
                    }
                }
            }
        });
        dialogFragment.show(ft, "dialog");
    }

    private void setListLayoutManager() {
        final Context context = LampApp.getInstance();
        final RecyclerView.LayoutManager layoutManager;
        layoutManager = new LinearLayoutManager(context);
        list.setLayoutManager(layoutManager);

        final SimpleDividerItemDecoration decoration = new SimpleDividerItemDecoration();
        final float inset = getResources().getDimension(R.dimen.default_title_margin);
        decoration.setInset((int) inset);
        decoration.setTabletOverride(true);
        list.addItemDecoration(decoration);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof AddAssetInterface) {
            mAddAssetInterface = new WeakReference<>((AddAssetInterface) context);
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement AddAssetInterface");
        }
    }

    void processAssetAndContinue() {
        if (isReadyForAssetCreation()) {
            final boolean isAssetEdit = mAddAssetInterface.get().isAssetEdit();
            if (isAssetEdit) {
                updateAssetAndFinish();
            } else {
                final Asset asset = new Asset();
                asset.setId(UUID.randomUUID().toString());
                NewScanSet scanSet = mAddAssetInterface.get().getScanSet();
                for (final ScanSetField scanSetField : scanSet.getScanSetFields()) {
                    final Field field = scanSetField.getField();
                    asset.addField(field);
                    String value;
                    if (Field.LOOKUP_LIST_TYPE.equals(scanSetField.getField().getType())) {
                        value = (String) scanSetField.getField().getAdditionalProperties().get(Field.KEY_LOOKUP_LIST_VALUE_ID);
                        String displayValue = (String) scanSetField.getField().getAdditionalProperties().get(Field.KEY_LOOKUP_LIST_VALUE_DISPLAY);
                        asset.addFieldValue(field.getFieldId() + Field.LOOKUP_LIST_FIELD_ID_DISPLAY_SUFFIX, displayValue);
                    } else {
                        value = (String) field.getAdditionalProperties().get(Field.KEY_FIELD_VALUE);
                    }
                    asset.addFieldValue(field.getFieldId(), value);
                }
                scanSet.addAsset(asset);
                mAddAssetInterface.get().onScanSetUpdatedWithNewAsset();
                clearViewHolders();
                setFocusToFirstField();
            }
        } else {
            Toast.makeText(getContext(), getString(R.string.scan_set_asset_field_missing), Toast.LENGTH_LONG).show();
        }
    }

    private void updateAssetAndFinish() {
        final Asset asset = getAssetToEdit();
        if (asset != null) {
            NewScanSet scanSet = mAddAssetInterface.get().getScanSet();
            for (final ScanSetField scanSetField : scanSet.getScanSetFields()) {
                final Field field = scanSetField.getField();
                String value;
                if (Field.LOOKUP_LIST_TYPE.equals(scanSetField.getField().getType())) {
                    value = (String) scanSetField.getField().getAdditionalProperties().get(Field.KEY_LOOKUP_LIST_VALUE_ID);
                    String displayValue = (String) scanSetField.getField().getAdditionalProperties().get(Field.KEY_LOOKUP_LIST_VALUE_DISPLAY);
                    asset.addFieldValue(field.getFieldId() + Field.LOOKUP_LIST_FIELD_ID_DISPLAY_SUFFIX, displayValue);
                } else {
                    value = (String) field.getAdditionalProperties().get(Field.KEY_FIELD_VALUE);
                }
                asset.addFieldValue(field.getFieldId(), value);
            }
            mAddAssetInterface.get().onScanSetAssetUpdated();
            clearViewHolders();
        } else {
            Log.e(TAG, "Expected asset to update but it was null!");
        }
    }

    private void clearViewHolders() {
        for (int position = 0; position < list.getAdapter().getItemCount(); position++) {
            AddAssetAdapter.ViewHolder holder = (AddAssetAdapter.ViewHolder) list.findViewHolderForAdapterPosition(position);
            if (holder != null) {
                holder.clear(position);
            }
        }
    }

    private void setFocusToFirstField() {
        for (int position = 0; position < list.getAdapter().getItemCount(); position++) {
            AddAssetAdapter.ViewHolder holder = (AddAssetAdapter.ViewHolder) list.findViewHolderForAdapterPosition(position);
            if (holder != null) {
                final boolean isSuccess = holder.setInitialFocus(position);
                if (isSuccess) {
                    break;
                }
            }
        }
    }

    private boolean isReadyForAssetCreation() {
        boolean isReady = true;
        NewScanSet scanSet = mAddAssetInterface.get().getScanSet();
        for (ScanSetField scanSetField : scanSet.getScanSetFields()) {
            if (TextUtils.isEmpty(scanSetField.getScanFieldValue()) ||
                    TextUtils.isEmpty((String) scanSetField.getField().getAdditionalProperties().get(Field.KEY_FIELD_VALUE))) {
                isReady = false;
                break;
            }
        }
        return isReady;
    }

    public void setInputFieldForFieldName(final String value, final String fieldName) {
        final int itemCount = list.getAdapter().getItemCount();
        for (int position = 0; position < itemCount; position++) {
            AddAssetAdapter.ViewHolder viewHolder = (AddAssetAdapter.ViewHolder) list.findViewHolderForAdapterPosition(position);
            if (viewHolder != null) {
                if (fieldName.equalsIgnoreCase(viewHolder.fieldLabel.getText().toString())) {
                    viewHolder.fieldValue.setText(value);
                    break;
                }
            }
        }
    }
}
