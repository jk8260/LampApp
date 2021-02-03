package com.elasalle.lamp.service;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.elasalle.lamp.BuildConfig;
import com.elasalle.lamp.LampApp;
import com.elasalle.lamp.R;
import com.elasalle.lamp.client.LampRestClient;
import com.elasalle.lamp.model.ErrorMessage;
import com.elasalle.lamp.model.Status;
import com.elasalle.lamp.model.SystemMessage;
import com.elasalle.lamp.network.NetworkChangeBroadcastReceiver;
import com.elasalle.lamp.ui.BlockingMessageActivity;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.maven.artifact.versioning.ComparableVersion;
import org.joda.time.DateTime;
import org.joda.time.Hours;

import java.text.ParseException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StatusTask extends LampTask {

    private static final String TAG = StatusTask.class.getSimpleName();

    private boolean isDismissBlockingActivity;

    public StatusTask(LampRestClient lampRestClient) {
        super(lampRestClient);
    }

    @Override
    public void run() {
        if(LampApp.isApplicationConnected()) {
            Call<Status> statusCall= lampRestClient.status();
            statusCall.enqueue(new Callback<Status>() {
                @Override
                public void onResponse(Call<Status> call, Response<Status> response) {
                    if (response.isSuccessful()) {
                        final Status status = response.body();
                        if(BuildConfig.DEBUG) {
                            Log.d(TAG, status.message);
                        }
                        isDismissBlockingActivity = true;
                        checkMinAppVersion(status);
                        checkTimeDrift(status);
                        checkServerMessages(status);
                        dismissBlockingActivity();
                        LampApp.getSessionManager().setAboutTime(status.aboutLastUpdated);
                        LampApp.getSessionManager().setSearchTime(status.searchHelpLastUpdated);
                        LampApp.getSessionManager().setLookupTime(status.lookupHelpLastUpdated);
                        LampApp.getSessionManager().setScanTime(status.scanHelpLastUpdated);
                        LampApp.getSessionManager().setNotificationsTime(status.notificationsHelpLastUpdated);
                    } else {
                        logErrorResponse(response);
                    }
                }

                @Override
                public void onFailure(Call<Status> call, Throwable t) {
                    Log.e(TAG, t.getMessage());
                }
            });
        } else {
            sendNoNetworkMessage();
        }
    }

    private void sendNoNetworkMessage() {
        NetworkChangeBroadcastReceiver.sendNoNetworkMessage();
    }

    private void dismissBlockingActivity() {
        if (isDismissBlockingActivity) {
            Activity topActivity = LampApp.getInstance().getTopActivity();
            if (topActivity != null && topActivity instanceof BlockingMessageActivity) {
                displayBlockingActivity("", true);
            }
        }
    }

    private void checkServerMessages(final Status status) {
        if (status.blockingMessage) {
            displayBlockingActivity(status.message);
        } else {
            LampApp.getSessionManager().setSystemMessage(new SystemMessage(status.message, status.messageLevel));
            LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(LampApp.getInstance());
            Intent intent = new Intent(SystemMessage.SYSTEM_MESSAGE);
            intent.putExtra(SystemMessage.SYSTEM_MESSAGE, status.message);
            intent.putExtra(SystemMessage.SYSTEM_MESSAGE_LEVEL, status.messageLevel);
            localBroadcastManager.sendBroadcast(intent);
        }
    }

    private void checkTimeDrift(final Status status) {
        try {
            final DateTime deviceTime = new DateTime();
            final DateTime serverTime = new DateTime(DateUtils.parseDate(status.serverTime, "yyyy-MM-dd'T'HH:mm:ss'Z'"));
            final int hours = Hours.hoursBetween(deviceTime.toLocalDateTime(), serverTime.toLocalDateTime()).getHours();
            if (Math.abs(hours) >= 24) {
                final String message = LampApp.getInstance().getString(R.string.device_date_out_of_sync) + " " + serverTime.toString();
                displayBlockingActivity(message);
            }
        } catch (ParseException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private void checkMinAppVersion(final Status status) {
        final ComparableVersion minAppVersion = new ComparableVersion(status.minAppVersion);
        final ComparableVersion currentVersion = new ComparableVersion(BuildConfig.VERSION_NAME);
        if(currentVersion.compareTo(minAppVersion) < 0) {
            displayBlockingActivity(LampApp.getInstance().getString(R.string.application_version_mismatch));
        }
    }

    private void displayBlockingActivity(final String message) {
        isDismissBlockingActivity = false;
        displayBlockingActivity(message, false);
    }

    private void displayBlockingActivity(final String message, final boolean isDismiss) {
        Activity topActivity = LampApp.getInstance().getTopActivity();
        if (topActivity != null) {
            Intent intent = new Intent(LampApp.getInstance().getApplicationContext(), BlockingMessageActivity.class);
            intent.putExtra(BlockingMessageActivity.MESSAGE, message);
            intent.putExtra(BlockingMessageActivity.DISMISS, isDismiss);
            topActivity.startActivity(intent);
        }
    }

    private void logErrorResponse(Response<Status> response) {
        Log.e(TAG, "Status Request Error");
        Log.e(TAG, "Code " + response.code());
        Log.e(TAG, "Message " + response.message());
        Log.e(TAG, new ErrorMessage(response.errorBody(), response.message()).message);
    }
}
