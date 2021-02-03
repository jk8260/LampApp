package com.elasalle.lamp.component;

import com.elasalle.lamp.main.DashboardFragment;
import com.elasalle.lamp.module.DataModule;
import com.elasalle.lamp.module.NotificationModule;
import com.elasalle.lamp.module.ServicesModule;
import com.elasalle.lamp.module.TaskModule;
import com.elasalle.lamp.module.UserModule;
import com.elasalle.lamp.ui.FooterFragment;
import com.elasalle.lamp.ui.customer.ChangeCustomerActivity;
import com.elasalle.lamp.ui.dashboard.DashboardFooterFragment;
import com.elasalle.lamp.ui.dashboard.menu.MyAccountActivity;

import javax.inject.Singleton;

import dagger.Component;

@Component(modules = {UserModule.class, TaskModule.class, ServicesModule.class, DataModule.class, NotificationModule.class})
@Singleton
public interface UserComponent {
    void inject(FooterFragment footerFragment);
    void inject(DashboardFooterFragment dashboardFooterFragment);
    void inject(MyAccountActivity myAccountActivity);
    void inject(DashboardFragment dashboardFragment);
    void inject(ChangeCustomerActivity changeCustomerActivity);
}
