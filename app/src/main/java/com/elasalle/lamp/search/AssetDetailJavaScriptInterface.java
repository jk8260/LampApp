package com.elasalle.lamp.search;

import android.util.Log;
import android.webkit.JavascriptInterface;

import com.elasalle.lamp.model.asset.AssetDetails;
import com.elasalle.lamp.model.search.Datum;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class AssetDetailJavaScriptInterface {

    private static final String TAG = AssetDetailActivity.class.getSimpleName();

    private final WeakReference<AssetDetailActivity> assetDetailActivityWeakReference;

    public AssetDetailJavaScriptInterface(AssetDetailActivity assetDetailActivity) {
        this.assetDetailActivityWeakReference = new WeakReference<>(assetDetailActivity);
    }

    @JavascriptInterface
    public void onCallback(String data) {
        Log.d(TAG, "Received data: " + data);
        try {
            final JSONObject jsonObject = new JSONObject(data);
            final String type = (String) jsonObject.get("type");
            final ObjectMapper mapper = new ObjectMapper();
            if ("details".equalsIgnoreCase(type)) {
                AssetDetails assetDetails = mapper.readValue(jsonObject.getJSONObject("objects").toString(), AssetDetails.class);
                assetDetailActivityWeakReference.get().showDetail(assetDetails);
            } else if ("list".equalsIgnoreCase(type)) {
                final List<Datum> datumList = new ArrayList<>();
                final JSONArray jsonArray = jsonObject.getJSONArray("objects");
                for (int i = 0; i < jsonArray.length(); i++) {
                    final Datum datum = mapper.readValue(jsonArray.get(i).toString(), Datum.class);
                    datumList.add(datum);
                }
                assetDetailActivityWeakReference.get().showListScreen(datumList);
            } else if ("refresh".equalsIgnoreCase(type)) {
                assetDetailActivityWeakReference.get().refreshDetail();
            } else {
                Log.w(TAG, "Unexpected type! Type: " + type);
            }
        } catch (JSONException|IOException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }
}
