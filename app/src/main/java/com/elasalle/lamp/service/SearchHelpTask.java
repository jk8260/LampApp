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

public class SearchHelpTask extends LampTask {

    public static final String SEARCH_HTML = "search.html";

    private static final String TAG = SearchHelpTask.class.getSimpleName();

    public SearchHelpTask(LampRestClient lampRestClient) {
        super(lampRestClient);
    }

    @Override
    public void run() {
        final String searchTime = PreferencesHelper.getSearchTime();
        final String sessionSearchTime = LampApp.getSessionManager().getSearchTime();
        if (TextUtils.isEmpty(searchTime) || TextUtils.isEmpty(sessionSearchTime)) {
            fetchSearchHtml();
        } else {
            try {
                final String DATE_ISO_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
                final Date searchDate = DateUtils.parseDate(searchTime, DATE_ISO_FORMAT);
                final Date latestSearchDate = DateUtils.parseDate(sessionSearchTime, DATE_ISO_FORMAT);
                if(searchDate.compareTo(latestSearchDate) < 0) {
                    fetchSearchHtml();
                }
            } catch (ParseException e) {
                Log.e(TAG, e.getMessage());
                fetchSearchHtml();
            }
        }

    }

    private void fetchSearchHtml() {
        Call<ResponseBody> searchCall = lampRestClient.searchHelp();
        searchCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    saveSearchFile(response.body());
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
        Log.d(TAG, "Search Html Request Error");
        Log.d(TAG, "Code " + response.code());
        Log.d(TAG, response.message());
        try {
            Log.d(TAG, response.errorBody().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveSearchFile(ResponseBody body) {
        try {
            Context context = LampApp.getInstance();
            FileUtils.writeByteArrayToFile(new File(context.getFilesDir(), SEARCH_HTML), body.bytes());
            saveSearchTimeStamp();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private void saveSearchTimeStamp() {
        String timestamp = LampApp.getSessionManager().getSearchTime();
        PreferencesHelper.saveSearchTime(timestamp);
    }
}
