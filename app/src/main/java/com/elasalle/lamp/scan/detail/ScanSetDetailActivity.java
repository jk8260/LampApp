package com.elasalle.lamp.scan.detail;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.elasalle.lamp.LampApp;
import com.elasalle.lamp.R;
import com.elasalle.lamp.analytics.AnalyticsActivity;
import com.elasalle.lamp.client.SyncCallback;
import com.elasalle.lamp.model.scan.Asset;
import com.elasalle.lamp.model.scan.ScanSet;
import com.elasalle.lamp.model.user.User;
import com.elasalle.lamp.scan.asset.AddAssetActivity;
import com.elasalle.lamp.scan.model.NewScanSet;
import com.elasalle.lamp.scan.newset.NewScanSetActivity;
import com.elasalle.lamp.ui.FooterFragment;
import com.elasalle.lamp.util.GoogleAnalyticsHelper;
import com.elasalle.lamp.util.MessageHelper;
import com.elasalle.lamp.util.ResourcesUtil;

import java.util.Date;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ScanSetDetailActivity extends AnalyticsActivity implements ScanSetDetailInterface {

    private static final int REQUEST_CODE_ASSET = 224;

    public static final String KEY_SCAN_SET = "Scan Set";

    @BindView(R.id.header) View header;
    @BindView(R.id.scan_set_detail_add_asset) FloatingActionButton fabAddAsset;

    @Inject User user;
    @Inject ScanSetDetailManager scanSetDetailManager;
    @Inject ScanSetSyncManager scanSetSyncManager;

    private ScanSet scanSet;
    private NewScanSet configuredScanSet;

    @Override
    protected void setAnalyticsScreenName() {
        mTracker.setScreenName(getString(R.string.analytics_scan_detail));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_set_detail);
        ButterKnife.bind(this);
        LampApp.scanSetComponent().inject(this);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            scanSet = bundle.getParcelable(KEY_SCAN_SET);
            if (scanSet != null) {
                setTitle(scanSet.getName());
            }
        }
        if (user.isGuest()) {
            header.setVisibility(View.INVISIBLE);
        } else {
            updateSyncStatus((TextView) header);
        }
        setupActionBar();
        setupFooterFragment();
        showDetailFragment();
        setupAddAssetFab();
    }

    private void setupAddAssetFab() {
        fabAddAsset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddAssetScreen(null);
                GoogleAnalyticsHelper.sendAnalyticsEvent(
                        getString(R.string.analytics_scan),
                        getString(R.string.analytics_action_click_new_asset)
                );
            }
        });
    }

    private void showAddAssetScreen(@Nullable final Asset asset) {
        scanSetDetailManager.save(configuredScanSet);
        Intent intent = new Intent(ScanSetDetailActivity.this, AddAssetActivity.class);
        intent.putExtra(AddAssetActivity.KEY_SCAN_SET_ID, scanSet.getId());
        if (asset != null) {
            intent.putExtra(AddAssetActivity.KEY_EDIT_ASSET_ID, asset.getId());
        }
        startActivityForResult(intent, REQUEST_CODE_ASSET);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_ASSET: {
                refreshDetailFragment();
                break;
            }
            default: {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    private void refreshDetailFragment() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(ScanSetDetailFragment.class.getSimpleName());
        if (fragment != null) {
            ((ScanSetDetailFragment) fragment).refreshList();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.scan_set_detail_menu, menu);
        if (user.isGuest()) {
            MenuItem item = menu.findItem(R.id.action_scan_sync);
            if (item != null) {
                item.setVisible(false);
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();
        switch (id) {
            case android.R.id.home: {
                onBackPressed();
                break;
            }
            case R.id.action_edit_scan_sets: {
                showScanSetEditScreen();
                break;
            }
            case R.id.action_duplicate_scan_sets: {
                duplicateScanSet();
                break;
            }
            case R.id.action_share_csv: {
                onShareAsCsv();
                break;
            }
            case R.id.action_scan_sync: {
                if (!user.isGuest()) {
                    final TextView textView = (TextView) header;
                    textView.setText(getString(R.string.syncing));
                    scanSetSyncManager.sync(user.getCustomerId(), new SyncCallback(){
                        @Override
                        public void onSuccess() {
                            scanSet = scanSetSyncManager.getLatestScanSet(scanSet.getId());
                            textView.post(new Runnable() {
                                @Override
                                public void run() {
                                    updateSyncStatus(textView);
                                }
                            });
                        }

                        @Override
                        public void onFailure(@NonNull String message) {
                            textView.post(new Runnable() {
                                @Override
                                public void run() {
                                    updateSyncStatus(textView);
                                }
                            });
                            MessageHelper.displayMessage(ScanSetDetailActivity.this, message, null);
                        }

                        @Override
                        public void onReauthenticate() {
                            textView.post(new Runnable() {
                                @Override
                                public void run() {
                                    updateSyncStatus(textView);
                                }
                            });
                        }
                    });
                }
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateSyncStatus(TextView textView) {
        final Date date = scanSet.getSyncDate();
        if (date == null) {
            textView.setText(getString(R.string.scan_sync_status_not_synced));
        } else {
            textView.setText(scanSet.getSyncDate().toString());
        }
    }

    private void onShareAsCsv() {
        scanSetDetailManager.shareCSV(this, scanSet, configuredScanSet);
        GoogleAnalyticsHelper.sendAnalyticsEvent(
                getString(R.string.analytics_scan),
                getString(R.string.analytics_action_share_scan_set),
                scanSet.getId()
        );
    }

    private void showScanSetEditScreen() {
        scanSetDetailManager.save(configuredScanSet);
        Intent intent = new Intent(this, NewScanSetActivity.class);
        intent.putExtra(NewScanSetActivity.KEY_EDIT_SCAN_SET_ID, scanSet.getId());
        intent.putExtra(NewScanSetActivity.KEY_EDIT, true);
        startActivity(intent);
        GoogleAnalyticsHelper.sendAnalyticsEvent(
                getString(R.string.analytics_scan),
                getString(R.string.analytics_action_edit_scan_set),
                scanSet.getId()
        );
    }

    private void duplicateScanSet() {
        scanSetDetailManager.save(configuredScanSet);
        scanSetDetailManager.duplicate(scanSet, new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), getString(R.string.scan_set_duplicated_message_success), Toast.LENGTH_LONG).show();
                GoogleAnalyticsHelper.sendAnalyticsEvent(
                        getString(R.string.analytics_scan),
                        getString(R.string.analytics_action_duplicate_scan_set),
                        scanSet.getId()
                );
            }
        }, new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), getString(R.string.scan_set_duplicated_message_failure), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(ResourcesUtil.getDrawable(R.drawable.action_back, null));
        }

    }

    private void setupFooterFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.footer, new FooterFragment())
                .commit();
    }

    private void showDetailFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, new ScanSetDetailFragment(), ScanSetDetailFragment.class.getSimpleName())
                .commit();
    }

    @Override
    public ScanSet getScanSet() {
        return scanSet;
    }

    @Override
    public NewScanSet getConfiguredScanSet() {
        configuredScanSet = scanSetDetailManager.getConfiguredScanSetForScanSet(scanSet);
        return configuredScanSet;
    }

    @Override
    public void onAssetRemoved(NewScanSet newScanSet) {
        scanSetDetailManager.updateForRemovedAsset(newScanSet, scanSet);
    }

    @Override
    public void onAssetClick(Asset asset) {
        showAddAssetScreen(asset);
    }
}
