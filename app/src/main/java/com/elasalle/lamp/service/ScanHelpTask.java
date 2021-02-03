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

public class ScanHelpTask extends LampTask {

    public static final String SCAN_HTML = "scan.html";

    private static final String TAG = ScanHelpTask.class.getSimpleName();

    public ScanHelpTask(LampRestClient lampRestClient) {
        super(lampRestClient);
    }

    @Override
    public void run() {
        final String scanTime = PreferencesHelper.getScanTime();
        final String sessionScanTime = LampApp.getSessionManager().getScanTime();
        if (TextUtils.isEmpty(scanTime) || TextUtils.isEmpty(sessionScanTime)) {
            fetchScanHtml();
        } else {
            try {
                final String DATE_ISO_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
                final Date scanDate = DateUtils.parseDate(scanTime, DATE_ISO_FORMAT);
                final Date latestScanDate = DateUtils.parseDate(sessionScanTime, DATE_ISO_FORMAT);
                if(scanDate.compareTo(latestScanDate) < 0) {
                    fetchScanHtml();
                }
            } catch (ParseException e) {
                Log.e(TAG, e.getMessage());
                fetchScanHtml();
            }
        }

    }

    private void fetchScanHtml() {
        Call<ResponseBody> scanCall = lampRestClient.scanHelp();
        scanCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    saveScanFile(response.body());
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
        Log.d(TAG, "Scan Html Request Error");
        Log.d(TAG, "Code " + response.code());
        Log.d(TAG, response.message());
        try {
            Log.d(TAG, response.errorBody().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveScanFile(ResponseBody body) {
        try {
            Context context = LampApp.getInstance();
            FileUtils.writeByteArrayToFile(new File(context.getFilesDir(), SCAN_HTML), body.bytes());
            saveScanTimeStamp();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private void saveScanTimeStamp() {
        String timestamp = LampApp.getSessionManager().getScanTime();
        PreferencesHelper.saveScanTime(timestamp);
    }
}
