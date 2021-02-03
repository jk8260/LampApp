package com.elasalle.lamp.service;

import android.util.Log;

import com.elasalle.lamp.LampApp;
import com.elasalle.lamp.R;
import com.elasalle.lamp.client.LampRestClient;
import com.elasalle.lamp.login.LoginManager;
import com.elasalle.lamp.model.user.Customer;
import com.elasalle.lamp.model.ErrorMessage;
import com.elasalle.lamp.client.ApiRequestCallback;
import com.elasalle.lamp.model.user.Field;
import com.elasalle.lamp.security.TokenManager;

import java.net.HttpURLConnection;
import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerDetailsTask extends LampTask {

    private final TokenManager tokenManager;
    private final LookupListTask lookupListTask;

    private String id;
    private ApiRequestCallback callback;
    private Runnable reAuthenticateCallback;

    @Inject
    public CustomerDetailsTask(LampRestClient lampRestClient, TokenManager tokenManager, LookupListTask lookupListTask) {
        super(lampRestClient);
        this.tokenManager = tokenManager;
        this.lookupListTask = lookupListTask;
    }

    @Override
    public void run() {
        if (id != null) {
            Call<Customer> call = lampRestClient.customerDetails(tokenManager.getToken(), id.trim());
            call.enqueue(new Callback<Customer>() {
                @Override
                public void onResponse(Call<Customer> call, Response<Customer> response) {
                    if (response.isSuccessful()) {
                        final Customer customer = response.body();
                        tokenManager.saveToken(customer.removeToken());
                        LampApp.getSessionManager().setCustomerDetails(customer);
                        if (callback != null) {
                            callback.onSuccess();
                            fetchLookupLists(customer.getFields());
                        }
                    } else {
                        final String message = new ErrorMessage(response.errorBody(), response.message()).message;
                        Log.e(getClass().getSimpleName(), message);
                        if (response.code() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                            if (reAuthenticateCallback != null) {
                                reAuthenticateCallback.run();
                            }
                            LoginManager.reAuthenticate();
                        } else {
                            if (callback != null) {
                                callback.onFailure(message);
                            }
                        }
                    }
                    callback = null;
                    reAuthenticateCallback = null;
                }

                @Override
                public void onFailure(Call<Customer> call, Throwable t) {
                    Log.e(getClass().getSimpleName(), t.getMessage());
                    if (callback != null) {
                        callback.onFailure(LampApp.getInstance().getString(R.string.error_customer_details_generic));
                    }
                    callback = null;
                    reAuthenticateCallback = null;
                }
            });
        }
    }

    private void fetchLookupLists(List<Field> fields) {
        for (Field field : fields) {
            if (Field.LOOKUP_LIST_TYPE.equals(field.getType())) {
                lookupListTask.addField(field);
            }
        }
        lookupListTask.run();
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCallback(ApiRequestCallback callback) {
        this.callback = callback;
    }

    public void setReAuthenticateCallback(Runnable reAuthenticateCallback) {
        this.reAuthenticateCallback = reAuthenticateCallback;
    }
}
