package com.elasalle.lamp.search;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.elasalle.lamp.R;
import com.elasalle.lamp.model.search.Attribute;
import com.elasalle.lamp.model.search.Datum;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AssetListAdapter extends RecyclerView.Adapter<AssetListAdapter.ViewHolder> implements Filterable {

    private final List<Datum> data;
    private final List<Datum> filteredData;

    private AssetListAdapter.OnItemClickListener onItemClickListener;

    public AssetListAdapter(List<Datum> data) {
        this.data = data;
        this.filteredData = new ArrayList<>(data);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext().getApplicationContext()).inflate(R.layout.card_asset_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Datum datum = filteredData.get(position);
        holder.container.removeAllViews();
        for (Attribute attribute : datum.attributes) {
            if (!attribute.display) {
                continue;
            }
            LinearLayout field = (LinearLayout) LayoutInflater.from(holder.itemView.getContext().getApplicationContext()).inflate(R.layout.card_asset_list_item_child, null);
            TextView label = (TextView) field.findViewById(R.id.asset_list_label);
            TextView value = (TextView) field.findViewById(R.id.asset_list_value);
            label.setText(attribute.label);
            value.setText(attribute.value);
            holder.container.addView(field);
        }
    }

    @Override
    public int getItemCount() {
        return this.filteredData.size();
    }

    @Override
    public Filter getFilter() {
        this.filteredData.clear();
        this.filteredData.addAll(data);
        return new AssetListFilter(this.filteredData, this);
    }

    public Datum getDatumAtPosition(int position) {
        return this.filteredData.get(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final View parent;
        @BindView(R.id.container) LinearLayout container;

        public ViewHolder(View itemView) {
            super(itemView);
            parent = itemView;
            ButterKnife.bind(this, itemView);
            parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        int position = getAdapterPosition();
                        Datum datum = getDatumAtPosition(position);
                        onItemClickListener.onItemClick(datum);
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Datum datum);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }
}
