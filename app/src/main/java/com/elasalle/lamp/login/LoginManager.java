package com.elasalle.lamp.login;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.elasalle.lamp.LampApp;
import com.elasalle.lamp.R;
import com.elasalle.lamp.client.LampRestClient;
import com.elasalle.lamp.model.ErrorMessage;
import com.elasalle.lamp.model.login.LoginCredentials;
import com.elasalle.lamp.model.login.LoginResponse;
import com.elasalle.lamp.model.user.User;
import com.elasalle.lamp.client.ApiRequestCallback;
import com.elasalle.lamp.notification.LampNotificationsManager;
import com.elasalle.lamp.security.TokenManager;
import com.elasalle.lamp.service.CustomerDetailsTask;
import com.elasalle.lamp.util.PreferencesHelper;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginManager {

    static final String FLAG_LOGIN_REAUTHENTICATE = "Re-authenticate";

    private final LampRestClient lampRestClient;
    private final TokenManager tokenManager;
    private final CustomerDetailsTask customerDetailsTask;
    private final LampNotificationsManager lampNotificationsManager;

    @Inject
    public LoginManager(LampRestClient lampRestClient, TokenManager tokenManager, CustomerDetailsTask customerDetailsTask, LampNotificationsManager notificationsManager) {
        this.lampRestClient = lampRestClient;
        this.tokenManager = tokenManager;
        this.customerDetailsTask = customerDetailsTask;
        this.lampNotificationsManager = notificationsManager;
    }

    public void login(@NonNull final LoginCredentials credentials, @NonNull final ApiRequestCallback callback, final Runnable reAuthenticateCallback) {
        if(!TextUtils.isEmpty(credentials.getUsername()) && !TextUtils.isEmpty(credentials.getPassword())) {

            if (!LampApp.isApplicationConnected()) {
                String message = LampApp.getInstance().getString(R.string.error_no_network);
                callback.onFailure(message);
            } else {
                Call<LoginResponse> response = this.lampRestClient.login(credentials);
                response.enqueue(new Callback<LoginResponse>() {
                    @Override
                    public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                        if (response.isSuccessful()) {
                            processResponse(response.body(), credentials.getUsername(), callback, reAuthenticateCallback);
                        } else {
                            callback.onFailure(new ErrorMessage(response.errorBody(), response.message()).message);
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginResponse> call, Throwable t) {
                        clearSession();
                        String message = t != null ? t.getMessage() : LampApp.getInstance().getString(R.string.error_login_generic);
                        callback.onFailure(message);
                    }
                });
            }
        } else {
            final String message = LampApp.getInstance().getString(R.string.error_login_null_or_empty_credentials);
            callback.onFailure(message);
        }
    }

    public void getCustomerDetails(@NonNull ApiRequestCallback callback, Runnable reAuthenticateCallback) {
        customerDetailsTask.setId(LampApp.getSessionManager().user().getCustomerId());
        customerDetailsTask.setCallback(callback);
        customerDetailsTask.setReAuthenticateCallback(reAuthenticateCallback);
        customerDetailsTask.run();
    }

    private void processResponse(final LoginResponse loginResponse, final String username, ApiRequestCallback callback, Runnable reAuthenticateCallback) {
        persistUserToSession(new User(loginResponse, username));
        tokenManager.saveToken(loginResponse.token);
        getCustomerDetails(callback, reAuthenticateCallback);
    }

    private void persistUserToSession(final User user) {
        LampApp.getSessionManager().startSession(user);
    }

    private void clearSession() {
        LampApp.getSessionManager().endSession();
    }

    private static void showLoginScreen(final boolean isNewTask) {
        deleteAuthToken();
        LampApp application = LampApp.getInstance();
        Intent intent = new Intent(application.getApplicationContext(), LoginActivity.class);
        Activity topActivity = application.getTopActivity();
        if (isNewTask || topActivity == null) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            application.startActivity(intent);
        } else {
            intent.putExtra(FLAG_LOGIN_REAUTHENTICATE, true);
            topActivity.startActivity(intent);
        }
    }

    public static void showLoginScreen() {
        showLoginScreen(true);
    }

    private static void deleteAuthToken() {
        TokenManager.deleteToken();
    }

    public static void reAuthenticate() {
        showLoginScreen(false);
    }

    public void registerForPushNotifications() {
        if (PreferencesHelper.isNotificationsOn()) {
            lampNotificationsManager.registerForPushNotifications();
        }
    }
}