package com.elasalle.lamp.ui.dashboard.menu;


import android.util.Log;

import com.elasalle.lamp.service.LookupHelpTask;
import com.elasalle.lamp.service.NotificationsHelpTask;
import com.elasalle.lamp.service.ScanHelpTask;
import com.elasalle.lamp.service.SearchHelpTask;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created by abrun on 6/9/16.
 */

public class HelpActivity extends WebviewActivity {

    public static final String HELP_TYPE = "lamp.help";
    public static final String SEARCH_HELP_INTENT = "lamp.help.search";
    public static final String LOOKUP_HELP_INTENT = "lamp.help.lookup";
    public static final String SCAN_HELP_INTENT = "lamp.help.scan";
    public static final String NOTIFICATIONS_HELP_INTENT = "lamp.help.notifications";

    @Override
    protected void onStart() {
        super.onStart();
        setTitle("");
    }

    @Override
    protected void loadUrl() {
        switch (getIntent().getStringExtra(HELP_TYPE)) {
            case SEARCH_HELP_INTENT:
                checkForFile(SearchHelpTask.SEARCH_HTML);
                this.url = "file://" + getFilesDir().getAbsolutePath() + File.separator + SearchHelpTask.SEARCH_HTML;
                break;
            case LOOKUP_HELP_INTENT:
                checkForFile(LookupHelpTask.LOOKUP_HTML);
                this.url = "file://" + getFilesDir().getAbsolutePath() + File.separator + LookupHelpTask.LOOKUP_HTML;
                break;
            case SCAN_HELP_INTENT:
                checkForFile(ScanHelpTask.SCAN_HTML);
                this.url = "file://" + getFilesDir().getAbsolutePath() + File.separator + ScanHelpTask.SCAN_HTML;
                break;
            case NOTIFICATIONS_HELP_INTENT:
                checkForFile(NotificationsHelpTask.NOTIFICATIONS_HTML);
                this.url = "file://" + getFilesDir().getAbsolutePath() + File.separator + NotificationsHelpTask.NOTIFICATIONS_HTML;
                break;
        }
        super.loadUrl();
    }

    private void checkForFile(String name) {
        final File file = new File(getFilesDir().getAbsolutePath(), name);
        if (!file.exists()) {
            try {
                FileUtils.copyInputStreamToFile(getAssets().open(name), file);
            } catch (IOException e) {
                Log.e(getClass().getSimpleName(), e.getMessage());
            }
        }
    }

    @Override
    protected void setAnalyticsScreenName() {
        mTracker.setScreenName("Help");
    }
}
