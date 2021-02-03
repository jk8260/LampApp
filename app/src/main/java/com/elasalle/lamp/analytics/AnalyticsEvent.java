package com.elasalle.lamp.analytics;

public interface AnalyticsEvent {

    void sendAnalyticsEvent(String action, String label);

}
