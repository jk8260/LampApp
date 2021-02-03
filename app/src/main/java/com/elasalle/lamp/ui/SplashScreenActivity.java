package com.elasalle.lamp.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.elasalle.lamp.R;
import com.elasalle.lamp.ui.dashboard.menu.IntroductionActivity;
import com.elasalle.lamp.login.LoginActivity;
import com.elasalle.lamp.login.LoginManager;
import com.elasalle.lamp.util.PreferencesHelper;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }

    @Override
    protected void onResume() {
        super.onResume();
        final boolean isIntroductionDisplayed =  PreferencesHelper.getIntroductionDisplayed();
        if (isIntroductionDisplayed) {
            showActivity(LoginActivity.class);
        } else {
            showIntroduction();
        }
    }

    private void showIntroduction() {
        showActivity(IntroductionActivity.class);
    }

    private void showActivity(final Class<? extends Activity> activityClass) {
        final WeakReference<Activity> activityWeakReference = new WeakReference<Activity>(this);
        final long DELAY_MILLISECONDS = 500;
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if (activityClass.getCanonicalName().equals(IntroductionActivity.class.getCanonicalName())) {
                    startActivity(new Intent(activityWeakReference.get(), activityClass));
                } else if (activityClass.getCanonicalName().equals(LoginActivity.class.getCanonicalName())) {
                    LoginManager.showLoginScreen();
                }
                finish();
            }
        }, DELAY_MILLISECONDS);
    }
}
