package com.elasalle.lamp.analytics;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.elasalle.lamp.LampApp;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public abstract class AnalyticsFragment extends Fragment {

    protected Tracker mTracker;

    abstract protected void setAnalyticsScreenName();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTracker = LampApp.getInstance().getDefaultTracker();
    }

    @Override
    public void onResume() {
        super.onResume();
        setAnalyticsScreenName();
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
}
