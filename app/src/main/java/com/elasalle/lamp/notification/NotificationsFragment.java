package com.elasalle.lamp.notification;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.elasalle.lamp.LampApp;
import com.elasalle.lamp.R;
import com.elasalle.lamp.analytics.AnalyticsFragment;
import com.elasalle.lamp.main.DashboardEventListener;
import com.elasalle.lamp.model.notification.Notification;
import com.elasalle.lamp.ui.dashboard.menu.HelpActivity;
import com.elasalle.lamp.util.GoogleAnalyticsHelper;
import com.elasalle.lamp.util.PreferencesHelper;

import java.lang.ref.WeakReference;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NotificationsFragment extends AnalyticsFragment {

    @BindView(R.id.notifications_status) TextView footerNotificationsStatus;
    @BindView(R.id.notifications_alert_switch) Switch footerNotificationsAlertSwitch;
    @BindView(R.id.background) View background;
    @BindView(R.id.background_text) TextView backgroundText;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.list) AbsListView list;

    @Inject
    LampNotificationsManager lampNotificationsManager;

    private WeakReference<DashboardEventListener> mListener;

    @Override
    protected void setAnalyticsScreenName() {
        mTracker.setScreenName(getString(R.string.analytics_notifications));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);
        ButterKnife.bind(this, view);
        LampApp.notificationComponent(getActivity()).inject(this);
        setupToolbar();
        setNotificationsAlertBar();
        setSwitchListener();
        return view;
    }

    private void setSwitchListener() {
        footerNotificationsAlertSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreferencesHelper.turnNotificationsOn(footerNotificationsAlertSwitch.isChecked());
                setNotificationsAlertBar();
                if (PreferencesHelper.isNotificationsOn()) {
                    lampNotificationsManager.registerForPushNotifications();
                    GoogleAnalyticsHelper.sendAnalyticsEvent(
                            getString(R.string.analytics_notifications),
                            getString(R.string.analytics_action_turn_notifications_on)
                    );
                } else {
                    lampNotificationsManager.unregisterForPushNotifications();
                    GoogleAnalyticsHelper.sendAnalyticsEvent(
                            getString(R.string.analytics_notifications),
                            getString(R.string.analytics_action_turn_notifications_off)
                    );
                }
            }
        });
    }

    private void setNotificationsAlertBar() {
        if(PreferencesHelper.isNotificationsOn()) {
            footerNotificationsStatus.setText(getString(R.string.notifications_status_on));
            footerNotificationsAlertSwitch.setChecked(true);
        } else {
            footerNotificationsStatus.setText(getString(R.string.notifications_status_off));
            footerNotificationsAlertSwitch.setChecked(false);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof DashboardEventListener) {
            mListener = new WeakReference<>((DashboardEventListener)context);
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement DashboardEventListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setupList();
    }

    private void setupToolbar() {
        toolbar.setTitle(getString(R.string.dashboard_notifications_title));
        toolbar.setNavigationIcon(R.drawable.action_home);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.get().onNavigationIconClick();
            }
        });
        toolbar.inflateMenu(R.menu.notifications_main);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.notifications_menu_edit: {
                        list.setItemChecked(0, true);
                        return true;
                    }
                    case R.id.action_help: {
                        showHelp();
                        return true;
                    }
                    default: {
                        return false;
                    }
                }
            }
        });
    }

    private void setupList() {
        final List<Notification> notificationList = lampNotificationsManager.getNotifications();
        if (notificationList != null && notificationList.size() > 0) {
            hideBackground();
            final NotificationsAdapter adapter = new NotificationsAdapter(getActivity(), notificationList);
            list.setAdapter(adapter);
            list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
            list.setMultiChoiceModeListener(getMultiChoiceModeListener(adapter));
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    final Notification notification = (Notification) list.getItemAtPosition(position);
                    lampNotificationsManager.markAsRead(notification);
                    showDetail(notification.target, notification.id);
                    GoogleAnalyticsHelper.sendAnalyticsEvent(
                            getString(R.string.analytics_notifications),
                            getString(R.string.analytics_action_select_notification),
                            notification.id
                    );
                }
            });
        } else {
            showBackground();
        }
    }

    private void showDetail(String target, String id) {
        Intent intent = new Intent(getActivity(), NotificationDetailActivity.class);
        intent.putExtra(NotificationDetailActivity.KEY_URL, target);
        intent.putExtra(NotificationDetailActivity.KEY_NOTIFICATION_ID, id);
        startActivity(intent);
    }

    private AbsListView.MultiChoiceModeListener getMultiChoiceModeListener(final NotificationsAdapter adapter) {
        final NotificationsMultiChoiceModeListener listener = new NotificationsMultiChoiceModeListener(list, toolbar, adapter);
        listener.setOnContextualActionBarMenuItemClickListener(new OnContextualActionBarMenuItemClickListener() {
            @Override
            public void onMenuItemClick(MenuItem menuItem, SparseBooleanArray sparseBooleanArray) {
                for (int i = 0; i < sparseBooleanArray.size(); i++) {
                    int key = sparseBooleanArray.keyAt(i);
                    boolean isSelected = sparseBooleanArray.get(key);
                    if (isSelected) {
                        final Notification notification = (Notification) list.getItemAtPosition(key);
                        if (menuItem.getItemId() == R.id.notifications_menu_mark_read) {
                            lampNotificationsManager.markAsRead(notification);
                            adapter.notifyDataSetChanged();
                            GoogleAnalyticsHelper.sendAnalyticsEvent(
                                    getString(R.string.analytics_notifications),
                                    getString(R.string.analytics_action_mark_notification_as_read),
                                    notification.id
                            );
                        } else if (menuItem.getItemId() == R.id.notifications_menu_delete) {
                            lampNotificationsManager.delete(notification);
                            adapter.remove(notification);
                            GoogleAnalyticsHelper.sendAnalyticsEvent(
                                    getString(R.string.analytics_notifications),
                                    getString(R.string.analytics_action_remove_notification),
                                    notification.id
                            );
                        }
                    }
                }
                if (list.getAdapter().isEmpty()) {
                    showBackground();
                }
            }
        });
        return listener;
    }

    private void showBackground() {
        displayBackground(true);
    }

    private void hideBackground() {
        displayBackground(false);
    }

    private void displayBackground(final boolean isVisible) {
        if (isVisible) {
            list.setVisibility(View.INVISIBLE);
            background.setVisibility(View.VISIBLE);
        } else {
            list.setVisibility(View.VISIBLE);
            background.setVisibility(View.INVISIBLE);
        }
    }

    private void showHelp() {
        Intent intent = new Intent(getContext(), HelpActivity.class);
        intent.putExtra(HelpActivity.HELP_TYPE, HelpActivity.NOTIFICATIONS_HELP_INTENT);
        startActivity(intent);
    }
}
