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

public class LookupHelpTask extends LampTask {

    public static final String LOOKUP_HTML = "lookup.html";

    private static final String TAG = LookupHelpTask.class.getSimpleName();

    public LookupHelpTask(LampRestClient lampRestClient) {
        super(lampRestClient);
    }

    @Override
    public void run() {
        final String lookupTime = PreferencesHelper.getLookupTime();
        final String sessionLookupTime = LampApp.getSessionManager().getLookupTime();
        if (TextUtils.isEmpty(lookupTime) || TextUtils.isEmpty(sessionLookupTime)) {
            fetchLookupHtml();
        } else {
            try {
                final String DATE_ISO_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
                final Date lookupDate = DateUtils.parseDate(lookupTime, DATE_ISO_FORMAT);
                final Date latestLookupDate = DateUtils.parseDate(sessionLookupTime, DATE_ISO_FORMAT);
                if(lookupDate.compareTo(latestLookupDate) < 0) {
                    fetchLookupHtml();
                }
            } catch (ParseException e) {
                Log.e(TAG, e.getMessage());
                fetchLookupHtml();
            }
        }

    }

    private void fetchLookupHtml() {
        Call<ResponseBody> lookupCall = lampRestClient.lookupHelp();
        lookupCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    saveLookupFile(response.body());
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
        Log.d(TAG, "Lookup Html Request Error");
        Log.d(TAG, "Code " + response.code());
        Log.d(TAG, response.message());
        try {
            Log.d(TAG, response.errorBody().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveLookupFile(ResponseBody body) {
        try {
            Context context = LampApp.getInstance();
            FileUtils.writeByteArrayToFile(new File(context.getFilesDir(), LOOKUP_HTML), body.bytes());
            saveLookupTimeStamp();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private void saveLookupTimeStamp() {
        String timestamp = LampApp.getSessionManager().getLookupTime();
        PreferencesHelper.saveLookupTime(timestamp);
    }
}
