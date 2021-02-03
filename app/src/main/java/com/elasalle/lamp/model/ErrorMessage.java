package com.elasalle.lamp.model;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;

public class ErrorMessage {
    private static final String KEY_ERROR_MESSAGE = "message";
    public String message;
    public ErrorMessage(@NonNull ResponseBody responseBody, @NonNull String responseMessage) {
        try {
            JSONObject jsonObject = new JSONObject(responseBody.string());
            this.message = jsonObject.getString(KEY_ERROR_MESSAGE);
        } catch (IOException|JSONException e) {
            e.printStackTrace();
        } finally {
            if (TextUtils.isEmpty(message)) {
                message = responseMessage;
            }
        }
    }
}
