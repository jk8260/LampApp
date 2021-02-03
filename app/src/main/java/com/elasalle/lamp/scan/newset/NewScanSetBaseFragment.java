package com.elasalle.lamp.scan.newset;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.elasalle.lamp.LampApp;
import com.elasalle.lamp.R;
import com.elasalle.lamp.scan.model.ScanSetField;

import java.lang.ref.WeakReference;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class NewScanSetBaseFragment extends Fragment {

    @BindView(R.id.headerOne) TextView headerOne;
    @BindView(R.id.headerTwo) TextView headerTwo;
    @BindView(R.id.list) RecyclerView list;

    WeakReference<NewScanSetInterface> newScanSetInterfaceWeakReference;

    private Unbinder unbinder;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_scan_set_list, container, false);
        unbinder = ButterKnife.bind(this, view);
        setRecyclerViewLayoutManager();
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof NewScanSetInterface) {
            newScanSetInterfaceWeakReference = new WeakReference<>((NewScanSetInterface) context);
        }  else {
            throw new RuntimeException(context.toString()
                    + " must implement NewScanSetInterface");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void setRecyclerViewLayoutManager() {
        final Context context = LampApp.getInstance().getApplicationContext();
        final RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        list.setLayoutManager(layoutManager);
    }

    interface NewScanSetInterface {
        List<ScanSetField> getFields();
        void addField(ScanSetField scanSetField);
        void removeField(ScanSetField scanSetField);
        List<ScanSetField> getAllFields();
        void onFieldSettingsSelected(ScanSetField scanSetField);
    }

}
