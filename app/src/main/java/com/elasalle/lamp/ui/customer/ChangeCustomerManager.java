package com.elasalle.lamp.ui.customer;

import android.support.annotation.NonNull;

import com.elasalle.lamp.LampApp;
import com.elasalle.lamp.client.ApiRequestCallback;
import com.elasalle.lamp.service.CustomerDetailsTask;

import javax.inject.Inject;

public class ChangeCustomerManager {

    private final CustomerDetailsTask task;

    @Inject
    public ChangeCustomerManager(CustomerDetailsTask task) {
        this.task = task;
    }

    public void changeCustomer(@NonNull final String customerId, @NonNull final ApiRequestCallback callback, Runnable reAuthenticateCallback) {
        final String currentCustomerId = LampApp.getSessionManager().user().getCustomerId();
        if (customerId.equals(currentCustomerId)) {
            callback.onSuccess();
        } else {
            this.task.setId(customerId);
            this.task.setCallback(new ApiRequestCallback() {
                @Override
                public void onSuccess() {
                    LampApp.getSessionManager().changeCurrentCustomerOnUser(customerId);
                    callback.onSuccess();
                }

                @Override
                public void onFailure(@NonNull String message) {
                    callback.onFailure(message);
                }
            });
            this.task.setReAuthenticateCallback(reAuthenticateCallback);
            this.task.run();
        }
    }
}
