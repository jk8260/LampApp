package com.elasalle.lamp.ui.dashboard.menu;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.webkit.JavascriptInterface;

import com.elasalle.lamp.R;
import com.google.android.gms.common.GoogleApiAvailability;

import org.apache.commons.lang3.StringEscapeUtils;

public class ThirdPartyLicensesActivity extends WebviewActivity {

    private static final String THIRD_PARTY_LICENSES_HTML = "third_party_licenses.html";
    private static final String ASSET_PATH = "file:///android_asset/";

    @Override
    protected void onStart() {
        super.onStart();
        setTitle(getString(R.string.third_party_licenses));
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void loadUrl() {
        this.webView.getSettings().setJavaScriptEnabled(true);
        this.webView.addJavascriptInterface(new AppJSInterface(this), "Android");
        this.url = ASSET_PATH + THIRD_PARTY_LICENSES_HTML;
        super.loadUrl();
    }

    @Override
    protected void setAnalyticsScreenName() {
        mTracker.setScreenName(getString(R.string.analytics_third_party_libraries));
    }

    public static class AppJSInterface {
        final ThirdPartyLicensesActivity context;
        public AppJSInterface(ThirdPartyLicensesActivity context) {
            this.context = context;
        }
        @JavascriptInterface
        public void getGPSLicense() {
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String license = GoogleApiAvailability.getInstance().getOpenSourceSoftwareLicenseInfo(context);
                    if (TextUtils.isEmpty(license)) {
                        license = "Not available.";
                    }
                    context.webView.evaluateJavascript("var div = document.getElementById('gps_license');div.innerHTML = div.innerHTML + '" + StringEscapeUtils.escapeEcmaScript(license) + "';", null);
                }
            });
        }
    }
}
