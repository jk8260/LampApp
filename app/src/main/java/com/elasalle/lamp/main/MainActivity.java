package com.elasalle.lamp.main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.elasalle.lamp.BuildConfig;
import com.elasalle.lamp.LampApp;
import com.elasalle.lamp.R;
import com.elasalle.lamp.analytics.AnalyticsEvent;
import com.elasalle.lamp.lookup.LookupFragment;
import com.elasalle.lamp.notification.NotificationsFragment;
import com.elasalle.lamp.scan.ScanFragment;
import com.elasalle.lamp.search.SearchFragment;
import com.google.android.gms.analytics.HitBuilders;

import net.hockeyapp.android.UpdateManager;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements DashboardEventListener, AnalyticsEvent {

    public static final String PUSH_NOTIFICATION = "Push Notification";

    @Nullable @BindView(R.id.drawer_layout) DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //noinspection PointlessBooleanExpression
        if (BuildConfig.DEBUG && BuildConfig.FLAVOR.equalsIgnoreCase("qa")) {
            UpdateManager.register(this);
        }
        ButterKnife.bind(this);
        if (drawerLayout != null) {
            setupDrawer();
        }
        if (savedInstanceState != null) {
            if (savedInstanceState.getBoolean("drawerOpen")) {
                showDashboard();
            }
        } else {
            showDashboard();
        }
        Intent intent = getIntent();
        if (intent != null && intent.getBooleanExtra(PUSH_NOTIFICATION, false)) {
            notificationsSelected();
        }
    }

    private void setupDrawer() {
        if (drawerLayout == null) {
            return;
        }
        Fragment fragment = new DashboardFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.dashboardFragment, fragment)
                .commit();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        for (Fragment fragment : fragments) {
            if (fragment instanceof DashboardFragment) {
                ((DashboardFragment) fragment).checkConnectivity();
                break;
            }
        }
        if (intent != null && intent.getBooleanExtra(PUSH_NOTIFICATION, false)) {
            notificationsSelected();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("drawerOpen", drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START));
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //noinspection PointlessBooleanExpression
        if (BuildConfig.DEBUG && BuildConfig.FLAVOR.equalsIgnoreCase("qa")) {
            UpdateManager.unregister();
        }
    }

    private void replaceFragment(Fragment fragment) {
        @SuppressLint("CommitTransaction")
        FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_main, fragment);
        if (drawerLayout == null) {
            fragmentTransaction.addToBackStack(null).commit();
        } else {
            fragmentTransaction.commit();
            hideDashboard();
        }
    }

    @Override
    public void searchSelected() {
        replaceFragment(new SearchFragment());
        String ANALYTICS_ACTION_SEARCH = getString(R.string.analytics_search);
        sendAnalyticsEvent(ANALYTICS_ACTION_SEARCH, null);
    }

    @Override
    public void lookupSelected() {
        replaceFragment(new LookupFragment());
        String ANALYTICS_ACTION_LOOKUP = getString(R.string.analytics_lookup);
        sendAnalyticsEvent(ANALYTICS_ACTION_LOOKUP, null);
    }

    @Override
    public void scanSelected() {
        replaceFragment(new ScanFragment());
        String ANALYTICS_ACTION_SCAN = getString(R.string.analytics_scan);
        sendAnalyticsEvent(ANALYTICS_ACTION_SCAN, null);
    }

    @Override
    public void notificationsSelected() {
        replaceFragment(new NotificationsFragment());
        String ANALYTICS_ACTION_NOTIFICATIONS = getString(R.string.analytics_notifications);
        sendAnalyticsEvent(ANALYTICS_ACTION_NOTIFICATIONS, null);
    }

    @Override
    public void closeDashboardIconClicked() {
        hideDashboard();
    }

    @Override
    public void onNavigationIconClick() {
        if (drawerLayout == null) {
            onBackPressed();
        } else {
            showDashboard();
        }
    }

    private void showDashboard() {
        if (drawerLayout != null) {
            drawerLayout.openDrawer(GravityCompat.START);
        } else {
            replaceFragment(new DashboardFragment());
        }
    }

    private void hideDashboard() {
        if (drawerLayout != null) {
            drawerLayout.closeDrawers();
        }
    }

    @Override
    public void sendAnalyticsEvent(String action, String label) {
        String ANALYTICS_CATEGORY = getString(R.string.analytics_dashboard);
        LampApp.getInstance().getDefaultTracker().send(new HitBuilders.EventBuilder()
                .setCategory(ANALYTICS_CATEGORY)
                .setAction(action)
                .build());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }
}
