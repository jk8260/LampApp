package com.elasalle.lamp.ui.customer;

import android.text.TextUtils;
import android.widget.Filter;

import com.elasalle.lamp.model.user.Customer;

import java.util.ArrayList;
import java.util.List;

class CustomersFilter extends Filter {

    private final List<Customer> customerList;
    private final ChangeCustomerAdapter adapter;

    public CustomersFilter(List<Customer> customerList, ChangeCustomerAdapter adapter) {
        this.customerList = customerList;
        this.adapter = adapter;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults filterResults = new FilterResults();
        if (TextUtils.isEmpty(constraint)) {
            filterResults.values = this.customerList;
        } else {
            List<Customer> filteredCustomers = new ArrayList<>();
            for (Customer customer : customerList) {
                if (customer.getName().toLowerCase().contains(constraint.toString().toLowerCase())) {
                    filteredCustomers.add(customer);
                }
            }
            this.customerList.clear();
            this.customerList.addAll(filteredCustomers);
            filterResults.values = this.customerList;
        }
        return filterResults;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        adapter.notifyDataSetChanged();
    }
}
