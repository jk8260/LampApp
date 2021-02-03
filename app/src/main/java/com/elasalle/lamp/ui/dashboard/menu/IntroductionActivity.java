package com.elasalle.lamp.ui.dashboard.menu;

import android.os.Bundle;
import android.view.View;

import com.elasalle.lamp.R;
import com.elasalle.lamp.analytics.AnalyticsActivity;
import com.elasalle.lamp.login.LoginManager;
import com.elasalle.lamp.util.PreferencesHelper;

public class IntroductionActivity extends AnalyticsActivity {

    public static final String INTENT_EXTRAS_FORCE_DISPLAY = "Force Display";
    private boolean isForceDisplay = false;

    @Override
    protected void setAnalyticsScreenName() {
        mTracker.setScreenName(getString(R.string.analytics_introduction));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            isForceDisplay = bundle.getBoolean(INTENT_EXTRAS_FORCE_DISPLAY);
        }
        if (isForceDisplay || !PreferencesHelper.getIntroductionDisplayed()) {
            setContentView(R.layout.activity_introduction);
        } else {
            showLoginScreen();
        }
    }

    public void dismissIntro(View view) {
        PreferencesHelper.saveIntroductionDisplayed();
        if (!isForceDisplay) {
            showLoginScreen();
        } else {
            finish();
        }
    }

    private void showLoginScreen() {
        LoginManager.showLoginScreen();
    }
}
