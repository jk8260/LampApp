package com.elasalle.lamp.ui.dashboard;

import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.elasalle.lamp.LampApp;
import com.elasalle.lamp.R;
import com.elasalle.lamp.model.SystemMessage;
import com.elasalle.lamp.service.SystemMessageBroadcastReceiver;

import java.lang.ref.WeakReference;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class DashboardMessageFragment extends Fragment {

    @Inject
    SystemMessageBroadcastReceiver broadcastReceiver;

    @BindView(R.id.dashboard_message_content) TextView dashboardMessageContent;
    @BindView(R.id.dashboard_message_title) TextView dashboardMessageTitle;
    @BindView(R.id.dashboard_message) View dashboardMessageContainer;
    @BindView(R.id.dashboard_message_close) View dashboardMessageClose;
    @BindView(R.id.topBar) View topBar;
    @BindView(R.id.bottomBar) View bottomBar;
    @BindView(R.id.leftBar) View leftBar;
    @BindView(R.id.rightBar) View rightBar;

    private Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dashboard_message, container, false);
        unbinder = ButterKnife.bind(this, view);
        LampApp.servicesComponent().inject(this);
        broadcastReceiver.setTask(displaySystemMessageFromReceiver());
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        displaySystemMessageFromSession();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver, new IntentFilter(SystemMessage.SYSTEM_MESSAGE));
        checkConnectivity();
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void checkConnectivity() {
        if(LampApp.isApplicationConnected()) {
            if(getString(R.string.dashboard_no_network_title).equals(dashboardMessageTitle.getText().toString())) {
                dismissMessage();
            }
        } else {
            displayMessage(getString(R.string.dashboard_no_network_title),
                    getString(R.string.dashboard_no_network_message),
                    false);
        }
    }

    private void dismissMessage() {
        dashboardMessageContainer.setVisibility(View.GONE);
        LampApp.getSessionManager().clearSystemMessage();
    }

    private void displayMessage(final String title, final String message, final boolean isDismissible) {
        dashboardMessageContainer.setVisibility(View.VISIBLE);
        dashboardMessageContent.setText(message);
        dashboardMessageTitle.setText(title);
        if (isDismissible) {
            topBar.setVisibility(View.INVISIBLE);
            bottomBar.setVisibility(View.INVISIBLE);
            leftBar.setVisibility(View.INVISIBLE);
            rightBar.setVisibility(View.INVISIBLE);
            dashboardMessageClose.setVisibility(View.VISIBLE);
            dashboardMessageClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismissMessage();
                }
            });
        } else {
            dashboardMessageClose.setVisibility(View.GONE);
            topBar.setVisibility(View.VISIBLE);
            bottomBar.setVisibility(View.VISIBLE);
            leftBar.setVisibility(View.VISIBLE);
            rightBar.setVisibility(View.VISIBLE);

        }
    }

    private Runnable displaySystemMessageFromReceiver() {
        final WeakReference<SystemMessageBroadcastReceiver> broadcastReceiverWeakReference = new WeakReference<>(broadcastReceiver);
        final WeakReference<DashboardMessageFragment> dashboardMessageFragmentWeakReference = new WeakReference<>(this);
        return new Runnable() {
            @Override
            public void run() {
                if (broadcastReceiverWeakReference.get().isDismissSystemMessages()) {
                    dismissMessage();
                } else {
                    if (broadcastReceiverWeakReference.get() != null && !TextUtils.isEmpty(broadcastReceiverWeakReference.get().getMessage())) {
                        dashboardMessageFragmentWeakReference.get().displayMessage(
                                broadcastReceiverWeakReference.get().getTitle() != null ?
                                        broadcastReceiverWeakReference.get().getTitle() :
                                        dashboardMessageFragmentWeakReference.get().getString(R.string.dashboard_system_message_title),
                                broadcastReceiverWeakReference.get().getMessage(),
                                broadcastReceiverWeakReference.get().isMessageDismissible()
                        );
                    }
                }
            }
        };
    }

    private void displaySystemMessageFromSession() {
        SystemMessage systemMessage = LampApp.getSessionManager().systemMessage();
        if (systemMessage != null) {
            if (!TextUtils.isEmpty(systemMessage.message)) {
                displayMessage(
                        systemMessage.title != null ?
                                systemMessage.title :
                                getString(R.string.dashboard_system_message_title),
                        systemMessage.message,
                        systemMessage.level.equals(SystemMessage.DISMISSIBLE_MESSAGE)
                );
            }
        }
    }
}
