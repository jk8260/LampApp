package com.elasalle.lamp.notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.elasalle.lamp.R;
import com.elasalle.lamp.main.MainActivity;
import com.elasalle.lamp.util.PreferencesHelper;
import com.microsoft.windowsazure.messaging.NotificationHub;
import com.microsoft.windowsazure.notifications.NotificationsHandler;

public class PushNotificationsHandler extends NotificationsHandler {

    private static final String TAG = PushNotificationsHandler.class.getSimpleName();

    public static final int NOTIFICATION_ID = 1;
    NotificationCompat.Builder builder;
    Context context;
    private NotificationManager mNotificationManager;

    @Override
    public void onRegistered(final Context context, final String gcmRegistrationId) {
        super.onRegistered(context, gcmRegistrationId);
        new AsyncTask<Void, Void, Void>() {
            protected Void doInBackground(Void... params) {
                try {
                    NotificationHub hub = new NotificationHub(PushNotificationSettings.HubName, PushNotificationSettings.HubListenConnectionString, context);
                    Log.i(TAG, "Attempting to register with NH using token : " + gcmRegistrationId);

                    String username = PreferencesHelper.getUsername();
                    if (!TextUtils.isEmpty(username)) {
                        String regID = hub.register(gcmRegistrationId, username).getRegistrationId();
                        Log.d(TAG, regID);
                    }

                    return null;
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage(), e);
                }
                return null;
            }
        }.execute();
    }

    @Override
    public void onReceive(Context context, Bundle bundle) {
        if (PreferencesHelper.isNotificationsOn()) {
            this.context = context;
            final String message = bundle.getString("title");
            sendNotification(message);
        }
    }

    private void sendNotification(@NonNull final String message) {
        mNotificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(MainActivity.PUSH_NOTIFICATION, true);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                intent, 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.home_lookup)
                        .setContentTitle(context.getString(R.string.push_notification_title))
                        .setStyle(new NotificationCompat
                                .BigTextStyle()
                                .bigText(message))
                        .setContentText(message)
                        .setAutoCancel(true);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}