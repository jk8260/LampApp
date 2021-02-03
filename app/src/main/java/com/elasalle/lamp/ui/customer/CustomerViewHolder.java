package com.elasalle.lamp.ui.customer;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.elasalle.lamp.R;

public class CustomerViewHolder extends RecyclerView.ViewHolder {
    public final TextView mTextView;
    public final ImageView mImageView;
    public CustomerViewHolder(View itemView) {
        super(itemView);
        this.mTextView = (TextView) itemView.findViewById(R.id.customer);
        this.mImageView = (ImageView) itemView.findViewById(R.id.selected_customer_checkmark);
    }
}
