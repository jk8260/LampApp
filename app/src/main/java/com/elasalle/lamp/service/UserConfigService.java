package com.elasalle.lamp.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.elasalle.lamp.LampApp;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import javax.inject.Inject;

import dagger.Lazy;

public class UserConfigService extends IntentService {

    @Inject ExecutorService executorService;
    @Inject Lazy<AboutTask> aboutTask;
    @Inject Lazy<StatusTask> statusTask;
    @Inject Lazy<CustomerDetailsTask> customerDetailsTask;
    @Inject Lazy<GuestTask> guestTask;
    @Inject Lazy<NotificationsTask> notificationsTask;
    @Inject Lazy<SearchHelpTask> searchHelpTask;
    @Inject Lazy<LookupHelpTask> lookupHelpTask;
    @Inject Lazy<ScanHelpTask> scanHelpTask;
    @Inject Lazy<NotificationsHelpTask> notificationHelpTask;

    public UserConfigService() {
        super("LAMP Intent Service");
        LampApp.servicesComponent().inject(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        startTasks(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }

    private void startTasks(Intent intent) {
        if (intent != null && LampTask.INTENT_ACTION.equals(intent.getAction())) {
            executeTask(intent);
        } else {
            executeAllTasks();
        }
    }

    private void executeTask(Intent intent) {
        final String[] tasks = intent.getStringArrayExtra(LampTask.EXTRA_NAME);
        for (final String task : tasks) {
            if (task.equals(LampTask.Type.ABOUT.name())) {
                executorService.execute(aboutTask.get());
            } else if (task.equals(LampTask.Type.STATUS.name())) {
                executorService.execute(statusTask.get());
            } else if (task.equals(LampTask.Type.CUSTOMER_DETAILS.name())) {
                executorService.execute(customerDetailsTask.get());
            } else if (task.equals(LampTask.Type.GUEST.name())) {
                executorService.execute(guestTask.get());
            } else if (task.equals(LampTask.Type.NOTIFICATIONS.name())) {
                executorService.execute(notificationsTask.get());
            } else if (task.equals(LampTask.Type.SEARCH_HELP.name())) {
                executorService.execute(searchHelpTask.get());
            } else if (task.equals(LampTask.Type.LOOKUP_HELP.name())) {
                executorService.execute(lookupHelpTask.get());
            } else if (task.equals(LampTask.Type.SCAN_HELP.name())) {
                executorService.execute(scanHelpTask.get());
            } else if (task.equals(LampTask.Type.NOTIFICATIONS_HELP.name())) {
                executorService.execute(notificationHelpTask.get());
            }
        }
    }

    private void executeAllTasks() {
        final List<LampTask> tasks = getTasks();
        for (LampTask task : tasks) {
            executorService.execute(task);
        }
    }

    @NonNull
    private ArrayList<LampTask> getTasks() {
        return new ArrayList<LampTask>() {
            {
                add(aboutTask.get());
                add(statusTask.get());
                add(customerDetailsTask.get());
                add(guestTask.get());
                add(notificationsTask.get());
            }
        };
    }
}
