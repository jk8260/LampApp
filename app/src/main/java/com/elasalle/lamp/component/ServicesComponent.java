package com.elasalle.lamp.component;

import com.elasalle.lamp.login.GuestLoginActivity;
import com.elasalle.lamp.login.LoginActivity;
import com.elasalle.lamp.login.ResetPasswordActivity;
import com.elasalle.lamp.module.DataModule;
import com.elasalle.lamp.module.NotificationModule;
import com.elasalle.lamp.module.ServicesModule;
import com.elasalle.lamp.module.TaskModule;
import com.elasalle.lamp.module.UserModule;
import com.elasalle.lamp.service.UserConfigService;
import com.elasalle.lamp.ui.dashboard.DashboardMessageFragment;

import javax.inject.Singleton;

import dagger.Component;

@Component(modules = {ServicesModule.class, TaskModule.class, UserModule.class, DataModule.class, NotificationModule.class})
@Singleton
public interface ServicesComponent {
    void inject(LoginActivity loginActivity);
    void inject(GuestLoginActivity guestLoginActivity);
    void inject(ResetPasswordActivity resetPasswordActivity);
    void inject(UserConfigService userConfigService);
    void inject(DashboardMessageFragment dashboardMessageFragment);
}
