package com.elasalle.lamp.client;

import android.support.annotation.NonNull;

public interface SyncCallback {
    void onSuccess();
    void onFailure(@NonNull String message);
    void onReauthenticate();
}
