package com.elasalle.lamp.ui.dashboard.menu;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.elasalle.lamp.BuildConfig;
import com.elasalle.lamp.R;
import com.elasalle.lamp.analytics.AnalyticsActivity;
import com.elasalle.lamp.util.WebViewHelper;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public abstract class WebviewActivity extends AnalyticsActivity {

    protected String url = "";

    @BindView(R.id.webView)
    WebView webView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            WebView.enableSlowWholeDocumentDraw();
        }
        setContentView(R.layout.activity_webview);
        ButterKnife.bind(this);
        setupWebviewSettings();
        setupClient();
        loadUrl();
        if (BuildConfig.DEBUG) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
    }

    @CallSuper
    protected void loadUrl() {
        if (!TextUtils.isEmpty(url)) {
            webView.loadUrl(url);
        }
    }

    protected void loadUrlWithHeaders(Map<String, String> headers) {
        webView.loadUrl(url, headers);
    }

    private void setupWebviewSettings() {
        WebSettings webSettings = webView.getSettings();
        webSettings.setBuiltInZoomControls(true);
        webSettings.setSupportZoom(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);
    }

    private void setupClient() {
        WebViewHelper.setupWebViewErrorPage(webView, url);
    }

    protected WebView getWebView() {
        return webView;
    }
}
