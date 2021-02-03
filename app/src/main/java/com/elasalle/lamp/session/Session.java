package com.elasalle.lamp.session;

import android.support.annotation.Nullable;

import com.elasalle.lamp.model.user.Customer;
import com.elasalle.lamp.model.user.User;

import java.util.HashMap;
import java.util.Map;

public final class Session {

    public static final String SESSION_USER = "user";
    public static final String SESSION_ABOUT_TIMESTAMP = "aboutTimestamp";
    public static final String SESSION_SEARCH_TIMESTAMP = "searchTimestamp";
    public static final String SESSION_LOOKUP_TIMESTAMP = "lookupTimestamp";
    public static final String SESSION_SCAN_TIMESTAMP = "scanTimestamp";
    public static final String SESSION_NOTIFICATIONS_TIMESTAMP = "notificationsTimestamp";
    public static final String SESSION_SYSTEM_MESSAGE = "systemMessage";
    public static final String SESSION_CUSTOMER_DETAILS = "customerDetails";
    public static final String SESSION_TOKEN = "token";

    private final Map<String, Object> attributes;

    public Session() {
        this.attributes = new HashMap<>();
    }

    Object getAttribute(String key) {
        return attributes.get(key);
    }

    void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }

    void clear() {
        attributes.clear();
    }

    void clearUser() {
        clearAttribute(SESSION_USER);
        clearAttribute(SESSION_CUSTOMER_DETAILS);
        clearAttribute(SESSION_TOKEN);
    }

    void clearAttribute(String attribute) {
        attributes.remove(attribute);
    }

    @Nullable User getUser() {
        return (User) attributes.get(SESSION_USER);
    }

    @Nullable Customer getCustomerDetails() {
        return (Customer) attributes.get(SESSION_CUSTOMER_DETAILS);
    }
}
