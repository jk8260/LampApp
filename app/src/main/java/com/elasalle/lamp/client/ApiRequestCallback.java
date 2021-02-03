package com.elasalle.lamp.client;

import android.support.annotation.NonNull;

public interface ApiRequestCallback {
    void onSuccess();
    void onFailure(@NonNull String message);
}
