package com.elasalle.lamp.model.guest;


import com.elasalle.lamp.BuildConfig;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.ISODateTimeFormat;

public class GuestLogin {

    public final String email;
    public final String timestamp;
    public final String platform;
    public final String appVersion;

    public GuestLogin(String email) {
        this.email = email;
        this.timestamp = ISODateTimeFormat.dateTimeNoMillis().print(new DateTime(DateTimeZone.UTC));
        this.platform = "Android";
        this.appVersion = BuildConfig.VERSION_NAME;
    }

    public GuestLogin(String email, String timestamp, String platform, String appVersion) {
        this.email = email;
        this.timestamp = timestamp;
        this.platform = platform;
        this.appVersion = appVersion;
    }
}
