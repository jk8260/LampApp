package com.elasalle.lamp.login;

import android.support.annotation.NonNull;
import android.util.Log;

import com.elasalle.lamp.LampApp;
import com.elasalle.lamp.R;
import com.elasalle.lamp.client.LampRestClient;
import com.elasalle.lamp.model.ErrorMessage;
import com.elasalle.lamp.client.ApiRequestCallback;

import javax.inject.Inject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPasswordManager {

    private final LampRestClient lampRestClient;

    @Inject
    public ResetPasswordManager(LampRestClient lampRestClient) {
        this.lampRestClient = lampRestClient;
    }

    public void reset(@NonNull final String username, @NonNull final ApiRequestCallback callback) {
        if (!LampApp.isApplicationConnected()) {
            String message = LampApp.getInstance().getString(R.string.error_no_network);
            callback.onFailure(message);
        } else {
            Call<ResponseBody> call = lampRestClient.resetPassword(username);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        callback.onSuccess();
                    } else {
                        final ErrorMessage errorMessage = new ErrorMessage(response.errorBody(), response.message());
                        callback.onFailure(errorMessage.message);
                        Log.e(getClass().getSimpleName(), errorMessage.message);
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    callback.onFailure(t.getMessage());
                    Log.e(getClass().getSimpleName(), t.getMessage());
                }
            });
        }
    }

}
