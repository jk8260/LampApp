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

public class AboutTask extends LampTask {

    public static final String ABOUT_HTML = "about.html";

    private static final String TAG = AboutTask.class.getSimpleName();

    public AboutTask(LampRestClient lampRestClient) {
        super(lampRestClient);
    }

    @Override
    public void run() {
        final String aboutTime = PreferencesHelper.getAboutTime();
        final String sessionAboutTime = LampApp.getSessionManager().getAboutTime();
        if (TextUtils.isEmpty(aboutTime) || TextUtils.isEmpty(sessionAboutTime)) {
            fetchAboutHtml();
        } else {
            try {
                final String DATE_ISO_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
                final Date aboutDate = DateUtils.parseDate(aboutTime, DATE_ISO_FORMAT);
                final Date latestAboutDate = DateUtils.parseDate(sessionAboutTime, DATE_ISO_FORMAT);
                if(aboutDate.compareTo(latestAboutDate) < 0) {
                    fetchAboutHtml();
                }
            } catch (ParseException e) {
                Log.e(TAG, e.getMessage());
                fetchAboutHtml();
            }
        }

    }

    private void fetchAboutHtml() {
        Call<ResponseBody> aboutCall = lampRestClient.about();
        aboutCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    saveAboutFile(response.body());
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
        Log.d(TAG, "About Html Request Error");
        Log.d(TAG, "Code " + response.code());
        Log.d(TAG, response.message());
        try {
            Log.d(TAG, response.errorBody().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveAboutFile(ResponseBody body) {
        try {
            Context context = LampApp.getInstance();
            FileUtils.writeByteArrayToFile(new File(context.getFilesDir(), ABOUT_HTML), body.bytes());
            saveAboutTimeStamp();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private void saveAboutTimeStamp() {
        String timestamp = LampApp.getSessionManager().getAboutTime();
        PreferencesHelper.saveAboutTime(timestamp);
    }
}
