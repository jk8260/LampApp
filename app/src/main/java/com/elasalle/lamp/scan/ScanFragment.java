package com.elasalle.lamp.scan;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.elasalle.lamp.LampApp;
import com.elasalle.lamp.R;
import com.elasalle.lamp.analytics.AnalyticsFragment;
import com.elasalle.lamp.model.scan.ScanSet;
import com.elasalle.lamp.recyclerview.SimpleDividerItemDecoration;
import com.elasalle.lamp.main.DashboardEventListener;
import com.elasalle.lamp.scan.detail.ScanSetDetailActivity;
import com.elasalle.lamp.scan.newset.NewScanSetDialogFragment;
import com.elasalle.lamp.ui.FooterFragment;
import com.elasalle.lamp.ui.dashboard.menu.HelpActivity;
import com.elasalle.lamp.util.DisplayHelper;
import com.elasalle.lamp.util.GoogleAnalyticsHelper;

import java.lang.ref.WeakReference;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ScanFragment extends AnalyticsFragment {

    private static final String TAG = ScanFragment.class.getSimpleName();

    private WeakReference<DashboardEventListener> mListener;

    @BindView(R.id.scan_toolbar) Toolbar toolbar;
    @BindView(R.id.scan_sets_list) RecyclerView scanSets;
    @BindView(R.id.scan_add_set) FloatingActionButton addScanFab;
    @BindView(R.id.scan_background) View scanBackground;
    @BindView(R.id.scan_header) View scanHeader;

    @Inject ScanSetAdapter adapter;
    @Inject ScanSetManager scanSetManager;
    @Inject NewScanSetDialogFragment newScanSetDialogFragment;

    private Unbinder unbinder;

    @Override
    protected void setAnalyticsScreenName() {
        mTracker.setScreenName(getString(R.string.analytics_scan));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scan, container, false);
        unbinder = ButterKnife.bind(this, view);
        LampApp.scanSetComponent().inject(this);
        setupFooterFragment();
        setupToolbar();
        setupRecyclerView();
        setupAddScanSetFab();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void setupAddScanSetFab() {
        addScanFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                Fragment previousFragment = getFragmentManager().findFragmentByTag("dialog");
                if (previousFragment != null) {
                    ft.remove(previousFragment);
                }
                newScanSetDialogFragment.show(ft, "dialog");
            }
        });
    }

    private void setupFooterFragment() {
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.footer, new FooterFragment())
                .commit();
    }

    private void setupToolbar() {
        toolbar.setTitle(getString(R.string.dashboard_scan_title));
        toolbar.setNavigationIcon(R.drawable.action_home);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.get().onNavigationIconClick();
            }
        });
        toolbar.inflateMenu(R.menu.scan_fragment_menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_help: {
                        Log.d(TAG, "scan help");
                        showHelp();
                        return true;
                    }
                    default: {
                        return false;
                    }
                }
            }
        });
    }

    private void setupRecyclerView() {
        if (DisplayHelper.isTablet()) {
            GridLayoutManager gridLayoutManager;
            if (DisplayHelper.isPortraitMode()) {
                gridLayoutManager = new GridLayoutManager(getActivity(), 2);
            } else {
                gridLayoutManager = new GridLayoutManager(getActivity(), 3);
            }
            scanSets.setLayoutManager(gridLayoutManager);
        } else {
            scanSets.setLayoutManager(new LinearLayoutManager(getActivity()));
            scanSets.addItemDecoration(new SimpleDividerItemDecoration());
        }
        adapter.setOnItemClickListener(new ScanSetAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ScanSet scanSet) {
                showScanDetail(scanSet);
                GoogleAnalyticsHelper.sendAnalyticsEvent(
                        getString(R.string.analytics_scan),
                        getString(R.string.analytics_action_select_scan_set),
                        scanSet.getId()
                );
            }
        });
        adapter.setScanSetRemovedListener(new ScanSetAdapter.OnScanSetRemovedListener() {
            @Override
            public void onScanSetRemoved(ScanSet scanSet) {
                scanSetManager.delete(scanSet);
            }
        });
        scanSets.setAdapter(adapter);

        final ScanSetTouchHelper callback = new ScanSetTouchHelper(adapter, getActivity());
        callback.onItemRemoved(new Runnable() {
            @Override
            public void run() {
                updateBackground();
            }
        });
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(scanSets);
    }

    private void showScanDetail(ScanSet scanSet) {
        Intent intent = new Intent(getActivity(), ScanSetDetailActivity.class);
        intent.putExtra(ScanSetDetailActivity.KEY_SCAN_SET, scanSet);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                mListener.get().onNavigationIconClick();
                return true;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof DashboardEventListener) {
            mListener = new WeakReference<>((DashboardEventListener)context);
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement DashboardEventListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.setData(scanSetManager.getScanSets());
        updateBackground();
    }

    private void updateBackground() {
        if (scanSets.getAdapter().getItemCount() > 0 ) {
            scanBackground.setVisibility(View.GONE);
            setScanHeaderVisibility(View.VISIBLE);
        } else {
            scanBackground.setVisibility(View.VISIBLE);
            setScanHeaderVisibility(View.GONE);
        }
    }

    private void setScanHeaderVisibility(final int visibility) {
        if (DisplayHelper.isTablet()) {
            scanHeader.setVisibility(View.GONE);
        } else {
            scanHeader.setVisibility(visibility);
        }
    }

    private void showHelp() {
        Intent intent = new Intent(getContext(), HelpActivity.class);
        intent.putExtra(HelpActivity.HELP_TYPE, HelpActivity.SCAN_HELP_INTENT);
        startActivity(intent);
    }
}