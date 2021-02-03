package com.elasalle.lamp.client;

import android.support.annotation.NonNull;

public interface SearchQueryCallback <U> {
    void onSuccess(U results);
    void onFailure(@NonNull String message);
}
