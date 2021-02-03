package com.elasalle.lamp.util;

import com.elasalle.lamp.LampApp;
import com.google.android.gms.analytics.HitBuilders;

public class GoogleAnalyticsHelper {

    public static void sendAnalyticsEvent(String category, String action) {
        LampApp.getInstance().getDefaultTracker().send(new HitBuilders.EventBuilder()
                .setCategory(category)
                .setAction(action)
                .build());
    }

    public static void sendAnalyticsEvent(String category, String action, String label) {
        LampApp.getInstance().getDefaultTracker().send(new HitBuilders.EventBuilder()
                .setCategory(category)
                .setAction(action)
                .setLabel(label)
                .build());
    }

    public static void sendAnalyticsEvent(String category, String action, String label, long value) {
        LampApp.getInstance().getDefaultTracker().send(new HitBuilders.EventBuilder()
                .setCategory(category)
                .setAction(action)
                .setLabel(label)
                .setValue(value)
                .build());
    }
}
