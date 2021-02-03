package com.elasalle.lamp.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import com.elasalle.lamp.LampApp;
import com.elasalle.lamp.R;
import com.elasalle.lamp.analytics.AnalyticsActivity;
import com.elasalle.lamp.main.MainActivity;
import com.elasalle.lamp.model.guest.GuestLogin;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GuestLoginActivity extends AnalyticsActivity {

    @BindView(R.id.guest_email) EditText guestEmail;
    @BindView(R.id.guest_login_toolbar) Toolbar toolbar;

    @Inject GuestLoginManager guestLoginManager;

    @Override
    protected void setAnalyticsScreenName() {
        mTracker.setScreenName(getString(R.string.analytics_guest_login));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_login);
        ButterKnife.bind(this);
        LampApp.servicesComponent().inject(this);
        setupToolbar();
        setListeners();
    }

    private void setListeners() {
        guestEmail.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    loginAsGuest();
                    return true;
                }
                return false;
            }
        });
    }

    private void setupToolbar() {
        toolbar.setTitle(getString(R.string.guest_login_title));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void guestLogin(View view) {
        loginAsGuest();
    }

    private void loginAsGuest() {
        if(isEmail()) {
            guestLoginManager.guestLogin(new GuestLogin(guestEmail.getText().toString()));
            showMainScreen();
        } else {
            guestEmail.setError(getString(R.string.error_guest_login_email));
        }
    }

    private void showMainScreen() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private boolean isEmail() {
        return Patterns.EMAIL_ADDRESS.matcher(guestEmail.getText().toString()).matches();
    }

}
