package com.elasalle.lamp.module;

import android.app.ProgressDialog;
import android.content.Context;

import com.elasalle.lamp.R;

import dagger.Module;
import dagger.Provides;

@Module
public class UIModule {

    private final Context context;

    public UIModule(Context context) {
        this.context = context;
    }

    @Provides
    ProgressDialog progressDialog() {
        ProgressDialog progressDialog = new ProgressDialog(context, R.style.ProgressDialog);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        return progressDialog;
    }

}
