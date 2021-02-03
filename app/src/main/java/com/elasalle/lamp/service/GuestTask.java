package com.elasalle.lamp.service;

import android.support.annotation.Nullable;
import android.util.Log;

import com.elasalle.lamp.client.LampRestClient;
import com.elasalle.lamp.data.repository.GuestLoginRepository;
import com.elasalle.lamp.model.ErrorMessage;
import com.elasalle.lamp.model.guest.GuestLogin;
import com.elasalle.lamp.model.guest.GuestLoginItems;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GuestTask extends LampTask {

    private final GuestLoginRepository guestLoginRepository;

    public GuestTask(LampRestClient lampRestClient, GuestLoginRepository guestLoginRepository) {
        super(lampRestClient);
        this.guestLoginRepository = guestLoginRepository;
    }

    @Override
    public void run() {
        final GuestLoginItems guestLogins = getGuestLoginList();
        if(guestLogins.items != null && guestLogins.items.size() > 0) {
            final Call<ResponseBody> call = lampRestClient.guest(guestLogins);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if(response.isSuccessful()) {
                        removeGuestLoginsFromDatasource();
                    } else {
                        final String message = new ErrorMessage(response.errorBody(), response.message()).message;
                        Log.e(getClass().getSimpleName(), message);
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.e(getClass().getSimpleName(), t.getMessage());
                }
            });
        }
    }

    private @Nullable
    GuestLoginItems getGuestLoginList() {
        List<GuestLogin> guestLogins = guestLoginRepository.findAll();
        GuestLoginItems guestLoginItems = new GuestLoginItems();
        guestLoginItems.items = guestLogins;
        return guestLoginItems;
    }

    private void removeGuestLoginsFromDatasource() {
        guestLoginRepository.deleteAll();
    }
}
