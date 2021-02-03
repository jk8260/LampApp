package com.elasalle.lamp.data;

import android.support.annotation.NonNull;
import android.util.Log;

import com.elasalle.lamp.LampApp;
import com.elasalle.lamp.model.user.User;

import org.apache.commons.codec.binary.Hex;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.inject.Inject;

import dagger.Lazy;
import io.realm.Realm;
import io.realm.RealmConfiguration;

public class DataStoreManager {

    private static final String TAG = DataStoreManager.class.getSimpleName();

    private final Lazy<User> user;

    @Inject
    public DataStoreManager(Lazy<User> user) {
        this.user = user;
    }

    public Realm datasource() {
        String name = user.get().isGuest() ? user.get().getEmail() : user.get().getUsername();
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(name.getBytes("UTF-8"));
            name = new String(Hex.encodeHex(md.digest()));
        } catch (NoSuchAlgorithmException|UnsupportedEncodingException e) {
            Log.e(TAG, e.getMessage());
        }
        return getRealmDatabase(name);
    }

    private Realm getRealmDatabase(@NonNull final String name) {
        Realm.init(LampApp.getInstance());
        RealmConfiguration configuration = new RealmConfiguration.Builder()
                .name(name)
                .build();
        return Realm.getInstance(configuration);
    }

    public Realm sessionDatasource() {
        return getRealmDatabase("session");
    }

}
