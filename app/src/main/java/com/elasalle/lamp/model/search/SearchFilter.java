package com.elasalle.lamp.model.search;

import com.elasalle.lamp.LampApp;
import com.elasalle.lamp.model.user.Customer;
import com.elasalle.lamp.model.user.SearchType;

public class SearchFilter {

    public String filter;

    public SearchFilter(String filter) {
        this.filter = getFilterCode(filter);
    }

    private String getFilterCode(final String filter) {
        String filterCode = "";
        final Customer customer = LampApp.getSessionManager().getCustomerDetails();
        for (final SearchType searchType : customer.getSearchTypes()) {
            if (searchType.getContentTypeLabel().equals(filter)) {
                filterCode = searchType.getContentTypeCode();
                break;
            }
        }
        return filterCode;
    }
}