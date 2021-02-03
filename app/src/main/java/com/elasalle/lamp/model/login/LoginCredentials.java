package com.elasalle.lamp.model.login;

import android.util.Base64;

public class LoginCredentials {

    private final String username;
    private final String password;

    public LoginCredentials(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public String toString() {
        if (username != null && password != null) {
            String credentials = username + ":" + password;
            return "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
        } else {
            return "";
        }
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

}
