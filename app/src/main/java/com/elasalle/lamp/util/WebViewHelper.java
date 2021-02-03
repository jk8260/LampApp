package com.elasalle.lamp.util;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebViewHelper {

    public static void setupWebViewErrorPage(@NonNull final WebView webView, @Nullable String url) {
        setupClient(webView, url);
    }

    public static void setupWebViewOnProgressChangeCallback(@NonNull final WebView webView, @NonNull final Runnable callback) {
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress == 100) {
                    callback.run();
                }
            }
        });
    }

    private static void setupClient(@NonNull final WebView webView, @Nullable final String url) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            webView.setWebViewClient(new WebViewClient(){
                @TargetApi(Build.VERSION_CODES.M)
                @Override
                public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                    super.onReceivedError(view, request, error);
                    if (url == null || url.equals(request.getUrl().toString())) {
                        showError(webView);
                    }
                }
            });
        } else {
            webView.setWebViewClient(new WebViewClient(){
                @SuppressWarnings("deprecation")
                @Override
                public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                    super.onReceivedError(view, errorCode, description, failingUrl);
                    if (url == null || url.equals(failingUrl)) {
                        showError(webView);
                    }
                }
            });
        }
    }

    private static void showError(@NonNull final WebView view) {
        view.loadData("<h1 style=\"text-align: center;\">There was an error loading the page.</h1>", "text/html", "utf-8");
    }

}
