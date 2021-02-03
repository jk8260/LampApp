package com.elasalle.lamp.service;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.elasalle.lamp.LampApp;
import com.elasalle.lamp.client.LampRestClient;
import com.elasalle.lamp.util.PreferencesHelper;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by abrun on 6/9/16.
 */

public class NotificationsHelpTask extends LampTask {

    public static final String NOTIFICATIONS_HTML = "notifications.html";

    private static final String TAG = NotificationsHelpTask.class.getSimpleName();

    public NotificationsHelpTask(LampRestClient lampRestClient) {
        super(lampRestClient);
    }

    @Override
    public void run() {
        final String notificationsTime = PreferencesHelper.getNotificationsTime();
        final String sessionNotificationsTime = LampApp.getSessionManager().getNotificationsTime();
        if (TextUtils.isEmpty(notificationsTime) || TextUtils.isEmpty(sessionNotificationsTime)) {
            fetchNotificationsHtml();
        } else {
            try {
                final String DATE_ISO_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
                final Date notificationsDate = DateUtils.parseDate(notificationsTime, DATE_ISO_FORMAT);
                final Date latestNotificationsDate = DateUtils.parseDate(sessionNotificationsTime, DATE_ISO_FORMAT);
                if(notificationsDate.compareTo(latestNotificationsDate) < 0) {
                    fetchNotificationsHtml();
                }
            } catch (ParseException e) {
                Log.e(TAG, e.getMessage());
                fetchNotificationsHtml();
            }
        }

    }

    private void fetchNotificationsHtml() {
        Call<ResponseBody> notificationsCall = lampRestClient.notificationsHelp();
        notificationsCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    saveNotificationsFile(response.body());
                } else {
                    logErrorResponse(response);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, t.getMessage());
            }
        });
    }

    private void logErrorResponse(Response<ResponseBody> response) {
        Log.d(TAG, "Notifications Html Request Error");
        Log.d(TAG, "Code " + response.code());
        Log.d(TAG, response.message());
        try {
            Log.d(TAG, response.errorBody().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveNotificationsFile(ResponseBody body) {
        try {
            Context context = LampApp.getInstance();
            FileUtils.writeByteArrayToFile(new File(context.getFilesDir(), NOTIFICATIONS_HTML), body.bytes());
            saveNotificationsTimeStamp();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private void saveNotificationsTimeStamp() {
        String timestamp = LampApp.getSessionManager().getNotificationsTime();
        PreferencesHelper.saveNotificationsTime(timestamp);
    }
}
