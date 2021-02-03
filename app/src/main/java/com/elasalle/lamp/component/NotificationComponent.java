package com.elasalle.lamp.component;

import com.elasalle.lamp.main.DashboardFragment;
import com.elasalle.lamp.module.DataModule;
import com.elasalle.lamp.module.NotificationModule;
import com.elasalle.lamp.module.ServicesModule;
import com.elasalle.lamp.module.UIModule;
import com.elasalle.lamp.module.UserModule;
import com.elasalle.lamp.notification.NotificationDetailActivity;
import com.elasalle.lamp.notification.NotificationsFragment;

import javax.inject.Singleton;

import dagger.Component;

@Component(modules = {UserModule.class, DataModule.class, NotificationModule.class, ServicesModule.class, UIModule.class})
@Singleton
public interface NotificationComponent {
    void inject(DashboardFragment dashboardFragment);
    void inject(NotificationsFragment notificationsFragment);
    void inject(NotificationDetailActivity notificationDetailActivity);
}
