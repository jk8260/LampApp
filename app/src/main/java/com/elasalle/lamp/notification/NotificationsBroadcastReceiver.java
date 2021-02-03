package com.elasalle.lamp.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationsBroadcastReceiver extends BroadcastReceiver {

    private Runnable notificationsDownloadedTask;
    private Runnable notificationsChangedTask;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(LampNotificationsManager.NOTIFICATIONS)) {
            final boolean isNotificationsDownloaded = intent.getBooleanExtra(LampNotificationsManager.NOTIFICATIONS_DOWNLOADED, false);
            final boolean isNotificationsChanged = intent.getBooleanExtra(LampNotificationsManager.NOTIFICATIONS_CHANGED, false);
            if (isNotificationsDownloaded) {
                if (notificationsDownloadedTask != null) {
                    notificationsDownloadedTask.run();
                }
            }
            if (isNotificationsChanged) {
                if (notificationsChangedTask != null) {
                    notificationsChangedTask.run();
                }
            }
        }
    }

    public void setNotificationsDownloadedTask(Runnable task) {
        this.notificationsDownloadedTask = task;
    }

    public void setNotificationsChangedTask(Runnable task) {
        this.notificationsChangedTask = task;
    }
}
