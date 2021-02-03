package com.elasalle.lamp.ui.dashboard.menu;

import android.util.Log;

import com.elasalle.lamp.R;
import com.elasalle.lamp.service.AboutTask;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class AboutActivity extends WebviewActivity {

    @Override
    protected void onStart() {
        super.onStart();
        setTitle(getString(R.string.about));
    }

    @Override
    protected void loadUrl() {
        checkForAboutFile();
        this.url = "file://" + getFilesDir().getAbsolutePath() + File.separator + AboutTask.ABOUT_HTML;
        super.loadUrl();
    }

    private void checkForAboutFile() {
        final File file = new File(getFilesDir().getAbsolutePath(), AboutTask.ABOUT_HTML);
        if (!file.exists()) {
            try {
                FileUtils.copyInputStreamToFile(getAssets().open(AboutTask.ABOUT_HTML), file);
            } catch (IOException e) {
                Log.e(getClass().getSimpleName(), e.getMessage());
            }
        }
    }

    @Override
    protected void setAnalyticsScreenName() {
        mTracker.setScreenName(getString(R.string.analytics_about));
    }
}
