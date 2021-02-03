package com.elasalle.lamp.analytics;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.elasalle.lamp.LampApp;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public abstract class AnalyticsActivity extends AppCompatActivity {

    protected Tracker mTracker;

    abstract protected void setAnalyticsScreenName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTracker = LampApp.getInstance().getDefaultTracker();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setAnalyticsScreenName();
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
}
