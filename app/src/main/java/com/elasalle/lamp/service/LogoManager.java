package com.elasalle.lamp.service;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ImageView;

import com.elasalle.lamp.LampApp;
import com.elasalle.lamp.client.ApiRequestCallback;
import com.elasalle.lamp.model.ErrorMessage;
import com.elasalle.lamp.model.user.Customer;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LogoManager {

    private static final String TAG = LogoManager.class.getSimpleName();

    private final OkHttpClient client;

    @Inject
    public LogoManager(OkHttpClient client) {
        this.client = client;
    }

    public void downloadCompanyLogo(@NonNull final Customer customer, @NonNull final ImageView imageView, @NonNull final ApiRequestCallback callback) {
        if (isLogoCached(customer.getCustomerId())) {
            if (setLogoFromCache(customer.getCustomerId(), imageView)) {
                callback.onSuccess();
            } else {
                download(customer, imageView, callback);
            }
        } else {
            download(customer, imageView, callback);
        }
    }

    private void download(@NonNull final Customer customer, @NonNull final ImageView imageView, @NonNull final ApiRequestCallback callback) {
        client.newCall(new Request.Builder().url(customer.getLogoUrl()).build()).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, e.getMessage());
                callback.onFailure(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final byte[] image = response.body().bytes();
                    cacheLogo(customer.getCustomerId(), image);
                    setImageLogo(image, imageView);
                    callback.onSuccess();
                } else {
                    final String message = new ErrorMessage(response.body(), response.message()).message;
                    Log.e(TAG, message);
                    callback.onFailure(message);
                }
            }
        });
    }

    private void setImageLogo(byte[] image, @NonNull final ImageView imageView) {
        final Bitmap logo = BitmapFactory.decodeByteArray(image, 0, image.length);
        imageView.post(new Runnable() {
            @Override
            public void run() {
                imageView.setImageBitmap(logo);
                imageView.setColorFilter(null);
            }
        });
    }

    private boolean setLogoFromCache(final String customerId, final ImageView imageView) {
        boolean isLogoSet = false;
        try {
            byte[] logo = FileUtils.readFileToByteArray(getLogoFile(customerId));
            setImageLogo(logo, imageView);
            isLogoSet = true;
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
        return isLogoSet;
    }

    private boolean isLogoCached(final String customerId) {
        File logo = getLogoFile(customerId);
        return logo.exists();
    }

    private void cacheLogo(final String customerId, final byte[] logo) {
        try {
            FileUtils.writeByteArrayToFile(getLogoFile(customerId), logo);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    @NonNull
    private File getLogoFile(String customerId) {
        return new File(LampApp.getInstance().getCacheDir(), customerId + "-logo");
    }

}
