package com.elasalle.lamp.search;

import android.support.annotation.NonNull;
import android.util.Log;

import com.elasalle.lamp.client.NetworkRequestCallback;
import com.elasalle.lamp.login.LoginManager;
import com.elasalle.lamp.model.ErrorMessage;
import com.elasalle.lamp.model.asset.AssetDetails;
import com.elasalle.lamp.security.TokenManager;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.HttpURLConnection;

import javax.inject.Inject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;

public class AssetDetailManager {

    private static final String TAG = AssetDetailManager.class.getSimpleName();

    private final TokenManager tokenManager;
    private final OkHttpClient client;

    private Runnable onReauthenticateCallback;

    @Inject
    public AssetDetailManager(TokenManager tokenManager, OkHttpClient client) {
        this.tokenManager = tokenManager;
        this.client = client;
    }

    public void getAssetDetails(@NonNull final String actionsUrl, @NonNull final NetworkRequestCallback<AssetDetails> callback) {
        client.newCall((new Request.Builder().url(actionsUrl).header("token", tokenManager.getToken()).build())).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                Log.e(TAG, e.getMessage());
                callback.onFailure(e.getMessage());
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        ObjectMapper mapper = new ObjectMapper();
                        AssetDetails assetDetails = mapper.readValue(response.body().string(), AssetDetails.class);
                        callback.onSuccess(assetDetails);
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                        callback.onFailure(e.getMessage());
                    }
                } else {
                    final String message = logError(response.body(), response.message());
                    processError(response.code(), message, callback);
                }
            }
        });
    }

    private String logError(ResponseBody responseBody, String msg) {
        final String message = new ErrorMessage(responseBody, msg).message;
        Log.e(TAG, message);
        return message;
    }

    private <T> void processError(final int code, final String message, NetworkRequestCallback<T> callback) {
        if (code == HttpURLConnection.HTTP_UNAUTHORIZED) {
            reAuthenticate();
        } else if (callback != null) {
            callback.onFailure(message);
        }
    }

    private void reAuthenticate() {
        Log.i(TAG, "401: Re-authenticating user");
        LoginManager.reAuthenticate();
        onReauthenticateCallback.run();
    }

    public void setOnReauthenticateCallback(Runnable onReauthenticateCallback) {
        this.onReauthenticateCallback = onReauthenticateCallback;
    }
}
