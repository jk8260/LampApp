package com.elasalle.lamp.notification;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.elasalle.lamp.LampApp;
import com.elasalle.lamp.client.LampRestClient;
import com.elasalle.lamp.data.repository.NotificationRepository;
import com.elasalle.lamp.login.LoginManager;
import com.elasalle.lamp.model.ErrorMessage;
import com.elasalle.lamp.model.notification.Notification;
import com.elasalle.lamp.model.notification.NotificationUpload;
import com.elasalle.lamp.security.TokenManager;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.microsoft.windowsazure.notifications.NotificationsManager;

public class LampNotificationsManager {

    private static final String TAG = LampNotificationsManager.class.getSimpleName();

    public static final String NOTIFICATIONS = "Notifications";
    public static final String NOTIFICATIONS_DOWNLOADED = "Notifications Downloaded";
    public static final String NOTIFICATIONS_CHANGED = "Notifications Changed";
    public static final String NOTIFICATION_PRIVILEGE = "EnableMobileNotifications";

    private final TokenManager tokenManager;
    private final LampRestClient client;
    private final NotificationRepository notificationRepository;
    private final NotificationsBroadcastReceiver notificationsBroadcastReceiver;

    public static MobileServiceClient mClient;

    @Inject
    public LampNotificationsManager(TokenManager tokenManager, LampRestClient client, NotificationRepository notificationRepository, NotificationsBroadcastReceiver notificationsBroadcastReceiver) {
        this.tokenManager = tokenManager;
        this.client = client;
        this.notificationRepository = notificationRepository;
        this.notificationsBroadcastReceiver = notificationsBroadcastReceiver;
    }

    public String getValueForNotificationsBadge() {
        String value = null;
        List<Notification> notifications = filterByDate(notificationRepository.findAll());
        if (notifications != null && notifications.size() > 0) {
            if (notifications.size() > 99) {
                value = "99+";
            } else {
                value = "" + notifications.size();
            }
        }
        return value;
    }

    private List<Notification> filterByDate(final List<Notification> notifications) {
        final List<Notification> filteredNotifications = new ArrayList<>();
        for (final Notification notification : notifications) {
            final DateTimeFormatter parser = ISODateTimeFormat.dateTimeParser();
            final DateTime dateTime = parser.parseDateTime(notification.createdDate);
            if (!notification.read && !notification.deleted && dateTime.plusMonths(1).isAfterNow()) {
                filteredNotifications.add(notification);
            }
        }
        return filteredNotifications;
    }

    public List<Notification> getNotifications() {
        return filterByDate(notificationRepository.findAll());
    }

    public void downloadNotifications() {
        client.getNotifications(tokenManager.getToken()).enqueue(new Callback<List<Notification>>() {
            @Override
            public void onResponse(Call<List<Notification>> call, Response<List<Notification>> response) {
                if (response.isSuccessful()) {
                    List<Notification> notifications = response.body();
                    for (Notification notification : notifications) {
                        notificationRepository.save(notification);
                    }
                    Log.i(TAG, String.format("%d notifications successfully downloaded.", notifications.size()));
                    broadcastDownloadSuccessfullyCompleted();
                } else {
                    ErrorMessage message = new ErrorMessage(response.errorBody(), response.message());
                    Log.e(TAG, message.message);
                    if (response.code() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                        LoginManager.reAuthenticate();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Notification>> call, Throwable t) {
                Log.e(TAG, t.getMessage(), t);
            }
        });
    }

    private void broadcastDownloadSuccessfullyCompleted() {
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(LampApp.getInstance());
        Intent intent = new Intent(NOTIFICATIONS);
        intent.putExtra(NOTIFICATIONS_DOWNLOADED, true);
        localBroadcastManager.sendBroadcast(intent);
    }

    private void postNotificationsChange() {
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(LampApp.getInstance());
        Intent intent = new Intent(NOTIFICATIONS);
        intent.putExtra(NOTIFICATIONS_CHANGED, true);
        localBroadcastManager.sendBroadcast(intent);
    }

    public void unregisterForNotifications(Activity activity) {
        LocalBroadcastManager.getInstance(activity).unregisterReceiver(notificationsBroadcastReceiver);
    }

    public void registerForNotifications(Activity activity) {
        LocalBroadcastManager.getInstance(activity).registerReceiver(notificationsBroadcastReceiver, new IntentFilter(LampNotificationsManager.NOTIFICATIONS));
    }

    public void onNotificationsDownloaded(Runnable task) {
        this.notificationsBroadcastReceiver.setNotificationsDownloadedTask(task);
    }

    public void onNotificationsChanged(Runnable task) {
        this.notificationsBroadcastReceiver.setNotificationsChangedTask(task);
    }

    public void markAsRead(Notification notification) {
        notification.read = true;
        final ISO8601DateFormat format = new ISO8601DateFormat();
        notification.dateRead = format.format(new Date());
        notificationRepository.save(notification);
        postNotificationsChange();
    }

    public void delete(Notification notification) {
        notification.deleted = true;
        notificationRepository.save(notification);
        postNotificationsChange();
    }

    public void updateNotifications() {
        final List<Notification> notifications = notificationRepository.findAll();
        if (notifications != null && !notifications.isEmpty()) {
            final List<NotificationUpload> notificationUploads = new ArrayList<>();
            for (final Notification notification : notifications) {
                NotificationUpload upload = new NotificationUpload();
                upload.id = notification.id;
                upload.read = notification.read;
                upload.dateRead = notification.dateRead;
                upload.deleted = notification.deleted;
                notificationUploads.add(upload);
            }
            client.updateNotifications(tokenManager.getToken(), notificationUploads).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        Log.i(TAG, "Notifications successfully updated.");
                    } else {
                        ErrorMessage message = new ErrorMessage(response.errorBody(), response.message());
                        Log.e(TAG, message.message);
                        if (response.code() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                            LoginManager.reAuthenticate();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.e(TAG, t.getMessage(), t);
                }
            });
        }
    }

    public void delete(String notificationId) {
        Notification notification = notificationRepository.findOneById(notificationId);
        delete(notification);
    }

    public void registerForPushNotifications() {
        NotificationsManager.handleNotifications(LampApp.getInstance(), PushNotificationSettings.SenderId, PushNotificationsHandler.class);
    }

    public void unregisterForPushNotifications() {
        NotificationsManager.stopHandlingNotifications(LampApp.getInstance());
    }
}
