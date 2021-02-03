package com.elasalle.lamp.ui.customer;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.elasalle.lamp.LampApp;
import com.elasalle.lamp.R;
import com.elasalle.lamp.analytics.AnalyticsActivity;
import com.elasalle.lamp.client.ApiRequestCallback;
import com.elasalle.lamp.search.SearchOnQueryTextListener;
import com.elasalle.lamp.util.DisplayHelper;
import com.elasalle.lamp.util.MessageHelper;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChangeCustomerActivity extends AnalyticsActivity {

    @BindView(R.id.recycler_view) RecyclerView recyclerView;
    @BindView(R.id.change_customer_toolbar)Toolbar toolbar;
    @BindView(R.id.select_customer_fab)FloatingActionButton fab;
    @BindView(R.id.progressBar) View progressBar;

    @Inject ChangeCustomerAdapter changeCustomerAdapter;
    @Inject ChangeCustomerRecyclerViewOnItemTouchListener onItemTouchListener;
    @Inject SearchOnQueryTextListener searchOnQueryTextListener;
    @Inject ChangeCustomerManager changeCustomerManager;

    @Override
    protected void setAnalyticsScreenName() {
        mTracker.setScreenName(getString(R.string.analytics_change_customer));
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_customer);
        ButterKnife.bind(this);
        LampApp.userComponent().inject(this);
        setupToolbar();
        setupRecyclerView();
        setupFab();
    }

    private void setupFab() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressBar();
                final String selectedCustomerId = changeCustomerAdapter.getSelectedCustomerId();
                changeCustomerManager.changeCustomer(selectedCustomerId, new ApiRequestCallback() {
                    @Override
                    public void onSuccess() {
                        dismissProgressBar();
                        finish();
                    }

                    @Override
                    public void onFailure(@NonNull String message) {
                        dismissProgressBar();
                        setInputViewsEnabled(true);
                        displayMessage(message);
                    }
                }, new Runnable() {

                    @Override
                    public void run() {
                        dismissProgressBar();
                    }
                });
            }
        });
    }

    private void setupToolbar() {
        toolbar.setTitle(getString(R.string.change_customer_title));
        toolbar.setNavigationIcon(R.drawable.action_back);
        toolbar.setNavigationOnClickListener(getToolbarNavigationListener());
        setupSearch();
    }

    @NonNull
    private View.OnClickListener getToolbarNavigationListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        };
    }

    private void setupSearch() {
        searchOnQueryTextListener.setFilterable(changeCustomerAdapter);
        toolbar.inflateMenu(R.menu.toolbar_search_menu);
        SearchView searchView = (SearchView) toolbar.getMenu().findItem(R.id.action_search).getActionView();
        searchView.setMaxWidth(DisplayHelper.getWiderScreenWidth());
        searchView.setOnQueryTextListener(searchOnQueryTextListener);
    }

    private void setupRecyclerView() {
        this.onItemTouchListener.setRecyclerView(recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(changeCustomerAdapter);
        recyclerView.addOnItemTouchListener(onItemTouchListener);
    }

    private void showProgressBar() {
        setInputViewsEnabled(false);
        this.progressBar.setVisibility(View.VISIBLE);
    }

    private void setInputViewsEnabled(boolean isEnabled) {
        fab.setEnabled(isEnabled);
        toolbar.getMenu().findItem(R.id.action_search).setVisible(isEnabled);
        toolbar.setNavigationOnClickListener(isEnabled ? getToolbarNavigationListener() : null);
        if (isEnabled) {
            recyclerView.addOnItemTouchListener(onItemTouchListener);
        } else {
            recyclerView.removeOnItemTouchListener(onItemTouchListener);
        }
    }

    private void dismissProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        //noinspection StatementWithEmptyBody
        if (progressBar.getVisibility() == View.VISIBLE) {
            // no-op
        } else {
            super.onBackPressed();
        }
    }

    private void displayMessage(String message) {
        MessageHelper.displayMessage(ChangeCustomerActivity.this, message, null);
    }

}
