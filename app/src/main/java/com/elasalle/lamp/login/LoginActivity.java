package com.elasalle.lamp.login;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;

import com.elasalle.lamp.BuildConfig;
import com.elasalle.lamp.LampApp;
import com.elasalle.lamp.R;
import com.elasalle.lamp.analytics.AnalyticsActivity;
import com.elasalle.lamp.client.ApiRequestCallback;
import com.elasalle.lamp.main.MainActivity;
import com.elasalle.lamp.model.login.LoginCredentials;
import com.elasalle.lamp.service.LampTask;
import com.elasalle.lamp.service.UserConfigService;
import com.elasalle.lamp.util.MessageHelper;
import com.elasalle.lamp.util.PreferencesHelper;

import java.lang.ref.WeakReference;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AnalyticsActivity {

    @Inject LoginManager loginManager;

    @BindView(R.id.login_username) EditText loginUsername;
    @BindView(R.id.login_password) EditText loginPassword;
    @BindView(R.id.login_button) View loginButton;
    @BindView(R.id.login_guest) View guestLoginButton;
    @BindView(R.id.login_remember_me) CheckBox rememberMe;
    @BindView(R.id.login_password_forgotten) View forgotPassword;
    @BindView(R.id.progressBar) View progressBar;

    @Override
    protected void setAnalyticsScreenName() {
        mTracker.setScreenName(getString(R.string.analytics_login));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        LampApp.servicesComponent().inject(this);
        setListeners();
    }

    private void setListeners() {
        loginPassword.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    dismissSoftKeyboard();
                    login();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        restoreUsername();
    }

    @SuppressLint("SetTextI18n")
        private void restoreUsername() {
        Boolean rememberMe = PreferencesHelper.getRememberMe();
        String username = PreferencesHelper.getUsername();
        if (rememberMe && !TextUtils.isEmpty(username)) {
            loginUsername.setText(username);
            this.rememberMe.setChecked(true);
        }
    }

    public void login(View view) {
        login();
    }

    private void login() {
        final String username = this.loginUsername.getText().toString();
        final String password = this.loginPassword.getText().toString();
        if (isUsernameAndPasswordFilled(username, password)) {
            showProgressBar();
            final WeakReference<LoginActivity> loginActivityWeakReference = new WeakReference<>(this);
            final Runnable onReAuthenticateCallback = new Runnable() {
                @Override
                public void run() {
                    if (loginActivityWeakReference.get() != null) {
                        loginActivityWeakReference.get().dismissProgressBar();
                        loginActivityWeakReference.get().displayMessage(getString(R.string.error_login_generic));
                    }
                }
            };
            this.loginManager.login(new LoginCredentials(username, password), new ApiRequestCallback() {
                @Override
                public void onSuccess() {
                    checkRememberMe();
                    startConfigService();
                    showMainScreen();
                    loginManager.registerForPushNotifications();
                    dismissProgressBar();
                    finish();
                }

                @Override
                public void onFailure(@NonNull String message) {
                    onApiCallFailure(message);
                }
            }, onReAuthenticateCallback);
        }
    }

    private void onApiCallFailure(@NonNull String message) {
        deleteUsername();
        dismissProgressBar();
        if (!TextUtils.isEmpty(message)) {
            displayMessage(message);
        }
        setInputViewsEnabled(true);
    }

    private void displayMessage(String message) {
        MessageHelper.displayMessage(LoginActivity.this, message, null);
    }

    private void deleteUsername() {
        PreferencesHelper.removeUsername();
    }

    private void checkRememberMe() {
        PreferencesHelper.saveRememberMe(rememberMe.isChecked());
        PreferencesHelper.saveUsername(loginUsername.getText().toString());
    }

    private void startConfigService() {
        final Intent intent = new Intent(this, UserConfigService.class);
        intent.setAction(LampTask.INTENT_ACTION)
                .putExtra(LampTask.EXTRA_NAME, new String[]{
                        LampTask.Type.NOTIFICATIONS.name(),
                        LampTask.Type.GUEST.name()
                });
        startService(intent);
    }

    private void showMainScreen() {
        final boolean isReauthenticate = getIntent().getBooleanExtra(LoginManager.FLAG_LOGIN_REAUTHENTICATE, false);
        if (!isReauthenticate) {
            final Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

    private boolean isUsernameAndPasswordFilled(final String username, final String password) {
        boolean isUsernameAndPasswordFilled = true;
        if (TextUtils.isEmpty(username)) {
            isUsernameAndPasswordFilled = false;
            this.loginUsername.setError(getString(R.string.error_login_username));
        }
        if (TextUtils.isEmpty(password)) {
            isUsernameAndPasswordFilled = false;
            this.loginPassword.setError(getString(R.string.error_login_password));
        }
        return isUsernameAndPasswordFilled;
    }

    private void showProgressBar() {
        setInputViewsEnabled(false);
        this.progressBar.setVisibility(View.VISIBLE);
    }

    private void dismissProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    private void setInputViewsEnabled(final boolean enabled) {
        loginUsername.setEnabled(enabled);
        loginPassword.setEnabled(enabled);
        loginButton.setEnabled(enabled);
        guestLoginButton.setEnabled(enabled);
        rememberMe.setEnabled(enabled);
        forgotPassword.setEnabled(enabled);
    }

    public void guestLogin(View view) {
        Intent intent = new Intent(this, GuestLoginActivity.class);
        startActivity(intent);
    }

    public void forgotPassword(View view) {
        Intent intent = new Intent(this, ResetPasswordActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        final int activityStackSize = LampApp.getInstance().getActivityStackSize();
        //noinspection StatementWithEmptyBody
        if (progressBar.getVisibility() == View.VISIBLE || activityStackSize > 1) {
            // no-op
        } else {
            super.onBackPressed();
        }
    }

    private void dismissSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && imm.isAcceptingText()) {
            imm.hideSoftInputFromWindow(loginPassword.getWindowToken(), 0);
        }
    }
}
