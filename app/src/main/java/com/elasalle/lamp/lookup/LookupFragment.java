package com.elasalle.lamp.lookup;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.elasalle.lamp.BuildConfig;
import com.elasalle.lamp.LampApp;
import com.elasalle.lamp.R;
import com.elasalle.lamp.analytics.AnalyticsFragment;
import com.elasalle.lamp.camera.CameraActivity;
import com.elasalle.lamp.client.SearchQueryCallback;
import com.elasalle.lamp.main.DashboardEventListener;
import com.elasalle.lamp.model.lookup.LookupResponse;
import com.elasalle.lamp.ui.FooterFragment;
import com.elasalle.lamp.ui.dashboard.menu.HelpActivity;
import com.elasalle.lamp.util.CameraPermissionsHelper;
import com.elasalle.lamp.util.DisplayHelper;
import com.elasalle.lamp.util.GoogleAnalyticsHelper;
import com.elasalle.lamp.util.WebViewHelper;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class LookupFragment extends AnalyticsFragment {

    private static final String TAG = LookupFragment.class.getSimpleName();
    private static final int REQUEST_CODE_CAMERA = 102;
    private static final String CAMERA_SCAN_LOOKUP_TERM = "Serial #";

    private SearchView searchView;
    private DashboardEventListener mListener;
    private Unbinder unbinder;

    @BindView(R.id.lookup_webview) WebView webView;
    @BindView(R.id.lookup_search_content) View content;
    @BindView(R.id.lookup_image) ImageView imageView;
    @BindView(R.id.lookup_text) TextView textView;
    @BindView(R.id.search_open_camera) FloatingActionButton cameraFab;
    @BindView(R.id.search_toolbar) Toolbar toolbar;

    @Inject LookupManager lookupManager;
    @Inject ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lookup, container, false);
        unbinder = ButterKnife.bind(this, view);
        LampApp.searchComponent(getActivity()).inject(this);
        setupFooterFragment();
        setupFabButton();
        setupToolbar();
        setupWebview();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void setupFooterFragment() {
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.footer, new FooterFragment())
                .commit();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setupWebview() {
        WebView.setWebContentsDebuggingEnabled(BuildConfig.DEBUG);
        WebSettings webSettings = webView.getSettings();
        webSettings.setBuiltInZoomControls(true);
        webSettings.setSupportZoom(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAppCacheEnabled(true);
        WebViewHelper.setupWebViewErrorPage(webView, null);
        WebViewHelper.setupWebViewOnProgressChangeCallback(webView, new Runnable() {
            @Override
            public void run() {
                dismissProgressBar();
            }
        });
    }

    @Override
    protected void setAnalyticsScreenName() {
        mTracker.setScreenName(getString(R.string.analytics_lookup));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof DashboardEventListener) {
            mListener = (DashboardEventListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement DashboardEventListener");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CameraActivity.INTENT_CODE_CAMERA) {
            if (resultCode == Activity.RESULT_OK) {
                String scannedValue = data.getStringExtra(CameraActivity.INTENT_PARAM_BARCODE);
                if (!TextUtils.isEmpty(scannedValue) && !scannedValue.equals(getString(R.string.scanning))) {
                    searchView.setQuery(scannedValue, true);
                }
                searchView.clearFocus();
            }
        }
    }

    private void setupToolbar() {
        toolbar.setNavigationIcon(R.drawable.action_home);
        toolbar.setTitle(getString(R.string.dashboard_lookup_title));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissSoftKeyboard();
                mListener.onNavigationIconClick();
            }
        });
        toolbar.inflateMenu(R.menu.toolbar_search_menu);
        searchView = (SearchView) toolbar.getMenu().findItem(R.id.action_search).getActionView();
        searchView.setMaxWidth(DisplayHelper.getWiderScreenWidth());
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String serialNumber) {
                dismissSoftKeyboard();
                if (!TextUtils.isEmpty(serialNumber)) {
                    showProgressBar();
                    lookupManager.lookup(serialNumber.trim(), new SearchQueryCallback<LookupResponse>() {
                        @Override
                        public void onSuccess(LookupResponse results) {
                            if (results != null) {
                                webView.loadUrl(results.url);
                                showDetails();
                            }
                        }

                        @Override
                        public void onFailure(@NonNull String message) {
                            Log.e(TAG, message);
                            showNoResults();
                            dismissProgressBar();
                        }
                    });
                    GoogleAnalyticsHelper.sendAnalyticsEvent(
                            getString(R.string.analytics_lookup),
                            getString(R.string.analytics_action_lookup_search),
                            serialNumber);
                    return true;
                } else {
                    return false;
                }
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        searchView.setIconified(false);
        searchView.requestFocus();
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_help: {
                        Log.d(TAG, "scan help");
                        showHelp();
                        return true;
                    }
                    default: {
                        return false;
                    }
                }
            }
        });
    }

    private void dismissSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && imm.isAcceptingText()) {
            imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
        }
        searchView.clearFocus();
    }

    private void showDetails() {
        content.setVisibility(View.GONE);
        webView.setVisibility(View.VISIBLE);
    }

    private void showNoResults() {
        webView.setVisibility(View.GONE);
        content.setVisibility(View.VISIBLE);
        imageView.setImageResource(R.drawable.empty_none);
        textView.setText(getString(R.string.lookup_not_found));
    }

    private void showProgressBar() {
        progressDialog.show();
    }

    private void dismissProgressBar() {
        progressDialog.dismiss();
    }

    private void setupFabButton() {
        cameraFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CameraPermissionsHelper.isFeatureCameraAny(getActivity())) {
                    dismissSoftKeyboard();
                    if(CameraPermissionsHelper.isCameraPermissionGranted(LookupFragment.this, REQUEST_CODE_CAMERA)) {
                        CameraPermissionsHelper.startCameraActivity(LookupFragment.this, CAMERA_SCAN_LOOKUP_TERM);
                        sendCameraAnalytics();
                    }
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_CAMERA: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    CameraPermissionsHelper.startCameraActivity(this, CAMERA_SCAN_LOOKUP_TERM);
                    sendCameraAnalytics();
                }
            }
        }
    }

    private void sendCameraAnalytics() {
        GoogleAnalyticsHelper.sendAnalyticsEvent(
                getString(R.string.analytics_lookup),
                getString(R.string.analytics_action_open_camera)
        );
    }

    private void showHelp() {
        Intent intent = new Intent(getContext(), HelpActivity.class);
        intent.putExtra(HelpActivity.HELP_TYPE, HelpActivity.LOOKUP_HELP_INTENT);
        startActivity(intent);
    }
}