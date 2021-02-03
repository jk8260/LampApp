package com.elasalle.lamp.module;

import android.support.annotation.NonNull;

import com.elasalle.lamp.BuildConfig;
import com.elasalle.lamp.client.LampRestClient;
import com.elasalle.lamp.data.repository.GuestLoginRepository;
import com.elasalle.lamp.login.GuestLoginManager;
import com.elasalle.lamp.login.LoginManager;
import com.elasalle.lamp.login.ResetPasswordManager;
import com.elasalle.lamp.notification.LampNotificationsManager;
import com.elasalle.lamp.security.TokenManager;
import com.elasalle.lamp.service.CustomerDetailsTask;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

@Module
@Singleton
public class ServicesModule {

    @Provides
    @Singleton
    public LampRestClient lampClient() { return  getRetrofit().create(LampRestClient.class); }

    @NonNull
    private Retrofit getRetrofit() {
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .addConverterFactory(JacksonConverterFactory.create());
        return builder.build();
    }

    @Provides
    @Singleton
    public LoginManager loginManager(LampRestClient lampRestClient, TokenManager tokenManager, CustomerDetailsTask customerDetailsTask, LampNotificationsManager notificationsManager) {
        return new LoginManager(lampRestClient, tokenManager, customerDetailsTask, notificationsManager);
    }

    @Provides
    @Singleton
    public GuestLoginManager guestLoginManager(GuestLoginRepository guestLoginRepository) {
        return new GuestLoginManager(guestLoginRepository);
    }

    @Provides
    @Singleton
    public ResetPasswordManager resetPasswordManager(LampRestClient lampRestClient) {
        return new ResetPasswordManager(lampRestClient);
    }

    @Provides
    public OkHttpClient okHttpClient() {
        return new OkHttpClient.Builder().build();
    }

}
