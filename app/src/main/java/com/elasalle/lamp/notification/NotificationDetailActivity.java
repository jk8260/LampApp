package com.elasalle.lamp.notification;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.elasalle.lamp.LampApp;
import com.elasalle.lamp.R;
import com.elasalle.lamp.ui.dashboard.menu.WebviewActivity;
import com.elasalle.lamp.util.GoogleAnalyticsHelper;
import com.elasalle.lamp.util.WebViewHelper;

import javax.inject.Inject;

public class NotificationDetailActivity extends WebviewActivity {

    public static final String KEY_URL = "Target Url";
    public static final String KEY_NOTIFICATION_ID = "Notification Id";

    private String notificationId;

    @Inject ProgressDialog progressDialog;
    @Inject
    LampNotificationsManager lampNotificationsManager;

    @Override
    protected void setAnalyticsScreenName() {
        mTracker.setScreenName(getString(R.string.analytics_notifications_detail));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        this.url = getIntent().getStringExtra(KEY_URL);
        this.notificationId = getIntent().getStringExtra(KEY_NOTIFICATION_ID);
        LampApp.notificationComponent(this).inject(this);
        setupActionBar();
        super.onCreate(savedInstanceState);
        showProgressBar();
        setupWebviewProgressCallback();
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.action_back);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        setTitle(getString(R.string.notification_detail_title));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissProgressBar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.notification_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();
        switch (id) {
            case android.R.id.home: {
                onBackPressed();
                return true;
            }
            case R.id.action_delete_notification: {
                lampNotificationsManager.delete(notificationId);
                GoogleAnalyticsHelper.sendAnalyticsEvent(
                        getString(R.string.analytics_notifications),
                        getString(R.string.analytics_action_remove_notification),
                        notificationId
                );
                finish();
                return true;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }

        }
    }

    private void showProgressBar() {
        progressDialog.show();
    }

    private void dismissProgressBar() {
        progressDialog.dismiss();
    }

    private void setupWebviewProgressCallback() {
        WebViewHelper.setupWebViewOnProgressChangeCallback(getWebView(), new Runnable() {
            @Override
            public void run() {
                dismissProgressBar();
            }
        });
    }
}
