package com.elasalle.lamp.scan.asset;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.elasalle.lamp.LampApp;
import com.elasalle.lamp.R;
import com.elasalle.lamp.analytics.AnalyticsActivity;
import com.elasalle.lamp.camera.CameraActivity;
import com.elasalle.lamp.model.scan.Asset;
import com.elasalle.lamp.scan.model.NewScanSet;
import com.elasalle.lamp.util.CameraPermissionsHelper;
import com.elasalle.lamp.util.GoogleAnalyticsHelper;
import com.elasalle.lamp.util.ResourcesUtil;

import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

public class AddAssetActivity extends AnalyticsActivity implements AddAssetInterface {

    public static final String KEY_SCAN_SET_ID = "Scan Set Id";
    public static final String KEY_EDIT_ASSET_ID = "Edit Asset Id";

    private static final int REQUEST_CODE_CAMERA = 100;
    private static final int MENU_ITEM_ID_DELETE_ASSET = 50;

    private NewScanSet scanSet;
    private boolean isEnableCameraMenu = false;
    private String cameraScanLookupTerm;
    private boolean isAssetEdit = false;
    private String assetEditId;

    @Inject AddAssetManager addAssetManager;

    @Override
    protected void setAnalyticsScreenName() {
        mTracker.setScreenName(getString(R.string.analytics_scan_new_asset));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_set_add_asset);
        LampApp.scanSetComponent().inject(this);
        if (savedInstanceState == null) {
            final String scanSetId = getIntent().getStringExtra(KEY_SCAN_SET_ID);
            scanSet = addAssetManager.getConfiguredScanSet(scanSetId);
            assetEditId = getIntent().getStringExtra(KEY_EDIT_ASSET_ID);
        } else {
            final String scanSetId = savedInstanceState.getString(KEY_SCAN_SET_ID);
            scanSet = addAssetManager.getConfiguredScanSet(scanSetId);
            assetEditId = savedInstanceState.getString(KEY_EDIT_ASSET_ID);
        }
        isAssetEdit = assetEditId != null;
        setTitle(getString(R.string.scan_set_add_asset_title));
        setupActionbar();
        showAddAssetFragment();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        String scanSetId = getIntent().getStringExtra(KEY_SCAN_SET_ID);
        outState.putString(KEY_SCAN_SET_ID, scanSetId);
        outState.putString(KEY_EDIT_ASSET_ID, assetEditId);
        super.onSaveInstanceState(outState);
    }

    private void showAddAssetFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, new AddAssetFragment(), AddAssetFragment.class.getSimpleName())
                .commit();
    }

    private void setupActionbar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.action_close);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.scan_set_add_asset_menu, menu);
        menu.findItem(R.id.action_add_asset_camera).setEnabled(false);
        if (isAssetEdit) {
            MenuItem menuItem = menu.add(Menu.NONE, MENU_ITEM_ID_DELETE_ASSET, Menu.NONE, getString(R.string.delete_asset));
            menuItem.setIcon(ResourcesUtil.getDrawable(R.drawable.action_delete, null));
            menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        }
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_add_asset_camera).setEnabled(isEnableCameraMenu);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();
        switch (id) {
            case android.R.id.home: {
                onBackPressed();
                return true;
            }
            case R.id.action_add_asset_camera: {
                displayCameraScreen();
                return true;
            }
            case MENU_ITEM_ID_DELETE_ASSET: {
                deleteAsset();
            }
            default: {
                return super.onOptionsItemSelected(item);
            }

        }
    }

    private void deleteAsset() {
        final String assetId = getAssetEditId();
        final List<Asset> assetList = scanSet.getAssets();
        for (Iterator<Asset> iterator = assetList.listIterator(); iterator.hasNext();) {
            Asset asset = iterator.next();
            if (assetId.equals(asset.getId())) {
                iterator.remove();
                addAssetManager.updateScanSet(scanSet);
                Toast.makeText(getApplicationContext(), getString(R.string.scan_set_asset_deleted) ,Toast.LENGTH_LONG).show();
                finish();
                break;
            }
        }
    }

    private void displayCameraScreen() {
        if (CameraPermissionsHelper.isFeatureCameraAny(this)) {
            if(CameraPermissionsHelper.isCameraPermissionGranted(this, REQUEST_CODE_CAMERA)) {
                CameraPermissionsHelper.startCameraActivity(this, cameraScanLookupTerm);
                sendCameraAnalytics();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CameraActivity.INTENT_CODE_CAMERA) {
            if (resultCode == Activity.RESULT_OK) {
                String scannedValue = data.getStringExtra(CameraActivity.INTENT_PARAM_BARCODE);
                if (!TextUtils.isEmpty(scannedValue) && !scannedValue.equals(getString(R.string.scanning))) {
                    Fragment fragment = getSupportFragmentManager().findFragmentByTag(AddAssetFragment.class.getSimpleName());
                    if (fragment != null) {
                        ((AddAssetFragment) fragment).setInputFieldForFieldName(scannedValue, cameraScanLookupTerm);
                    }
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_CAMERA: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    CameraPermissionsHelper.startCameraActivity(this, cameraScanLookupTerm);
                    sendCameraAnalytics();
                }
            }
        }
    }

    @Override
    public NewScanSet getScanSet() {
        return scanSet;
    }

    @Override
    public void onScanSetUpdatedWithNewAsset() {
        addAssetManager.updateScanSet(scanSet);
        Toast.makeText(getApplicationContext(), getString(R.string.scan_set_asset_added), Toast.LENGTH_LONG).show();
        GoogleAnalyticsHelper.sendAnalyticsEvent(
                getString(R.string.analytics_scan_new_asset),
                getString(R.string.analytics_action_create_asset)
        );
    }

    @Override
    public void onScanSetAssetUpdated() {
        addAssetManager.updateScanSet(scanSet);
        Toast.makeText(getApplicationContext(), getString(R.string.scan_set_asset_updated), Toast.LENGTH_LONG).show();
        GoogleAnalyticsHelper.sendAnalyticsEvent(
                getString(R.string.analytics_scan_new_asset),
                getString(R.string.analytics_action_update_asset)
        );
        finish();
    }

    @Override
    public void onInputFieldSelected(String fieldName) {
        cameraScanLookupTerm = fieldName;
        isEnableCameraMenu = true;
        invalidateOptionsMenu();
    }

    @Override
    public void onInputFieldDeselected() {
        isEnableCameraMenu = false;
        invalidateOptionsMenu();
    }

    @Override
    public boolean isAssetEdit() {
        return this.isAssetEdit;
    }

    @Override
    public String getAssetEditId() {
        return this.assetEditId;
    }

    private void sendCameraAnalytics() {
        GoogleAnalyticsHelper.sendAnalyticsEvent(
                getString(R.string.analytics_scan_new_asset),
                getString(R.string.analytics_action_open_camera)
        );
    }
}
