package com.elasalle.lamp.search;

import android.content.Context;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Spinner;

import com.elasalle.lamp.LampApp;
import com.elasalle.lamp.R;
import com.elasalle.lamp.client.LampRestClient;
import com.elasalle.lamp.client.NetworkRequestCallback;
import com.elasalle.lamp.client.SearchQueryCallback;
import com.elasalle.lamp.login.LoginManager;
import com.elasalle.lamp.model.ErrorMessage;
import com.elasalle.lamp.model.asset.AssetDetails;
import com.elasalle.lamp.model.search.SearchData;
import com.elasalle.lamp.model.search.SearchFilter;
import com.elasalle.lamp.model.search.SearchResponse;
import com.elasalle.lamp.security.TokenManager;
import com.elasalle.lamp.util.GoogleAnalyticsHelper;

import java.net.HttpURLConnection;

import javax.inject.Inject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchManager implements SearchView.OnQueryTextListener {

    private static final String TAG = SearchManager.class.getSimpleName();

    private final LampRestClient lampRestClient;
    private final TokenManager tokenManager;
    private final AssetDetailManager assetDetailManager;

    private Runnable onQuerySubmitCallback;
    private Runnable onQueryTextChangeCallback;
    private Runnable onReauthenticateCallback;
    private SearchQueryCallback<SearchData> resultsCallback;
    private Spinner filterSpinner;
    private Location location;

    @Inject
    public SearchManager(LampRestClient lampRestClient, TokenManager tokenManager, AssetDetailManager assetDetailManager) {
        this.lampRestClient = lampRestClient;
        this.tokenManager = tokenManager;
        this.assetDetailManager = assetDetailManager;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        onQuerySubmitCallback.run();
        return !TextUtils.isEmpty(query) && query.length() >= 3;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (!TextUtils.isEmpty(newText) && newText.length() >= 3) {
            onQueryTextChangeCallback.run();
            String filter = (String) filterSpinner.getSelectedItem();
            filter = filter.substring(LampApp.getInstance().getString(R.string.search_filter_prefix).length(), filter.length()).trim();
            search(filter, newText.trim(), resultsCallback);
            return true;
        } else {
            return false;
        }
    }

    public void setCallbacks(@NonNull Runnable onQuerySubmitCallback, @NonNull Runnable onQueryTextChangeCallback, @NonNull SearchQueryCallback<SearchData> resultsCallback, @NonNull Runnable onReauthenticateCallback) {
        this.onQuerySubmitCallback = onQuerySubmitCallback;
        this.onQueryTextChangeCallback = onQueryTextChangeCallback;
        this.resultsCallback = resultsCallback;
        this.onReauthenticateCallback = onReauthenticateCallback;
        assetDetailManager.setOnReauthenticateCallback(onReauthenticateCallback);
    }

    public void setFilterSpinner(Spinner filterSpinner) {
        this.filterSpinner = filterSpinner;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void getAssetDetails(@NonNull final String actionsUrl, @NonNull final NetworkRequestCallback<AssetDetails> callback) {
        assetDetailManager.getAssetDetails(actionsUrl, callback);
    }

    private void search(final String filter, @NonNull final String query, @NonNull final SearchQueryCallback callback) {
        Call<SearchResponse> call = lampRestClient.search(
                tokenManager.getToken(),
                location != null ? location.getLatitude() + "," + location.getLongitude() : "",
                new SearchFilter(filter),
                query);
        call.enqueue(new Callback<SearchResponse>() {
            @Override
            public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {
                if (response.isSuccessful()) {
                    final SearchResponse searchResponse = response.body();
                    //noinspection unchecked
                    callback.onSuccess(new SearchData(searchResponse, filter));
                } else {
                    final String message = logError(response.errorBody(), response.message());
                    processError(response.code(), message, callback);
                }
            }

            @Override
            public void onFailure(Call<SearchResponse> call, Throwable t) {
                Log.e(TAG, t.getMessage());
                callback.onFailure(t.getMessage());
            }
        });
        Context context = LampApp.getInstance();
        GoogleAnalyticsHelper.sendAnalyticsEvent(
                context.getString(R.string.analytics_search),
                context.getString(R.string.analytics_action_search),
                query
        );
    }

    private String logError(ResponseBody responseBody, String msg) {
        final String message = new ErrorMessage(responseBody, msg).message;
        Log.e(TAG, message);
        return message;
    }

    private void processError(final int code, final String message, SearchQueryCallback callback) {
        if (code == HttpURLConnection.HTTP_UNAUTHORIZED) {
            reAuthenticate();
        } else if (callback != null) {
            callback.onFailure(message);
        }
    }

    private void reAuthenticate() {
        Log.i(TAG, "401: Re-authenticating user");
        LoginManager.reAuthenticate();
        onReauthenticateCallback.run();
    }
}