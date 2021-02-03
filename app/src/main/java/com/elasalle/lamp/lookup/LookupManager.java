package com.elasalle.lamp.lookup;

import android.support.annotation.NonNull;
import android.util.Log;

import com.elasalle.lamp.client.LampRestClient;
import com.elasalle.lamp.client.SearchQueryCallback;
import com.elasalle.lamp.login.LoginManager;
import com.elasalle.lamp.model.ErrorMessage;
import com.elasalle.lamp.model.lookup.LookupResponse;
import com.elasalle.lamp.security.TokenManager;

import java.net.HttpURLConnection;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LookupManager {

    private static final String TAG = LookupManager.class.getSimpleName();

    private final LampRestClient lampRestClient;
    private final TokenManager tokenManager;

    @Inject
    public LookupManager(LampRestClient lampRestClient, TokenManager tokenManager) {
        this.lampRestClient = lampRestClient;
        this.tokenManager = tokenManager;
    }

    public void lookup(@NonNull final String serialNumber, @NonNull final SearchQueryCallback<LookupResponse> callback) {
        Call<LookupResponse> call = lampRestClient.lookup(tokenManager.getToken(), serialNumber);
        call.enqueue(new Callback<LookupResponse>() {
            @Override
            public void onResponse(Call<LookupResponse> call, Response<LookupResponse> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    if (response.code() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                        LoginManager.reAuthenticate();
                    } else {
                        onError(response, callback);
                    }
                }
            }

            @Override
            public void onFailure(Call<LookupResponse> call, Throwable t) {
                onError(t, callback);
            }
        });
    }

    private void onError(Response<?> response, @NonNull SearchQueryCallback<?> callback) {
        final String message = new ErrorMessage(response.errorBody(), response.message()).message;
        error(callback, message);
    }

    private void onError(Throwable t, @NonNull SearchQueryCallback<?> callback) {
        error(callback, t.getMessage());
    }

    private void error(@NonNull SearchQueryCallback<?> callback, String message) {
        Log.e(TAG, message);
        callback.onFailure(message);
    }
}
