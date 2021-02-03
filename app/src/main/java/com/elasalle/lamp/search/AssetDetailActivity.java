package com.elasalle.lamp.search;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.ValueCallback;

import com.elasalle.lamp.LampApp;
import com.elasalle.lamp.R;
import com.elasalle.lamp.client.NetworkRequestCallback;
import com.elasalle.lamp.main.MainActivity;
import com.elasalle.lamp.model.asset.Action;
import com.elasalle.lamp.model.asset.AssetDetails;
import com.elasalle.lamp.model.search.Datum;
import com.elasalle.lamp.security.TokenManager;
import com.elasalle.lamp.ui.dashboard.menu.WebviewActivity;
import com.elasalle.lamp.util.MessageHelper;
import com.elasalle.lamp.util.PdfHelper;
import com.elasalle.lamp.util.WebViewHelper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

public class AssetDetailActivity extends WebviewActivity {

    private static final String TAG = AssetDetailActivity.class.getSimpleName();

    private static final String ACTION_SHARE = "share";
    private static final String ACTION_OPEN_LIST = "openList";
    private static final String ACTION_OPEN_DETAIL = "openDetail";
    private static final String ACTION_SHOW_MAP = "showMap";
    private static final String ACTION_CALL_NUMBER = "callNumber";
    private static final String ACTION_OPEN_URL = "openURL";
    private static final String ACTION_HOME = "goHome";
    private static final String ACTION_SMS = "shareText";

    public static final String KEY_URL = "url";
    public static final String KEY_ACTIONS = "actions";
    public static final String KEY_TITLE = "title";

    private Bundle bundle;
    private List<Action> actions;
    private Map<MenuItem, Action> menuActions;

    @Inject TokenManager tokenManager;
    @Inject ProgressDialog progressDialog;
    @Inject AssetDetailManager assetDetailManager;

    @Override
    protected void setAnalyticsScreenName() {
        mTracker.setScreenName(getString(R.string.analytics_asset_detail));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LampApp.searchComponent(this).inject(this);
        setTitleAndActionsFromBundle();
        setupActionBar();
        setupJavaScriptInterface();
        setupWebviewProgressCallback();
        loadPage();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setupJavaScriptInterface() {
        getWebView().getSettings().setJavaScriptEnabled(true);
        getWebView().addJavascriptInterface(new AssetDetailJavaScriptInterface(this), "Android");
    }

    private void setTitleAndActionsFromBundle() {
        bundle = getIntent().getExtras();
        if (bundle != null) {
            setTitle(bundle.getString(KEY_TITLE));
            Object[] actionsArray = (Object[]) bundle.get(KEY_ACTIONS);
            actions = new ArrayList<>();
           if (actionsArray != null) {
               for (Object object : actionsArray) {
                   actions.add((Action) object);
               }
           }
        } else {
            setTitle(getString(R.string.asset_details_title));
        }
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void loadPage() {
        showProgressBar();
        url = bundle.getString(KEY_URL);
        Map<String, String> headers = new HashMap<>();
        headers.put("token", tokenManager.getToken());
        loadUrlWithHeaders(headers);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        } else {
            Action action = menuActions.get(item);
            performActionForType(action);

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (getWebView().canGoBack()) {
            getWebView().goBack();
        } else {
            super.onBackPressed();
        }
    }

    private void performActionForType(Action action) {
        final String actionType = action.getType();
        final String data = action.getData();
        switch (actionType) {
            case ACTION_SHARE: {
                sharePdf();
                break;
            }
            case ACTION_CALL_NUMBER: {
                callNumber(data);
                break;
            }
            case ACTION_OPEN_DETAIL: {
                openDetail(data);
                break;
            }
            case ACTION_OPEN_LIST: {
                openList(data);
                break;
            }
            case ACTION_OPEN_URL: {
                openUrl(data);
                break;
            }
            case ACTION_SHOW_MAP: {
                showMap(data);
                break;
            }
            case ACTION_HOME: {
                displayDashboard();
                break;
            }
            case ACTION_SMS: {
                sendSMS(data);
                break;
            }
            default: {
                Log.w(TAG, "Selected action not found! Selected: " + actionType);
                break;
            }
        }
    }

    private void sendSMS(String data) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.putExtra("sms_body", data);
        intent.setType("vnd.android-dir/mms-sms");
        startActivity(Intent.createChooser(intent, getString(R.string.action_share)));
    }

    private void displayDashboard() {
        LampApp application = LampApp.getInstance();
        Intent intent = new Intent(application.getApplicationContext(), MainActivity.class);
        Activity topActivity = application.getTopActivity();
        if (topActivity != null) {
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            topActivity.startActivity(intent);
        } else {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            application.startActivity(intent);
        }
    }

    private void showMap(String geoLocation) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.fromParts("geo", geoLocation, null));
        startActivity(Intent.createChooser(intent, getString(R.string.action_open)));
    }

    private void openUrl(final String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(Intent.createChooser(intent, getString(R.string.action_open)));
    }

    private void openList(final String url) {
        // TODO
    }

    private void openDetail(final String url) {
        showProgressBar();
        assetDetailManager.setOnReauthenticateCallback(new Runnable() {
            @Override
            public void run() {
                dismissProgressBar();
            }
        });
        assetDetailManager.getAssetDetails(url, new NetworkRequestCallback<AssetDetails>() {
            @Override
            public void onSuccess(AssetDetails results) {
                showDetail(results);
                dismissProgressBar();
            }

            @Override
            public void onFailure(@NonNull String message) {
                dismissProgressBar();
                displayMessage(message);
            }
        });
    }

    void showDetail(AssetDetails assetDetails) {
        Intent intent = new Intent(this, AssetDetailActivity.class);
        intent.putExtra(AssetDetailActivity.KEY_URL, assetDetails.getUrl());
        intent.putExtra(AssetDetailActivity.KEY_TITLE, assetDetails.getTitle());
        intent.putExtra(AssetDetailActivity.KEY_ACTIONS, assetDetails.getActions().toArray());
        startActivity(intent);
    }

    void refreshDetail() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loadPage();
            }
        });
    }

    private void callNumber(final String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null));
        startActivity(Intent.createChooser(intent, getString(R.string.action_call)));
    }

    private void sharePdf() {
        try {
            File file = PdfHelper.webviewToPdf(getWebView());
            Uri uri = Uri.fromFile(file);
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            intent.setType("application/pdf");
            startActivity(Intent.createChooser(intent, getString(R.string.action_share)));
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
            displayMessage(getString(R.string.error_share));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menuActions = new HashMap<>();
        for (Action action : actions) {
            if (isActionSupported(action)) {
                MenuItem menuItem = menu.add(action.getTitle());
                menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
                menuActions.put(menuItem, action);
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }

    private boolean isActionSupported(Action action) {
        final String actionType = action.getType();
        switch (actionType) {
            case ACTION_CALL_NUMBER: {
                return getPackageManager().hasSystemFeature(PackageManager.FEATURE_TELEPHONY);
            }
            case ACTION_SMS: {
                return getPackageManager().hasSystemFeature(PackageManager.FEATURE_TELEPHONY);
            }
            default: {
                return true;
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
                getWebView().evaluateJavascript(injectJavaScriptInterfaceCallbackWrapper(), new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) {
                        Log.d(TAG, "Javascript callback value = " + value);
                    }
                });
                dismissProgressBar();
            }
        });
    }

    /**
     *
     * Injects a JavaScript wrapper for the JavaScript callback. The wrapper matches the
     * iOS callback and keeps the JavaScript code platform-agnostic.
     *
     * @return String with JavaScript wrapper for JavaScript Interface
     */
    private String injectJavaScriptInterfaceCallbackWrapper() {
        return "javascript:(function(){" +
                    "window.webkit = {\"messageHandlers\":" +
                        "{\"LampMobileMessageHandler\":" +
                            "{\"postMessage\":" +
                                "function(data){" +
                                    "Android.onCallback(data);" +
                                "}" +
                            "}" +
                        "}" +
                    "};" +
                "})();";
    }

    private void displayMessage(String message) {
        MessageHelper.displayMessage(AssetDetailActivity.this, message, null);
    }

    public void showListScreen(final List<Datum> datumList) {
        Intent intent = new Intent(this, AssetListActivity.class);
        intent.putParcelableArrayListExtra(AssetListActivity.KEY_LIST, (ArrayList<? extends Parcelable>) datumList);
        startActivity(intent);
    }
}
