package com.elasalle.lamp.scan.asset;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.elasalle.lamp.R;
import com.elasalle.lamp.model.user.LookupListItem;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LookupListAdapter extends RecyclerView.Adapter<LookupListAdapter.ViewHolder> implements Filterable {

    private final List<LookupListItem> lookupListItems;
    private final List<LookupListItem> filteredItems;
    private OnItemClickListener onItemClickListener;
    private String selectedItemId = "";

    public LookupListAdapter(List<LookupListItem> lookupListItems) {
        this.lookupListItems = lookupListItems;
        this.filteredItems = new ArrayList<>(lookupListItems);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.scan_add_asset_lookup_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final LookupListItem item = filteredItems.get(position);
        if (holder != null) {
            if (holder.mTextView != null) {
                holder.mTextView.setText(item.getValue());
            }
            if (holder.mImageView != null) {
                holder.setImageVisibility(item.getId());
            }
        }
    }

    @Override
    public int getItemCount() {
        return filteredItems.size();
    }

    public LookupListItem getItemAtPosition(final int position) {
        return filteredItems.get(position);
    }

    @Override
    public Filter getFilter() {
        this.filteredItems.clear();
        this.filteredItems.addAll(lookupListItems);
        return new LookupListItemsFilter(this.filteredItems, this);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.selected_item_checkmark)
        ImageView mImageView;
        @BindView(R.id.list_item)
        TextView mTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        final int position = getAdapterPosition();
                        LookupListItem item = getItemAtPosition(position);
                        selectedItemId = item.getId();
                        setImageVisibility(item.getId());
                        notifyDataSetChanged();
                        onItemClickListener.onItemClick(item);
                    }
                }
            });
        }

        public void setImageVisibility(final String id) {
            if (selectedItemId.equals(id)) {
                mImageView.setVisibility(View.VISIBLE);
            } else {
                mImageView.setVisibility(View.INVISIBLE);
            }
        }
    }

    interface OnItemClickListener {
        void onItemClick(LookupListItem item);
    }

    void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

}

