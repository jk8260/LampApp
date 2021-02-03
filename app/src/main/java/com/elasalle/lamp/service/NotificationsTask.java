package com.elasalle.lamp.service;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.elasalle.lamp.client.LampRestClient;
import com.elasalle.lamp.notification.LampNotificationsManager;

import javax.inject.Inject;

public class NotificationsTask extends LampTask {

    private final LampNotificationsManager lampNotificationsManager;

    @Inject
    public NotificationsTask(@Nullable LampRestClient lampRestClient, @NonNull LampNotificationsManager lampNotificationsManager) {
        super(lampRestClient);
        this.lampNotificationsManager = lampNotificationsManager;
    }

    @Override
    public void run() {
        lampNotificationsManager.updateNotifications();
        lampNotificationsManager.downloadNotifications();
    }
}
