package com.elasalle.lamp.component;

import com.elasalle.lamp.LampApp;
import com.elasalle.lamp.module.ApplicationModule;
import com.elasalle.lamp.module.DataModule;
import com.elasalle.lamp.module.UserModule;

import javax.inject.Singleton;

import dagger.Component;

@Component(modules = {ApplicationModule.class, DataModule.class, UserModule.class})
@Singleton
public interface ApplicationComponent {
    void inject(LampApp app);
}
