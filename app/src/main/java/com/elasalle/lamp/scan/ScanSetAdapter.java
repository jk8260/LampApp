package com.elasalle.lamp.scan;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.elasalle.lamp.R;
import com.elasalle.lamp.model.scan.ScanSet;
import com.elasalle.lamp.util.ResourcesUtil;

import org.joda.time.DateTime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ScanSetAdapter extends RecyclerView.Adapter<ScanSetAdapter.ViewHolder> {

    private OnItemClickListener itemClickListener;
    private OnScanSetRemovedListener scanSetRemovedListener;
    private List<ScanSet> data = new ArrayList<>();
    private DateFormat dateFormat;

    public ScanSetAdapter() {
        dateFormat = new SimpleDateFormat("MM/dd/yy", Locale.US);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.scan_set_cell, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ScanSet header = data.get(position);
        holder.assetsNumber.setText(String.format(Locale.US, "%d", header.getNumberOfAssets()));
        holder.setName.setText(header.getName());
        Date modifiedDate = header.getModifiedDate();
        if (modifiedDate != null) {
            holder.lastModified.setText(String.format("%s %s", ResourcesUtil.getString(R.string.scan_modified_date_prefix), dateFormat.format(header.getModifiedDate())));
        } else {
            holder.lastModified.setText(" - ");
        }
        Date syncDate = header.getSyncDate();
        if (syncDate != null) {
            holder.lastSync.setText(dateFormat.format(header.getSyncDate()));
            DateTime modifiedDateTime = new DateTime(modifiedDate);
            DateTime syncDateTime = new DateTime(syncDate);
            if (modifiedDateTime.isAfter(syncDateTime)) {
                holder.lastSync.setTextColor(ResourcesUtil.getColor(R.color.primary, null));
                holder.itemView.setBackgroundColor(ResourcesUtil.getColor(R.color.sync_date_outdated, null));
            } else {
                holder.lastSync.setTextColor(ResourcesUtil.getColor(R.color.grey4, null));
                holder.itemView.setBackgroundColor(ResourcesUtil.getColor(R.color.white, null));
            }
        } else {
            holder.lastSync.setText(" - ");
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void remove(int position) {
        ScanSet scanSet = getItem(position);
        if (scanSetRemovedListener != null) {
            scanSetRemovedListener.onScanSetRemoved(scanSet);
        }
        data.remove(position);
        notifyItemRemoved(position);
    }

    public ScanSet getItem(int position) {
        return data.get(position);
    }

    public void setScanSetRemovedListener(OnScanSetRemovedListener scanSetRemovedListener) {
        this.scanSetRemovedListener = scanSetRemovedListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.scan_cell_assets_number) TextView assetsNumber;
        @BindView(R.id.scan_cell_set_name) TextView setName;
        @BindView(R.id.scan_cell_modified_date) TextView lastModified;
        @BindView(R.id.scan_cell_last_sync) TextView lastSync;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (itemClickListener != null) {
                final int position = getAdapterPosition();
                final ScanSet scanSet = getItem(position);
                itemClickListener.onItemClick(scanSet);
            }
        }
    }

    public void setData(List<ScanSet> scanSets) {
        data = scanSets;
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(ScanSet scanSet);
    }

    public interface OnScanSetRemovedListener {
        void onScanSetRemoved(ScanSet scanSet);
    }

    public void setOnItemClickListener(final OnItemClickListener onItemClickListener) {
        this.itemClickListener = onItemClickListener;
    }
}