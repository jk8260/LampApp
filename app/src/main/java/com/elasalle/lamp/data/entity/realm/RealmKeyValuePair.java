package com.elasalle.lamp.data.entity.realm;

import android.util.Log;

import org.apache.commons.codec.binary.Hex;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RealmKeyValuePair extends RealmObject {
    @PrimaryKey
    private String id;
    public String key;
    public String value;

    public RealmKeyValuePair() {}

    public RealmKeyValuePair(String key, String value) {
        this.key = key;
        this.value = value;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            String primaryKey = key + value;
            md.update(primaryKey.getBytes("UTF-8"));
            this.id = new String(Hex.encodeHex(md.digest()));
        } catch (NoSuchAlgorithmException|UnsupportedEncodingException e) {
            Log.e(getClass().getSimpleName(), e.getMessage());
            this.id = key + value;
        }
    }

    public String getId() {
        return id;
    }
}
