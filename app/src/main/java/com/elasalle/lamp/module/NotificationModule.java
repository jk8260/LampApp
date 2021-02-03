package com.elasalle.lamp.module;

import com.elasalle.lamp.client.LampRestClient;
import com.elasalle.lamp.data.repository.NotificationRepository;
import com.elasalle.lamp.notification.NotificationsBroadcastReceiver;
import com.elasalle.lamp.notification.LampNotificationsManager;
import com.elasalle.lamp.security.TokenManager;

import dagger.Module;
import dagger.Provides;

@Module
public class NotificationModule {

    @Provides
    LampNotificationsManager notificationsManager(TokenManager tokenManager, LampRestClient client, NotificationRepository notificationRepository, NotificationsBroadcastReceiver notificationsBroadcastReceiver) {
        return new LampNotificationsManager(tokenManager, client, notificationRepository, notificationsBroadcastReceiver);
    }

    @Provides
    NotificationsBroadcastReceiver notificationsBroadcastReceiver() {
        return new NotificationsBroadcastReceiver();
    }

}
