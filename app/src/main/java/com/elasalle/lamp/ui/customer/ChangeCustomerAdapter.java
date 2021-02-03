package com.elasalle.lamp.ui.customer;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.elasalle.lamp.R;
import com.elasalle.lamp.model.user.Customer;

import java.util.ArrayList;
import java.util.List;

public class ChangeCustomerAdapter extends RecyclerView.Adapter<CustomerViewHolder> implements Filterable {

    private final List<Customer> customers;
    private final List<Customer> filteredCustomers;
    private String selectedCustomerId;

    public ChangeCustomerAdapter(List<Customer> customers, String selectedCustomerId) {
        this.customers = customers;
        this.filteredCustomers = new ArrayList<>(customers);
        this.selectedCustomerId = selectedCustomerId;
    }

    @Override
    public CustomerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_change_customer, parent, false);
        CustomerViewHolder customerViewHolder = new CustomerViewHolder(itemView);
        return customerViewHolder;
    }

    @Override
    public void onBindViewHolder(CustomerViewHolder holder, int position) {
        final Customer customer = filteredCustomers.get(position);
        holder.mTextView.setText(customer.getName());
        if (selectedCustomerId.equals(customer.getCustomerId())) {
            holder.mImageView.setVisibility(View.VISIBLE);
        } else {
            holder.mImageView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return filteredCustomers.size();
    }

    public void setSelectedCustomerId(String selectedCustomerId) {
        this.selectedCustomerId = selectedCustomerId;
    }

    public String getSelectedCustomerId() {
        return this.selectedCustomerId;
    }

    public Customer getCustomerAtPosition(final int position) {
        return filteredCustomers.get(position);
    }

    @Override
    public Filter getFilter() {
        this.filteredCustomers.clear();
        this.filteredCustomers.addAll(customers);
        return new CustomersFilter(this.filteredCustomers, this);
    }

}
