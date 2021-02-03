package com.elasalle.lamp.search;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.elasalle.lamp.LampApp;
import com.elasalle.lamp.R;
import com.elasalle.lamp.analytics.AnalyticsActivity;
import com.elasalle.lamp.client.NetworkRequestCallback;
import com.elasalle.lamp.model.asset.AssetDetails;
import com.elasalle.lamp.model.search.Datum;
import com.elasalle.lamp.util.DisplayHelper;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AssetListActivity extends AnalyticsActivity {

    public static final String KEY_LIST = "dataList";

    private List<Datum> listData;

    @BindView(R.id.assetList)
    RecyclerView recyclerView;

    SearchView searchView;

    @Inject AssetDetailManager assetDetailManager;
    @Inject ProgressDialog progressDialog;

    @Override
    protected void setAnalyticsScreenName() {
        mTracker.setScreenName(getString(R.string.analytics_asset_list));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asset_list);
        listData = getIntent().getParcelableArrayListExtra(KEY_LIST);
        setTitleFromData();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        ButterKnife.bind(this);
        LampApp.searchComponent(this).inject(this);
        setList();
    }

    private void setTitleFromData() {
        final Datum datum = listData.get(0);
        if (datum != null && datum.type.toLowerCase().contains("ticket")) {
            setTitle(getString(R.string.asset_list_title_tickets));
        } else if(datum != null && datum.type.toLowerCase().contains("contract")) {
            setTitle(getString(R.string.asset_list_title_contracts));
        } else {
            setTitle(getString(R.string.asset_list_title));
        }
    }

    private void setList() {
        assetDetailManager.setOnReauthenticateCallback(new Runnable() {
            @Override
            public void run() {
                dismissProgressBar();
            }
        });
        AssetListAdapter assetListAdapter = new AssetListAdapter(listData);
        assetListAdapter.setOnItemClickListener(new AssetListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Datum datum) {
                showProgressBar();
                assetDetailManager.getAssetDetails(datum.target, new NetworkRequestCallback<AssetDetails>() {
                    @Override
                    public void onSuccess(AssetDetails results) {
                        dismissProgressBar();
                        showDetail(results);
                    }

                    @Override
                    public void onFailure(@NonNull String message) {
                        dismissProgressBar();
                        displayMessage(message);
                    }
                });
            }
        });
        RecyclerView.LayoutManager layoutManager;
        if (DisplayHelper.isTablet()) {
            final int spanCount = DisplayHelper.isPortraitMode() ? 2 : 3;
            layoutManager = new GridLayoutManager(LampApp.getInstance(), spanCount);
        } else {
            layoutManager = new LinearLayoutManager(getApplicationContext());
        }
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(assetListAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_search_menu, menu);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setMaxWidth(DisplayHelper.getWiderScreenWidth());
        searchView.setIconified(true);
        SearchOnQueryTextListener listener = new SearchOnQueryTextListener();
        listener.setFilterable((AssetListAdapter) recyclerView.getAdapter());
        searchView.setOnQueryTextListener(listener);
        return true;
    }

    private void displayMessage(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog alertDialog = new AlertDialog
                        .Builder(AssetListActivity.this, R.style.AppTheme_Dialog_Alert)
                        .setMessage(message)
                        .setPositiveButton(getString(R.string.alert_button_text_dismiss), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setCancelable(false)
                        .create();
                alertDialog.show();
            }
        });
    }

    private void showProgressBar() {
        progressDialog.show();
    }

    private void dismissProgressBar() {
        progressDialog.dismiss();
    }

    private void showDetail(AssetDetails assetDetails) {
        Intent intent = new Intent(this, AssetDetailActivity.class);
        intent.putExtra(AssetDetailActivity.KEY_URL, assetDetails.getUrl());
        intent.putExtra(AssetDetailActivity.KEY_TITLE, assetDetails.getTitle());
        intent.putExtra(AssetDetailActivity.KEY_ACTIONS, assetDetails.getActions().toArray());
        startActivity(intent);
    }

}
