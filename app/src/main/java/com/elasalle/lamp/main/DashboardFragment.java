package com.elasalle.lamp.main;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.elasalle.lamp.LampApp;
import com.elasalle.lamp.R;
import com.elasalle.lamp.analytics.AnalyticsFragment;
import com.elasalle.lamp.model.user.Customer;
import com.elasalle.lamp.model.user.User;
import com.elasalle.lamp.notification.LampNotificationsManager;
import com.elasalle.lamp.ui.customer.ChangeCustomerActivity;
import com.elasalle.lamp.ui.dashboard.DashboardFooterFragment;
import com.elasalle.lamp.ui.dashboard.DashboardMessageFragment;
import com.elasalle.lamp.ui.dashboard.menu.AboutActivity;
import com.elasalle.lamp.ui.dashboard.menu.IntroductionActivity;
import com.elasalle.lamp.ui.dashboard.menu.MyAccountActivity;
import com.elasalle.lamp.ui.dashboard.menu.ThirdPartyLicensesActivity;
import com.elasalle.lamp.util.ResourcesUtil;

import java.lang.ref.WeakReference;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class DashboardFragment extends AnalyticsFragment {

    private static final String TAG = DashboardFragment.class.getSimpleName();

    @BindView(R.id.dashboard_search) View dashboardSearch;
    @BindView(R.id.dashboard_lookup) View dashboardLookup;
    @BindView(R.id.dashboard_notifications) View dashboardNotifications;
    @BindView(R.id.dashboard_scan) View dashboardScan;
    @BindView(R.id.dashboard_toolbar) Toolbar toolbar;

    @Inject User user;
    @Inject
    LampNotificationsManager notificationManager;

    private WeakReference<DashboardEventListener> mListener;
    private int selectedDashboardItemViewId = -1;
    private Unbinder unbinder;

    @Override
    protected void setAnalyticsScreenName() {
        mTracker.setScreenName(getString(R.string.analytics_dashboard));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!getActivity().getResources().getBoolean(R.bool.large_layout)) {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRetainInstance(true);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        unbinder = ButterKnife.bind(this, view);
        LampApp.notificationComponent(getActivity()).inject(this);
        this.notificationManager.onNotificationsDownloaded(new Runnable(){
            @Override
            public void run() {
                checkPrivileges();
            }
        });
        this.notificationManager.onNotificationsChanged(new Runnable() {
            @Override
            public void run() {
                checkPrivileges();
            }
        });
        setupViews();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void setupViews() {
        setupMessageFragment();
        setupFooterFragment();
        setupToolbar();
        if (user.isGuest()) {
            dashboardSearch.setVisibility(View.GONE);
            dashboardNotifications.setVisibility(View.GONE);
        } else {
            setupSearch();
            setupNotifications();
        }
        setupLookup();
        setupScan();
    }

    private void setupFooterFragment() {
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.dashboard_footer_fragment, new DashboardFooterFragment())
                .commit();
    }

    private void setupMessageFragment() {
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.dashboard_message_fragment, new DashboardMessageFragment())
                .commit();
    }

    private void setupToolbar() {
        toolbar.setTitle(getActivity().getResources().getString(R.string.dashboard_title));
        toolbar.setNavigationIcon(R.drawable.action_lasalle);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.get().closeDashboardIconClicked();
            }
        });
        setupToolbarMenu();
    }

    private void setupToolbarMenu() {
        Menu menu = toolbar.getMenu();
        if (menu != null) {
            menu.clear();
        }
        toolbar.inflateMenu(R.menu.dashboard_menu);
        MenuItem versionMenuItem = toolbar.getMenu().findItem(R.id.action_version);
        versionMenuItem.setTitle(getString(R.string.dashboard_menu_version) + "    " + LampApp.getVersionName());
        if (user.isGuest() || user.getCustomers().size() <= 1 || !LampApp.isApplicationConnected()) {
            if (getResources().getBoolean(R.bool.large_layout)) {
                toolbar.getMenu().findItem(R.id.action_change_customer).setVisible(false);
            } else {
                toolbar.getMenu().removeItem(R.id.action_change_customer);
            }
        }
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_about:
                        showAboutScreen();
                        break;
                    case R.id.action_third_party:
                        showThirdPartyLicensesScreen();
                        break;
                    case R.id.action_intro:
                        showIntroductionScreen();
                        break;
                    case R.id.action_my_account:
                        showMyAccountScreen();
                        break;
                    case R.id.action_logout:
                        logOut();
                        break;
                    case R.id.action_change_customer:
                        showChangeCustomerScreen();
                        break;
                    default:
                        return false;
                }
                return true;
            }
        });
    }

    private void showChangeCustomerScreen() {
        startActivity(new Intent(getActivity(), ChangeCustomerActivity.class));
    }

    private void logOut() {
        LampApp.getSessionManager().logout();
    }

    private void showMyAccountScreen() {
        startActivity(new Intent(getActivity(), MyAccountActivity.class));
    }

    private void showIntroductionScreen() {
        Intent intent = new Intent(getActivity(), IntroductionActivity.class);
        intent.putExtra(IntroductionActivity.INTENT_EXTRAS_FORCE_DISPLAY, true);
        startActivity(intent);
    }

    private void showThirdPartyLicensesScreen() {
        startActivity(new Intent(getActivity(), ThirdPartyLicensesActivity.class));
    }

    private void showAboutScreen() {
        startActivity(new Intent(getActivity(), AboutActivity.class));
    }

    private void setupSearch() {
        final View view = dashboardSearch;
        int iconId = R.drawable.home_search;
        String title = getString(R.string.dashboard_search_title);
        String description = getString(R.string.dashboard_search_description);
        updateDashboardItem(view, iconId, title, description);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectScreen(view,R.id.dashboard_search);
                mListener.get().searchSelected();
            }
        });
    }

    private void setupLookup() {
        final View view = dashboardLookup;
        int iconId = R.drawable.home_lookup;
        String title = getString(R.string.dashboard_lookup_title);
        String description = getString(R.string.dashboard_lookup_description);
        updateDashboardItem(view, iconId, title, description);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectScreen(view,R.id.dashboard_lookup);
                mListener.get().lookupSelected();
            }
        });
    }

    private void setupScan() {
        final View view = dashboardScan;
        int iconId = R.drawable.home_scan;
        String title = getString(R.string.dashboard_scan_title);
        String description = getString(R.string.dashboard_scan_description);
        updateDashboardItem(view, iconId, title, description);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectScreen(view,R.id.dashboard_scan);
                mListener.get().scanSelected();
            }
        });
    }

    private void setupNotifications() {
        final View view = dashboardNotifications;
        int iconId = R.drawable.home_notification;
        String title = getActivity().getResources().getString(R.string.dashboard_notifications_title);
        String description = getActivity().getResources().getString(R.string.dashboard_notifications_description);
        updateDashboardItem(view, iconId, title, description);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectScreen(view, R.id.dashboard_notifications);
                mListener.get().notificationsSelected();
            }
        });
        checkForNotifications();
    }

    private void checkForNotifications() {
        TextView badge = (TextView) dashboardNotifications.findViewById(R.id.badge);
        if (badge != null) {
            String badgeValue = notificationManager.getValueForNotificationsBadge();
            if (!TextUtils.isEmpty(badgeValue)) {
                badge.setVisibility(View.VISIBLE);
                badge.setText(badgeValue);
            } else {
                badge.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void updateDashboardItem(View view, int iconId, String title, String description) {
        TextView titleTextView = (TextView) view.findViewById(R.id.dashboard_item_title);
        titleTextView.setText(title);
        TextView descriptionTextView = (TextView) view.findViewById(R.id.dashboard_item_description);
        descriptionTextView.setText(description);
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.dashboard_item_fab);
        fab.setImageDrawable(ContextCompat.getDrawable(getActivity(), iconId));
    }

    private void selectScreen(View view, int id) {
        if (selectedDashboardItemViewId != -1) {
            setPreviousSelectedViewAsUnselected();
        }
        setViewAsSelected(view, id);
    }

    private void setViewAsSelected(View view, int id) {
        TextView titleTextView = (TextView) view.findViewById(R.id.dashboard_item_title);
        titleTextView.setTextColor(ContextCompat.getColor(getActivity(), R.color.primary));
        TextView descriptionTextView = (TextView) view.findViewById(R.id.dashboard_item_description);
        descriptionTextView.setTextColor(ContextCompat.getColor(getActivity(), R.color.accentDark));
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.dashboard_item_fab);
        fab.setSelected(true);
        View background = view.findViewById(R.id.dashboard_item_background);
        background.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.dashboard_item_background_selected));
        fab.setColorFilter(ResourcesUtil.getColor(R.color.white, null));
        selectedDashboardItemViewId = id;
    }

    private void setPreviousSelectedViewAsUnselected() {
        View view = getView();
        if (view != null) {
            View previous = getView().findViewById(selectedDashboardItemViewId);
            if (previous != null) {

                TextView titleTextView = (TextView) previous.findViewById(R.id.dashboard_item_title);
                titleTextView.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));

                TextView descriptionTextView = (TextView) previous.findViewById(R.id.dashboard_item_description);
                descriptionTextView.setTextColor(ContextCompat.getColor(getActivity(), R.color.grey3));

                View background = previous.findViewById(R.id.dashboard_item_background);
                background.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.dashboard_item_background));

                FloatingActionButton fab = (FloatingActionButton) previous.findViewById(R.id.dashboard_item_fab);
                fab.setSelected(false);
                fab.setColorFilter(ResourcesUtil.getColor(R.color.primary, null));

            } else {
                Log.e(TAG, "Previous view is null.");
            }
        } else {
            Log.e(TAG, "Current view is null.");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        notificationManager.registerForNotifications(getActivity());
        checkConnectivity();
        checkPrivileges();
    }

    @Override
    public void onPause() {
        super.onPause();
        notificationManager.unregisterForNotifications(getActivity());
    }

    private void checkPrivileges() {
        this.dashboardNotifications.setVisibility(View.GONE);
        if (!user.isGuest()) {
            Customer customer = LampApp.getSessionManager().getCustomerDetails();
            List<String> privileges =  customer.getPrivileges();
            if (privileges != null && privileges.size() > 0 ) {
                for (String privilege : privileges) {
                    if (privilege.equals(LampNotificationsManager.NOTIFICATION_PRIVILEGE)) {
                        this.dashboardNotifications.setVisibility(View.VISIBLE);
                        checkForNotifications();
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof DashboardEventListener) {
            mListener = new WeakReference<>((DashboardEventListener) context);
        } else {
            throw new RuntimeException(context.toString() + " must implement DashboardEventListener");
        }
    }

    public void checkConnectivity() {
        if (!LampApp.isApplicationConnected()) {
            this.dashboardSearch.setVisibility(View.GONE);
            this.dashboardLookup.setVisibility(View.GONE);
            this.dashboardNotifications.setVisibility(View.GONE);
        } else {
            if(user.isGuest()) {
                this.dashboardLookup.setVisibility(View.VISIBLE);
            } else {
                this.dashboardSearch.setVisibility(View.VISIBLE);
                this.dashboardLookup.setVisibility(View.VISIBLE);
            }
            checkPrivileges();
            setupToolbarMenu();
        }
    }
}
