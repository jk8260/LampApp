package com.elasalle.lamp.scan.detail;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.elasalle.lamp.LampApp;
import com.elasalle.lamp.R;
import com.elasalle.lamp.model.scan.Asset;
import com.elasalle.lamp.recyclerview.SimpleDividerItemDecoration;
import com.elasalle.lamp.scan.model.NewScanSet;
import com.elasalle.lamp.util.DisplayHelper;
import com.elasalle.lamp.util.GoogleAnalyticsHelper;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ScanSetDetailFragment extends Fragment {

    @BindView(R.id.list) RecyclerView list;
    @BindView(R.id.background) View emptyListBackground;

    private WeakReference<ScanSetDetailInterface> mScanSetDetailInterface;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scan_set_detail, container, false);
        ButterKnife.bind(this, view);
        setupList();
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ScanSetDetailInterface) {
            mScanSetDetailInterface = new WeakReference<>((ScanSetDetailInterface) context);
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ScanSetDetailInterface");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshList();
    }

    void refreshList() {
        final NewScanSet scanSet = mScanSetDetailInterface.get().getConfiguredScanSet();
        if (scanSet == null || scanSet.getAssets() == null || scanSet.getAssets().size() == 0) {
            emptyListBackground.setVisibility(View.VISIBLE);
        } else {
            emptyListBackground.setVisibility(View.GONE);
        }
        ScanSetDetailAdapter adapter = (ScanSetDetailAdapter) list.getAdapter();
        if (adapter != null) {
            adapter.updateData(scanSet);
        }
    }

    private void setupList() {
        final NewScanSet scanSet = mScanSetDetailInterface.get().getConfiguredScanSet();
        setListLayoutManager();
        final ScanSetDetailAdapter adapter = setupAdapter(scanSet);
        setupTouchHelper(adapter);
    }

    private void setupTouchHelper(ScanSetDetailAdapter adapter) {
        final ScanSetDetailTouchHelper callback = new ScanSetDetailTouchHelper(adapter, getActivity());
        callback.onItemRemoved(new Runnable() {
            @Override
            public void run() {
                refreshList();
            }
        });
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(list);
    }

    @NonNull
    private ScanSetDetailAdapter setupAdapter(NewScanSet scanSet) {
        ScanSetDetailAdapter adapter = new ScanSetDetailAdapter(scanSet);
        adapter.setAssetRemovedListener(new ScanSetDetailAdapter.OnAssetRemovedListener() {
            @Override
            public void onAssetRemoved(NewScanSet scanSet) {
                mScanSetDetailInterface.get().onAssetRemoved(scanSet);
            }
        });
        adapter.setAssetClickListener(new ScanSetDetailAdapter.OnAssetClickListener() {
            @Override
            public void onItemClick(Asset asset) {
                mScanSetDetailInterface.get().onAssetClick(asset);
                GoogleAnalyticsHelper.sendAnalyticsEvent(
                        getString(R.string.analytics_scan),
                        getString(R.string.analytics_action_select_asset),
                        asset.getId()
                );
            }
        });
        list.setAdapter(adapter);
        return adapter;
    }

    private void setListLayoutManager() {
        final Context context = LampApp.getInstance();
        final RecyclerView.LayoutManager layoutManager;
        if (DisplayHelper.isTablet()) {
            final int spanCount = DisplayHelper.isPortraitMode() ? 2 : 3;
            layoutManager = new GridLayoutManager(context, spanCount);
        } else {
            layoutManager = new LinearLayoutManager(context);
            SimpleDividerItemDecoration decoration = new SimpleDividerItemDecoration();
            final float inset = getResources().getDimension(R.dimen.default_ui_padding);
            decoration.setInset((int) inset);
            list.addItemDecoration(decoration);
        }
        list.setLayoutManager(layoutManager);
    }
}
