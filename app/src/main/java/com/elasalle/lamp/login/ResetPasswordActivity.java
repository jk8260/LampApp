package com.elasalle.lamp.login;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import com.elasalle.lamp.LampApp;
import com.elasalle.lamp.R;
import com.elasalle.lamp.analytics.AnalyticsActivity;
import com.elasalle.lamp.client.ApiRequestCallback;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ResetPasswordActivity extends AnalyticsActivity {

    @BindView(R.id.username) EditText username;
    @BindView(R.id.reset_password_toolbar) Toolbar toolbar;
    @BindView(R.id.reset_password_button) View submitButton;
    @BindView(R.id.progressBar) View progressBar;

    @Inject ResetPasswordManager resetPasswordManager;

    @Override
    protected void setAnalyticsScreenName() {
        mTracker.setScreenName(getString(R.string.analytics_forgot_password));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        ButterKnife.bind(this);
        LampApp.servicesComponent().inject(this);
        setupToolbar();
        setListeners();
    }

    public void resetPassword(View view) {
        final String name = username.getText().toString();
        if(!TextUtils.isEmpty(name)) {
            showProgressBar();
            resetPasswordManager.reset(name.trim(), new ApiRequestCallback() {
                @Override
                public void onSuccess() {
                    dismissProgressBar();
                    displayMessage(getString(R.string.forgot_password_success),
                            new Runnable() {
                                @Override
                                public void run() {
                                    finish();
                                }
                            });
                }

                @Override
                public void onFailure(@NonNull String message) {
                    dismissProgressBar();
                    displayMessage(message, null);
                    setInputViewsEnabled(true);
                }
            });
        } else {
            username.setError(getString(R.string.forgot_password_error));
        }
    }

    private void setupToolbar() {
        toolbar.setTitle(getString(R.string.forgot_password_title));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void displayMessage(final String message, @Nullable final Runnable action) {
        AlertDialog alertDialog = new AlertDialog
                .Builder(ResetPasswordActivity.this, R.style.AppTheme_Dialog_Alert)
                .setMessage(message)
                .setPositiveButton(getString(R.string.alert_button_text_dismiss), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if(action != null) {
                            action.run();
                        }
                    }
                })
                .setCancelable(false)
                .create();
        alertDialog.show();
    }

    private void setListeners() {
        username.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    resetPassword(null);
                    return true;
                }
                return false;
            }
        });
    }

    private void showProgressBar() {
        setInputViewsEnabled(false);
        this.progressBar.setVisibility(View.VISIBLE);
    }

    private void dismissProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    private void setInputViewsEnabled(final boolean enabled) {
        username.setEnabled(enabled);
        submitButton.setEnabled(enabled);
        getSupportActionBar().setDisplayHomeAsUpEnabled(enabled);
    }
}
