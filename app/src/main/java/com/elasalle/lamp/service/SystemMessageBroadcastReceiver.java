package com.elasalle.lamp.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.elasalle.lamp.model.SystemMessage;

public class SystemMessageBroadcastReceiver extends BroadcastReceiver {

    private Runnable task;
    private String message;
    private String title;
    private String level;
    private boolean isDismissSystemMessages;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(SystemMessage.SYSTEM_MESSAGE)) {
            message = intent.getStringExtra(SystemMessage.SYSTEM_MESSAGE);
            level = intent.getStringExtra(SystemMessage.SYSTEM_MESSAGE_LEVEL);
            title = intent.getStringExtra(SystemMessage.SYSTEM_MESSAGE_TITLE);
            isDismissSystemMessages = intent.getBooleanExtra(SystemMessage.SYSTEM_MESSAGE_DISMISS, false);
            if (task != null) {
                task.run();
            }
        }
    }

    public void setTask(Runnable task) {
        this.task = task;
    }

    public boolean isMessageDismissible() {
        return level.equals(SystemMessage.DISMISSIBLE_MESSAGE);
    }

    public String getMessage() {
        return message;
    }

    public String getTitle() {
        return title;
    }

    public boolean isDismissSystemMessages() {
        return isDismissSystemMessages;
    }
}
