package com.elasalle.lamp.model;

public class SystemMessage {

    public static final String SYSTEM_MESSAGE = "com.elasalle.lamp.service.SYSTEM_MESSAGE";
    public static final String SYSTEM_MESSAGE_LEVEL = "com.elasalle.lamp.service.SYSTEM_MESSAGE_LEVEL";
    public static final String SYSTEM_MESSAGE_TITLE = "com.elasalle.lamp.service.SYSTEM_MESSAGE_TITLE";
    public static final String SYSTEM_MESSAGE_DISMISS = "com.elasalle.lamp.service.SYSTEM_MESSAGE_DISMISS";
    public static final String DISMISSIBLE_MESSAGE = "1";
    public static final String NON_DISMISSIBLE_MESSAGE = "2";

    public final String title;
    public final String message;
    public final String level;

    public SystemMessage(String message, String level) {
        this.title = null;
        this.message = message;
        this.level = level;
    }

    public SystemMessage(String title, String message, String level) {
        this.title = title;
        this.message = message;
        this.level = level;
    }
}
