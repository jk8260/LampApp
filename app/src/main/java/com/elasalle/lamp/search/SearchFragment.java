package com.elasalle.lamp.search;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.elasalle.lamp.LampApp;
import com.elasalle.lamp.R;
import com.elasalle.lamp.analytics.AnalyticsFragment;
import com.elasalle.lamp.camera.CameraActivity;
import com.elasalle.lamp.client.NetworkRequestCallback;
import com.elasalle.lamp.client.SearchQueryCallback;
import com.elasalle.lamp.main.DashboardEventListener;
import com.elasalle.lamp.model.asset.AssetDetails;
import com.elasalle.lamp.model.search.SearchData;
import com.elasalle.lamp.model.search.SearchItem;
import com.elasalle.lamp.model.user.Customer;
import com.elasalle.lamp.recyclerview.SimpleDividerItemDecoration;
import com.elasalle.lamp.ui.FooterFragment;
import com.elasalle.lamp.ui.dashboard.menu.HelpActivity;
import com.elasalle.lamp.util.CameraPermissionsHelper;
import com.elasalle.lamp.util.DisplayHelper;
import com.elasalle.lamp.util.GoogleAnalyticsHelper;
import com.elasalle.lamp.util.MessageHelper;
import com.elasalle.lamp.util.ResourcesUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class SearchFragment extends AnalyticsFragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final String TAG = SearchFragment.class.getSimpleName();
    private static final String KEY_SEARCH_QUERY_COMPLETED = "isSearchQueryCompleted";
    private static final int REQUEST_CODE_CAMERA = 101;
    private static final int REQUEST_CODE_LOCATION = 201;
    private static final int REQUEST_CODE_LOCATION_SETTINGS = 301;
    private static final String CAMERA_SCAN_SEARCH_TERM = "Search";
    private DashboardEventListener mListener;
    private SearchView searchView;
    private GoogleApiClient mGoogleApiClient;

    @BindView(R.id.search_open_camera) FloatingActionButton cameraFab;
    @BindView(R.id.search_toolbar) Toolbar toolbar;
    @BindView(R.id.search_filter_spinner) Spinner searchFiltersDropdown;
    @BindView(R.id.search_result_list) RecyclerView searchResultList;
    @BindView(R.id.search_background) View searchBackground;
    @BindView(R.id.search_background_image) ImageView searchBackgroundImage;
    @BindView(R.id.search_filters) Toolbar searchFilterBar;
    @BindView(R.id.search_result_footer_text) TextView searchResultFooterText;

    @Inject SearchAdapter adapter;
    @Inject SearchManager searchManager;
    @Inject ProgressDialog progressDialog;

    private volatile boolean isSearchQueryCompleted;
    private Unbinder unbinder;

    @Override
    protected void setAnalyticsScreenName() {
        mTracker.setScreenName(getString(R.string.analytics_search));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setGoogleApiClient();
        requestLocationPermission();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        unbinder = ButterKnife.bind(this, view);
        LampApp.searchComponent(getActivity()).inject(this);
        setupFooterFragment();
        setupFabButton();
        setupToolbar();
        setupFilterBar();
        setupDropdown();
        setupRecyclerView();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            isSearchQueryCompleted = savedInstanceState.getBoolean(KEY_SEARCH_QUERY_COMPLETED);
        }
    }

    private void setupFooterFragment() {
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.footer, new FooterFragment())
                .commit();
    }

    @Override
    public void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isSearchQueryCompleted) {
            dismissProgressBar();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mGoogleApiClient != null) {
            stopLocationUpdates();
            mGoogleApiClient.unregisterConnectionCallbacks(this);
            mGoogleApiClient.unregisterConnectionFailedListener(this);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(KEY_SEARCH_QUERY_COMPLETED, isSearchQueryCompleted);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
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
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
        } else if (requestCode == REQUEST_CODE_LOCATION_SETTINGS) {
            if (resultCode == Activity.RESULT_OK) {
                startLocationUpdates();
            }
        }
    }

    private void setupFilterBar() {
        Drawable icon = ResourcesUtil.getDrawable(R.drawable.list_search_filter, null);
        icon.setColorFilter(ResourcesUtil.getColor(R.color.primary, null), PorterDuff.Mode.SRC_ATOP);
        searchFilterBar.setNavigationIcon(icon);
    }

    private void setupFabButton() {
        cameraFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CameraPermissionsHelper.isFeatureCameraAny(getActivity())) {
                    dismissSoftKeyboard();
                    if(CameraPermissionsHelper.isCameraPermissionGranted(SearchFragment.this, REQUEST_CODE_CAMERA)) {
                        CameraPermissionsHelper.startCameraActivity(SearchFragment.this, CAMERA_SCAN_SEARCH_TERM);
                        sendCameraAnalytics();
                    }
                }
            }
        });
    }

    private void setupToolbar() {
        toolbar.setNavigationIcon(R.drawable.action_home);
        toolbar.setTitle(getString(R.string.dashboard_search_title));
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
        searchManager.setCallbacks(getOnQuerySubmitCallback(), getOnQueryTextChangeCallback(), getQueryResultsCallback(), getOnReauthenticateCallback());
        searchManager.setFilterSpinner(searchFiltersDropdown);
        searchView.setOnQueryTextListener(searchManager);
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

    private void setupDropdown() {
        Customer customer = LampApp.getSessionManager().getCustomerDetails();
        Context context = getActivity();
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(context, R.layout.spinner_selected, customer.getSearchLabels(context));
        adapter.setDropDownViewResource(R.layout.search_filters_dropdown);
        searchFiltersDropdown.setAdapter(adapter);
        searchFiltersDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                searchManager.onQueryTextChange(searchView.getQuery().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ResourcesUtil.setSpinnerBackgroundTint(searchFiltersDropdown);
    }

    private void setupRecyclerView() {
        final Context context = LampApp.getInstance();
        final RecyclerView.LayoutManager layoutManager;
        if (DisplayHelper.isTablet()) {
            final int spanCount = DisplayHelper.isPortraitMode() ? 2 : 3;
            final int spanSizeForContent = 1;
            layoutManager = new GridLayoutManager(context, spanCount);
            ((GridLayoutManager)layoutManager).setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    if (adapter.getItem(position).isHeader()) {
                        return spanCount;
                    } else {
                        return spanSizeForContent;
                    }
                }
            });
        } else {
            layoutManager = new LinearLayoutManager(context);
            searchResultList.addItemDecoration(new SimpleDividerItemDecoration());
        }
        searchResultList.setLayoutManager(layoutManager);
        adapter.setOnItemClickListener(new SearchAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                SearchItem item = adapter.getItem(position);
                String actionsUrl = item.getData().target;
                showProgressBar();
                searchManager.getAssetDetails(actionsUrl, new NetworkRequestCallback<AssetDetails>() {
                    @Override
                    public void onSuccess(AssetDetails results) {
                        if(isAdded()) {
                            showAssetDetailsScreen(results);
                            dismissProgressBar();
                        }
                    }
                    @Override
                    public void onFailure(@NonNull String message) {
                        Log.e(TAG, "Error getting search details. " + message);
                        if(isAdded()) {
                            displayMessage(getString(R.string.error_search_details));
                            dismissProgressBar();
                        }
                    }
                });
                GoogleAnalyticsHelper.sendAnalyticsEvent(
                        getString(R.string.analytics_search),
                        getString(R.string.analytics_action_search_select_result),
                        item.getData().id
                );
            }
        });
        searchResultList.setAdapter(adapter);
    }

    private void showAssetDetailsScreen(AssetDetails assetDetails) {
        Intent intent = new Intent(getActivity(), AssetDetailActivity.class);
        intent.putExtra(AssetDetailActivity.KEY_URL, assetDetails.getUrl());
        intent.putExtra(AssetDetailActivity.KEY_TITLE, assetDetails.getTitle());
        intent.putExtra(AssetDetailActivity.KEY_ACTIONS, assetDetails.getActions().toArray());
        startActivity(intent);
    }

    @NonNull
    private Runnable getOnQueryTextChangeCallback() {
        return new Runnable() {
            @Override
            public void run() {
                isSearchQueryCompleted = false;
                searchView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!isSearchQueryCompleted) {
                            showProgressBar();
                        }
                    }
                }, 500);
            }
        };
    }

    @NonNull
    private SearchQueryCallback<SearchData> getQueryResultsCallback() {
        return new SearchQueryCallback<SearchData>() {
            @Override
            public void onSuccess(SearchData results) {
                isSearchQueryCompleted = true;
                if (isAdded()) {
                    adapter.setData(results.getResults());
                    adapter.notifyDataSetChanged();
                    if (adapter.getItemCount() > 0) {
                        showResults(results.getFooterText());
                    } else {
                        showNoResults();
                    }
                    dismissProgressBar();
                }
            }

            @Override
            public void onFailure(@NonNull String message) {
                isSearchQueryCompleted = true;
                Log.e(TAG, message);
                if (isAdded()) {
                    showNoResults();
                    dismissProgressBar();
                }
            }
        };
    }

    @NonNull
    private Runnable getOnQuerySubmitCallback() {
        return new Runnable() {
            @Override
            public void run() {
                dismissSoftKeyboard();
            }
        };
    }

    @NonNull
    private Runnable getOnReauthenticateCallback() {
        return new Runnable() {
            @Override
            public void run() {
                isSearchQueryCompleted = true;
                showNoResults();
                dismissProgressBar();
            }
        };
    }

    private void showNoResults() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                searchResultFooterText.setVisibility(View.GONE);
                searchResultList.setVisibility(View.GONE);
                ImageView imageView = (ImageView) getActivity().findViewById(R.id.search_background_image);
                if (imageView != null) {
                    imageView.setImageDrawable(ResourcesUtil.getDrawable(R.drawable.empty_none, null));
                    imageView.setColorFilter(ResourcesUtil.getColor(R.color.grey3, null));
                }
                TextView textView = (TextView) getActivity().findViewById(R.id.search_background_text);
                if (textView != null) {
                    textView.setText(getString(R.string.search_no_results));
                }
                searchBackground.setVisibility(View.VISIBLE);
            }
        });
    }

    private void showResults(final String footerText) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!TextUtils.isEmpty(footerText)) {
                    searchResultFooterText.setText(footerText);
                    searchResultFooterText.setVisibility(View.VISIBLE);
                }
                searchResultList.setVisibility(View.VISIBLE);
                searchBackground.setVisibility(View.GONE);
            }
        });
    }

    private void showProgressBar() {
        progressDialog.show();
    }

    private void dismissProgressBar() {
        progressDialog.dismiss();
    }

    private void dismissSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && imm.isAcceptingText()) {
            imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
        }
        searchView.clearFocus();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_CAMERA: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    CameraPermissionsHelper.startCameraActivity(this, CAMERA_SCAN_SEARCH_TERM);
                    sendCameraAnalytics();
                }
            }
            case REQUEST_CODE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkLocationSettings();
                }
            }
        }
    }

    private boolean isLocationPermissionGranted() {
        int permission = ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION);
        return permission == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermission() {
        Log.i(TAG, "Requesting permission for location.");
        requestPermissions(
                new String[] {
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                },
                REQUEST_CODE_LOCATION);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if(isLocationPermissionGranted()) {
            checkLocationSettings();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        this.searchManager.setLocation(location);
    }

    @SuppressWarnings("MissingPermission")
    private void startLocationUpdates() {
        if (mGoogleApiClient.isConnected() && isLocationPermissionGranted()) {
            searchManager.setLocation(LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient));
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, getLocationRequest(), this);
        }
    }

    private LocationRequest getLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        return locationRequest;
    }

    private void stopLocationUpdates() {
        if (mGoogleApiClient.isConnected() && isLocationPermissionGranted()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    private void setGoogleApiClient() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(LampApp.getInstance().getApplicationContext())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    private boolean checkLocationSettings() {
        final LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(getLocationRequest());
        final PendingResult<LocationSettingsResult> pendingResult = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        pendingResult.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
                final Status status = locationSettingsResult.getStatus();
                final int statusCode = status.getStatusCode();
                switch (statusCode) {
                    case LocationSettingsStatusCodes.SUCCESS: {
                        startLocationUpdates();
                        break;
                    }
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED: {
                        try {
                            status.startResolutionForResult(getActivity(), REQUEST_CODE_LOCATION_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            Log.w(TAG, e);
                        }
                        break;
                    }
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE: {
                        // no-op
                        break;
                    }
                }
            }
        });
        return false;
    }

    private void displayMessage(final String message) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MessageHelper.displayMessage(getActivity(), message, null);
            }
        });
    }

    private void sendCameraAnalytics() {
        GoogleAnalyticsHelper.sendAnalyticsEvent(
                getString(R.string.analytics_search),
                getString(R.string.analytics_action_open_camera)
        );
    }

    private void showHelp() {
        Intent intent = new Intent(getContext(), HelpActivity.class);
        intent.putExtra(HelpActivity.HELP_TYPE, HelpActivity.SEARCH_HELP_INTENT);
        startActivity(intent);
    }
}