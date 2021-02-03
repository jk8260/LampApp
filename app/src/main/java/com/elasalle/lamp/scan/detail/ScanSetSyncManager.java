package com.elasalle.lamp.scan.detail;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.elasalle.lamp.client.LampRestClient;
import com.elasalle.lamp.client.SyncCallback;
import com.elasalle.lamp.data.repository.NewScanSetRepository;
import com.elasalle.lamp.data.repository.ScanSetRepository;
import com.elasalle.lamp.login.LoginManager;
import com.elasalle.lamp.model.ErrorMessage;
import com.elasalle.lamp.model.scan.Asset;
import com.elasalle.lamp.model.scan.ScanSet;
import com.elasalle.lamp.model.scan.ScanSetDataSync;
import com.elasalle.lamp.model.scan.ScanSetFieldSync;
import com.elasalle.lamp.model.scan.ScanSetSync;
import com.elasalle.lamp.model.user.Field;
import com.elasalle.lamp.scan.model.NewScanSet;
import com.elasalle.lamp.security.TokenManager;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScanSetSyncManager {

    private static final String TAG = ScanSetSyncManager.class.getSimpleName();

    private final NewScanSetRepository newScanSetRepository;
    private final ScanSetRepository scanSetRepository;
    private final LampRestClient client;
    private final TokenManager tokenManager;

    @Inject
    public ScanSetSyncManager(NewScanSetRepository newScanSetRepository, ScanSetRepository scanSetRepository, LampRestClient client, TokenManager tokenManager) {
        this.newScanSetRepository = newScanSetRepository;
        this.scanSetRepository = scanSetRepository;
        this.client = client;
        this.tokenManager = tokenManager;
    }

    public ScanSet getLatestScanSet(@NonNull String id) {
        return scanSetRepository.findById(id);
    }

    public void sync(@NonNull final String customerId, @NonNull SyncCallback syncCallback) {
        final List<ScanSet> scanSetList = scanSetRepository.findAllByCustomerId(customerId);
        if (scanSetList != null && scanSetList.size() > 0) {
            syncScanSetListRecursively(scanSetList, 0, syncCallback);
        }
    }

    private void syncScanSetListRecursively(final List<ScanSet> scanSetList, final int position, SyncCallback syncCallback) {
        if (position < scanSetList.size()) {
            final ScanSetSync scanSetSync = mapScanSetToSyncModel(scanSetList, position);
            executeNetworkCallForScanSetSync(scanSetSync, scanSetList, position, syncCallback);
        } else {
            if (syncCallback != null) {
                syncCallback.onSuccess();
            }
        }
    }

    @NonNull
    private ScanSetSync mapScanSetToSyncModel(List<ScanSet> scanSetList, int position) {
        final ScanSet scanSet = scanSetList.get(position);
        final ScanSetSync scanSetSync = new ScanSetSync();
        scanSetSync.id = scanSet.getId();
        scanSetSync.name = scanSet.getName();
        final ISO8601DateFormat format = new ISO8601DateFormat();
        scanSetSync.updateDate = format.format(scanSet.getModifiedDate());
        scanSetSync.scanData = new ArrayList<>();
        mapScanSetAssetsToSyncModel(scanSet, scanSetSync);
        return scanSetSync;
    }

    private void mapScanSetAssetsToSyncModel(ScanSet scanSet, ScanSetSync scanSetSync) {
        for (final Asset asset : scanSet.getAssets()) {
            ScanSetDataSync data = new ScanSetDataSync();
            data.id = asset.getId();
            data.fields = new ArrayList<>();
            for (Map.Entry<String, String> entry : asset.getFieldIdValueMap().entrySet()) {
                final String key = entry.getKey();
                if (key.contains(Field.LOOKUP_LIST_FIELD_ID_DISPLAY_SUFFIX)) {
                    continue;
                }
                ScanSetFieldSync field = new ScanSetFieldSync();
                field.id = key;
                field.value = entry.getValue();
                data.fields.add(field);
            }
            scanSetSync.scanData.add(data);
        }
    }

    private void executeNetworkCallForScanSetSync(final ScanSetSync scanSetSync, final List<ScanSet> scanSetList, final int position, final SyncCallback syncCallback) {
        Call<ScanSetSync> call = client.syncScanSet(tokenManager.getToken(), scanSetSync);
        call.enqueue(new Callback<ScanSetSync>() {
            @Override
            public void onResponse(Call<ScanSetSync> call, Response<ScanSetSync> response) {
                if (response.isSuccessful()) {
                    processResponse(response, scanSetList, position);
                    syncScanSetListRecursively(scanSetList, position + 1, syncCallback);
                } else {
                    final String message = new ErrorMessage(response.errorBody(), response.message()).message;
                    Log.e(TAG, message);
                    if (response.code() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                        if (syncCallback != null) {
                            syncCallback.onReauthenticate();
                        }
                        LoginManager.reAuthenticate();
                    } else {
                        if (syncCallback != null) {
                            syncCallback.onFailure(message);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ScanSetSync> call, Throwable t) {
                Log.e(TAG, t.getMessage(), t);
            }
        });
    }

    private void processResponse(final Response<ScanSetSync> response, final List<ScanSet> scanSetList, final int position) {
        final ScanSetSync scanSetSync = response.body();
        final ScanSet scanSet = scanSetList.get(position);
        if (scanSet.getId().equals(scanSetSync.id)) {
            scanSet.setName(scanSetSync.name);
            scanSet.setSyncDate(new Date());
            if (TextUtils.isEmpty(scanSetSync.updateDate)) {
                scanSet.setModifiedDate(new Date());
            } else {
                try {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                    simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                    scanSet.setModifiedDate(simpleDateFormat.parse(scanSetSync.updateDate));
                } catch (Exception e) {
                    scanSet.setModifiedDate(new Date());
                    Log.e(TAG, e.getMessage(), e);
                }
            }
            for (final ScanSetDataSync dataSync : scanSetSync.scanData) {
                for (final Asset asset : scanSet.getAssets()) {
                    if (asset.getId().equals(dataSync.id)) {
                        for (final ScanSetFieldSync fieldSync : dataSync.fields) {
                            final Map<String, String> assetMap = asset.getFieldIdValueMap();
                            assetMap.put(fieldSync.id, fieldSync.value);
                        }
                    }
                }
            }
            saveSyncedScanSet(scanSet);
        } else {
            Log.w(TAG, String.format("Response for sync set did not match expected id! Expected id %s but received %s instead. Scan set not synced.", scanSet.getId(), scanSetSync.id));
        }
    }

    private void saveSyncedScanSet(ScanSet scanSet) {
        scanSetRepository.save(scanSet);
        NewScanSet newScanSet = newScanSetRepository.findById(scanSet.getId());
        newScanSet.setName(scanSet.getName());
        newScanSet.setAssets(scanSet.getAssets());
        newScanSetRepository.save(newScanSet);
    }
}
