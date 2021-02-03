package com.elasalle.lamp.service;

import com.elasalle.lamp.LampApp;
import com.elasalle.lamp.client.LampRestClient;

public abstract class LampTask implements Runnable {

    public static final String EXTRA_NAME = LampApp.getInstance().getPackageName() + ".TASKS";
    public static final String INTENT_ACTION = LampApp.getInstance().getPackageName() + ".ACTION_EXECUTE_TASKS";

    public enum  Type { ABOUT, STATUS, CUSTOMER_DETAILS, GUEST, LOOKUP_LIST, NOTIFICATIONS, SEARCH_HELP, LOOKUP_HELP, SCAN_HELP, NOTIFICATIONS_HELP }

    LampRestClient lampRestClient;

    public LampTask(LampRestClient lampRestClient) {
        this.lampRestClient = lampRestClient;
    }

}
