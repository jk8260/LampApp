package com.elasalle.lamp.scan.asset;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.elasalle.lamp.LampApp;
import com.elasalle.lamp.R;
import com.elasalle.lamp.data.repository.FieldLookupListRepository;
import com.elasalle.lamp.model.user.FieldLookupList;
import com.elasalle.lamp.model.user.LookupListItem;
import com.elasalle.lamp.recyclerview.SimpleDividerItemDecoration;
import com.elasalle.lamp.scan.model.ScanSetField;
import com.elasalle.lamp.search.SearchOnQueryTextListener;
import com.elasalle.lamp.util.DisplayHelper;
import com.elasalle.lamp.util.ResourcesUtil;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddAssetLookupListDialogFragment extends DialogFragment {

    public static final String KEY_SCAN_SET_FIELD = "Scan Set Field";

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.list) RecyclerView list;
    @BindView(R.id.fab_check) FloatingActionButton fab;

    private ScanSetField scanSetField;
    private OnListItemSelectedListener onListItemSelectedListener;
    private LookupListAdapter adapter;
    private LookupListItem lookupListItem;

    @Inject FieldLookupListRepository repository;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        Bundle arguments = getArguments();
        if (arguments != null) {
            scanSetField = arguments.getParcelable(KEY_SCAN_SET_FIELD);
        }
        LampApp.scanSetComponent().inject(this);
        setupAdapter();
    }

    private void setupList() {
        list.setHasFixedSize(true);
        list.setLayoutManager(new LinearLayoutManager(getContext()));
        final SimpleDividerItemDecoration decoration = new SimpleDividerItemDecoration();
        final int inset = (int) ResourcesUtil.getResources().getDimension(R.dimen.default_title_margin);
        decoration.setInset(inset);
        decoration.setTabletOverride(true);
        list.setAdapter(adapter);
        list.addItemDecoration(decoration);
    }

    private void setupAdapter() {
        FieldLookupList fieldLookupList = repository.findByFieldId(scanSetField.getField().getFieldId());
        adapter = new LookupListAdapter(fieldLookupList.getLookupListItems());
        adapter.setOnItemClickListener(new LookupListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(LookupListItem item) {
                lookupListItem = item;
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        getDialog().setCanceledOnTouchOutside(false);
        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Dialog alertDialog = new AlertDialog.Builder(getActivity())
                .setView(R.layout.fragment_add_asset_lookuplist)
                .create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                ButterKnife.bind(AddAssetLookupListDialogFragment.this, alertDialog);
                setupToolbar();
                setupList();
                setupFab();
            }
        });
        alertDialog.setCancelable(false);
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        resizeDialog(alertDialog);
        return alertDialog;
    }

    private void resizeDialog(Dialog alertDialog) {
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(alertDialog.getWindow().getAttributes());
        final int widthMargin = DisplayHelper.isTablet() ? (int) (getResources().getDimension(R.dimen.default_title_margin) * 2) : 0;
        final int width = DisplayHelper.getScreenWidth() - widthMargin;
        final int heightMargin = DisplayHelper.isTablet() ? DisplayHelper.getPixelsDp(200f) : 0;
        final int height = DisplayHelper.getScreenHeight() - heightMargin;
        lp.width  = width;
        lp.height = height;
        alertDialog.show();
        alertDialog.getWindow().setAttributes(lp);
    }

    private void setupFab() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissWithSelectedItem();
            }
        });
    }

    private void setupToolbar() {
        toolbar.setTitle(scanSetField.getScanFieldLabel());
        toolbar.setBackgroundColor(ResourcesUtil.getColor(R.color.accent, null));
        toolbar.setNavigationIcon(R.drawable.action_close);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissWithSelectedItem();
            }
        });
        setupSearch();
    }

    private void dismissWithSelectedItem() {
        if (onListItemSelectedListener != null) {
            onListItemSelectedListener.onListItemSelected(lookupListItem);
            dismiss();
        }
    }

    private void setupSearch() {
        SearchOnQueryTextListener searchOnQueryTextListener = new SearchOnQueryTextListener();
        searchOnQueryTextListener.setFilterable(adapter);
        toolbar.inflateMenu(R.menu.toolbar_search_menu);
        SearchView searchView = (SearchView) toolbar.getMenu().findItem(R.id.action_search).getActionView();
        searchView.setMaxWidth(DisplayHelper.getWiderScreenWidth());
        searchView.setOnQueryTextListener(searchOnQueryTextListener);
    }

    interface OnListItemSelectedListener {
        void onListItemSelected(LookupListItem lookupListItem);
    }

    void setOnListItemSelectedListener(OnListItemSelectedListener onListItemSelectedListener) {
        this.onListItemSelectedListener = onListItemSelectedListener;
    }

}
