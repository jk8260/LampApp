package com.elasalle.lamp.client;

import android.support.annotation.NonNull;

public interface NetworkRequestCallback<T> {
    void onSuccess(T results);
    void onFailure(@NonNull String message);
}
