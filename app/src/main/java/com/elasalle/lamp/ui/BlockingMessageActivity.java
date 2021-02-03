package com.elasalle.lamp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.KeyEvent;
import android.widget.TextView;

import com.elasalle.lamp.R;
import com.elasalle.lamp.analytics.AnalyticsActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BlockingMessageActivity extends AnalyticsActivity {

    public static final String MESSAGE = "message";
    public static final String DISMISS = "dismiss";

    @BindView(R.id.blocking_message)
    TextView blockingMessage;

    @Override
    protected void setAnalyticsScreenName() {
        mTracker.setScreenName(getString(R.string.analytics_blocking_message));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blocking);
        ButterKnife.bind(this);
        checkIntent(getIntent());
    }

    private void setBlockingMessage(@NonNull Bundle bundle) {
        String message = bundle.getString(MESSAGE);
        blockingMessage.setText(message);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        checkIntent(intent);
    }

    private void checkIntent(Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            boolean isDismiss = bundle.getBoolean(DISMISS);
            if (isDismiss) {
                finish();
            } else {
                setBlockingMessage(bundle);
            }
        }
    }

    @Override
    public void onBackPressed() {
        Snackbar.make(findViewById(R.id.blockingCoordinatorLayout),getString(R.string.blocking_app_disabled), Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return keyCode == KeyEvent.KEYCODE_BACK || super.onKeyDown(keyCode, event);
    }
}
