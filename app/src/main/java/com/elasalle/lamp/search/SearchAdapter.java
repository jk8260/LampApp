package com.elasalle.lamp.search;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.elasalle.lamp.R;
import com.elasalle.lamp.model.search.SearchItem;
import com.elasalle.lamp.util.DisplayHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.RowHolder> {

    private int VIEW_TYPE_CONTENT = 1;

    private OnItemClickListener itemClickListener;
    private List<SearchItem> data = new ArrayList<>();

    public SearchAdapter() {
    }

    @Override
    public SearchAdapter.RowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        if (viewType == VIEW_TYPE_CONTENT) {
            v = LayoutInflater.from(parent.getContext().getApplicationContext()).inflate(R.layout.search_result_item, parent, false);
        } else {
            v = LayoutInflater.from(parent.getContext().getApplicationContext()).inflate(R.layout.search_result_header, parent, false);
        }
        return new RowHolder(v);
    }

    @Override
    public void onBindViewHolder(SearchAdapter.RowHolder holder, int position) {
        SearchItem searchItem = data.get(position);
        if (searchItem.isHeader()) {
            if (holder != null && holder.headerText != null) {
                holder.headerText.setText(searchItem.getFields().get("header"));
                holder.headerText.setMinWidth(DisplayHelper.getWiderScreenWidth());
            }
        } else {
            if (holder != null && holder.container != null) {
                holder.container.removeAllViews();
                for (String key : searchItem.getFields().keySet()) {
                    LinearLayout field = (LinearLayout) LayoutInflater.from(holder.itemView.getContext()).inflate(R.layout.search_result_field, null);
                    TextView name = (TextView) field.findViewById(R.id.field_name);
                    TextView value = (TextView) field.findViewById(R.id.field_value);
                    name.setText(key);
                    value.setText(searchItem.getFields().get(key));
                    holder.container.addView(field);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public int getItemViewType(int position) {
        final int VIEW_TYPE_HEADER = 0;
        if (data.get(position).isHeader()) {
            return VIEW_TYPE_HEADER;
        }
        return VIEW_TYPE_CONTENT;
    }

    public SearchItem getItem(int position) {
        return data.get(position);
    }

    public class RowHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Nullable @BindView(R.id.search_result_header_text) TextView headerText;
        @Nullable @BindView(R.id.search_result_item) LinearLayout container;

        public RowHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            if (container != null) {
                view.setOnClickListener(this);
            }
        }

        @Override
        public void onClick(View v) {
            if (itemClickListener != null) {
                itemClickListener.onItemClick(v, getAdapterPosition());
            }
        }
    }

    public void setData(List<SearchItem> searchItems) {
        data = searchItems;
    }

    public interface OnItemClickListener {
        void onItemClick(View view , int position);
    }

    public void setOnItemClickListener(final OnItemClickListener onItemClickListener) {
        this.itemClickListener = onItemClickListener;
    }

}