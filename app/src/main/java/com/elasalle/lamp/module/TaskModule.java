package com.elasalle.lamp.module;

import android.support.annotation.Nullable;

import com.elasalle.lamp.LampApp;
import com.elasalle.lamp.client.LampRestClient;
import com.elasalle.lamp.data.repository.FieldLookupListRepository;
import com.elasalle.lamp.data.repository.GuestLoginRepository;
import com.elasalle.lamp.model.user.User;
import com.elasalle.lamp.notification.LampNotificationsManager;
import com.elasalle.lamp.security.TokenManager;
import com.elasalle.lamp.service.AboutTask;
import com.elasalle.lamp.service.CustomerDetailsTask;
import com.elasalle.lamp.service.GuestTask;
import com.elasalle.lamp.service.LookupHelpTask;
import com.elasalle.lamp.service.LookupListTask;
import com.elasalle.lamp.service.NotificationsHelpTask;
import com.elasalle.lamp.service.NotificationsTask;
import com.elasalle.lamp.service.ScanHelpTask;
import com.elasalle.lamp.service.SearchHelpTask;
import com.elasalle.lamp.service.StatusTask;
import com.elasalle.lamp.service.SystemMessageBroadcastReceiver;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;

@Module
@Singleton
public class TaskModule {

    @Provides
    public ExecutorService executorService() {
        return Executors.newCachedThreadPool();
    }

    @Provides
    @Singleton
    public AboutTask aboutTask(LampRestClient lampRestClient) {
        return new AboutTask(lampRestClient);
    }

    @Provides
    @Singleton
    public SearchHelpTask searchHelpTask(LampRestClient lampRestClient) {
        return new SearchHelpTask(lampRestClient);
    }

    @Provides
    @Singleton
    public LookupHelpTask lookupHelpTask(LampRestClient lampRestClient) {
        return new LookupHelpTask(lampRestClient);
    }

    @Provides
    @Singleton
    public ScanHelpTask scanHelpTask(LampRestClient lampRestClient) {
        return new ScanHelpTask(lampRestClient);
    }

    @Provides
    @Singleton
    public NotificationsHelpTask notificationsHelpTask(LampRestClient lampRestClient) {
        return new NotificationsHelpTask(lampRestClient);
    }

    @Provides
    @Singleton
    public StatusTask statusTask(LampRestClient lampRestClient) {
        return new StatusTask(lampRestClient);
    }

    @Provides
    public CustomerDetailsTask customerDetailsTask(LampRestClient lampRestClient, @Nullable String customerId, TokenManager tokenManager, LookupListTask lookupListTask) {
        CustomerDetailsTask customerDetailsTask = new CustomerDetailsTask(lampRestClient, tokenManager, lookupListTask);
        customerDetailsTask.setId(customerId);
        return  customerDetailsTask;
    }

    @Provides
    public GuestTask guestTask(LampRestClient lampRestClient, GuestLoginRepository guestLoginRepository) {
        return new GuestTask(lampRestClient, guestLoginRepository);
    }

    @Provides
    public SystemMessageBroadcastReceiver systemMessageBroadcastReceiver() {
        return new SystemMessageBroadcastReceiver();
    }

    @Provides @Nullable String customerId() {
        User user = LampApp.getSessionManager().user();
        String customerId = null;
        if (user != null) {
            customerId = user.getCustomerId();
        }
        return customerId;
    }

    @Provides
    LookupListTask lookupListTask(OkHttpClient client, TokenManager tokenManager, FieldLookupListRepository repository) {
        return new LookupListTask(null, client, tokenManager, repository);
    }

    @Provides
    NotificationsTask notificationsTask(LampNotificationsManager lampNotificationsManager) {
        return new NotificationsTask(null, lampNotificationsManager);
    }
}
