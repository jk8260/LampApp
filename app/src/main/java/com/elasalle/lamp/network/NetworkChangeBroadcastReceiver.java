package com.elasalle.lamp.network;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;

import com.elasalle.lamp.LampApp;
import com.elasalle.lamp.R;
import com.elasalle.lamp.main.DashboardFragment;
import com.elasalle.lamp.main.MainActivity;
import com.elasalle.lamp.model.SystemMessage;
import com.elasalle.lamp.model.user.User;

import java.util.List;

public class NetworkChangeBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION) && LampApp.appShouldHandleNetworkChange()) {
            if(LampApp.isApplicationConnected()) {
                onApplicationOnline();
            } else {
                onApplicationOffline();
            }
        }
    }

    private void onApplicationOnline() {
        User user = LampApp.getSessionManager().user();
        if (user == null) {
            return;// No logged-in user or guest
        }
        Activity topActivity = LampApp.getInstance().getTopActivity();
        if (topActivity instanceof MainActivity) {
            sendDismissMessage();
            List<Fragment> fragments = ((MainActivity) topActivity).getSupportFragmentManager().getFragments();
            for (Fragment fragment : fragments) {
                if (fragment instanceof DashboardFragment) {
                    ((DashboardFragment) fragment).checkConnectivity();
                    break;
                }
            }
        }
    }

    private void onApplicationOffline() {
        User user = LampApp.getSessionManager().user();
        if (user == null) {
            return;// No logged-in user or guest
        }
        displayDashboard();
        sendNoNetworkMessage();
    }

    private void displayDashboard() {
        LampApp application = LampApp.getInstance();
        Intent intent = new Intent(application.getApplicationContext(), MainActivity.class);
        Activity topActivity = application.getTopActivity();
        if (topActivity != null) {
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            topActivity.startActivity(intent);
        } else {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            application.startActivity(intent);
        }
    }

    public static void sendNoNetworkMessage() {
        final String title = LampApp.getInstance().getString(R.string.dashboard_no_network_title);
        final String message = LampApp.getInstance().getString(R.string.dashboard_no_network_message);
        final String level = SystemMessage.NON_DISMISSIBLE_MESSAGE;
        LampApp.getSessionManager().setSystemMessage(new SystemMessage(title, message, level));
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(LampApp.getInstance());
        Intent intent = new Intent(SystemMessage.SYSTEM_MESSAGE);
        intent.putExtra(SystemMessage.SYSTEM_MESSAGE_TITLE, title);
        intent.putExtra(SystemMessage.SYSTEM_MESSAGE, message);
        intent.putExtra(SystemMessage.SYSTEM_MESSAGE_LEVEL, level);
        localBroadcastManager.sendBroadcast(intent);
    }

    public static void sendDismissMessage() {
        LampApp.getSessionManager().clearSystemMessage();
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(LampApp.getInstance());
        Intent intent = new Intent(SystemMessage.SYSTEM_MESSAGE);
        intent.putExtra(SystemMessage.SYSTEM_MESSAGE_DISMISS, true);
        localBroadcastManager.sendBroadcast(intent);
    }
}
