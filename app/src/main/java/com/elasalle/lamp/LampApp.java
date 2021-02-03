package com.elasalle.lamp;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.elasalle.lamp.component.DaggerApplicationComponent;
import com.elasalle.lamp.component.DaggerNotificationComponent;
import com.elasalle.lamp.component.DaggerScanSetComponent;
import com.elasalle.lamp.component.DaggerSearchComponent;
import com.elasalle.lamp.component.DaggerServicesComponent;
import com.elasalle.lamp.component.DaggerUserComponent;
import com.elasalle.lamp.component.NotificationComponent;
import com.elasalle.lamp.component.ScanSetComponent;
import com.elasalle.lamp.component.SearchComponent;
import com.elasalle.lamp.component.ServicesComponent;
import com.elasalle.lamp.component.UserComponent;
import com.elasalle.lamp.module.ApplicationModule;
import com.elasalle.lamp.module.SearchModule;
import com.elasalle.lamp.module.ServicesModule;
import com.elasalle.lamp.module.UIModule;
import com.elasalle.lamp.module.UserModule;
import com.elasalle.lamp.service.LampTask;
import com.elasalle.lamp.service.UserConfigService;
import com.elasalle.lamp.session.SessionManager;
import com.elasalle.lamp.ui.BlockingMessageActivity;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.squareup.leakcanary.LeakCanary;

import net.hockeyapp.android.CrashManager;
import net.hockeyapp.android.CrashManagerListener;
import net.hockeyapp.android.metrics.MetricsManager;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import dagger.Lazy;

public class LampApp extends MultiDexApplication implements Application.ActivityLifecycleCallbacks {

    private final Stack<WeakReference<Activity>> activityStack = new Stack<>();
    private WeakReference<Activity> lastPausedActivity;
    private Tracker mTracker;

    private static UserComponent userComponent;
    private static ServicesComponent servicesComponent;
    private static LampApp instance;

    private static boolean shouldHandleNetworkChange = true;

    @Inject Lazy<SessionManager> sessionManager;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        registerActivityLifecycleCallbacks(this);
        if (BuildConfig.DEBUG && BuildConfig.FLAVOR.equals("qa")) {
            CrashManager.register(this, "1f1f1c3895e14c10adf42c6d2a15bc91", new CrashManagerListener() {
                @Override
                public boolean shouldAutoUploadCrashes() {
                    return true;
                }
            });
            MetricsManager.register(this, this);
        } else if (BuildConfig.DEBUG && BuildConfig.FLAVOR.equals("dev")) {
            LeakCanary.install(this);
        }
    }

    public static UserComponent userComponent() {
        if (userComponent == null) {
            userComponent = DaggerUserComponent.builder().userModule(new UserModule()).build();
        }
        return userComponent;
    }

    public static ServicesComponent servicesComponent() {
        if (servicesComponent == null) {
            servicesComponent = DaggerServicesComponent.builder().servicesModule(new ServicesModule()).build();
        }
        return servicesComponent;
    }

    public static SearchComponent searchComponent(Context context) {
        return DaggerSearchComponent.builder().searchModule(new SearchModule(instance.getApplicationContext())).uIModule(new UIModule(context)).build();
    }

    public static ScanSetComponent scanSetComponent() {
        return DaggerScanSetComponent.create();
    }

    public static NotificationComponent notificationComponent(Context context) { return  DaggerNotificationComponent.builder().uIModule(new UIModule(context)).build();}

    public static LampApp getInstance() {
        return instance;
    }

    public static SessionManager getSessionManager() {
        return getInstance().sessionManager();
    }

    public SessionManager sessionManager() {
        if (sessionManager == null || sessionManager.get() == null) {
            DaggerApplicationComponent.builder().applicationModule(new ApplicationModule()).build().inject(this);
        }
        return sessionManager.get();
    }

    public @Nullable Activity getTopActivity() {
        Activity activity = null;
        if (!activityStack.isEmpty()) {
            activity = this.activityStack.peek().get();
        }
        return activity;
    }

    public int getActivityStackSize() {
        return activityStack.size();
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        this.activityStack.push(new WeakReference<>(activity));
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        //noinspection StatementWithEmptyBody
        if (
            // Application first launch
            lastPausedActivity == null ||
            lastPausedActivity.get() == null ||
            // Application entered the foreground
            lastPausedActivity.get().equals(activity)
           ) {
            startConfigService();
        } else {
            // no-op: new activity pushed onto stack
        }
        shouldHandleNetworkChange = true;
    }

    @Override
    public void onActivityPaused(Activity activity) {
        // Skip BlockingMessageActivity due to onNewIntent()
        if (!(activity instanceof BlockingMessageActivity)) {
            lastPausedActivity = new WeakReference<>(activity);
        }
        shouldHandleNetworkChange = false;
    }

    @Override
    public void onActivityStopped(Activity activity) {
        // Add BlockingMessageActivity due to onNewIntent()
        if (activity instanceof BlockingMessageActivity) {
            lastPausedActivity = new WeakReference<>(activity);
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        if (activityStack.peek().get().equals(activity)) {
            this.activityStack.pop();
        } else {
            removeActivityFromStack(activity);
        }
        if (this.activityStack.empty()) {
            lastPausedActivity = null;
        }
    }

    private void removeActivityFromStack(Activity activity) {
        Iterator<WeakReference<Activity>> iterator = this.activityStack.iterator();
        //noinspection WhileLoopReplaceableByForEach
        while (iterator.hasNext()) {
            WeakReference<Activity> activityWeakReference = iterator.next();
            Activity stackActivity = activityWeakReference.get();
            if (stackActivity != null && stackActivity.equals(activity)) {
                this.activityStack.remove(activityWeakReference);
                break;
            }
        }
    }

    private void startConfigService() {
        if (lastPausedActivity != null && lastPausedActivity.get() instanceof BlockingMessageActivity) {
            // Workaround for BlockingMessageActivity due to onNewIntent()
            WeakReference<Activity> top = activityStack.pop();
            lastPausedActivity = activityStack.isEmpty() ? null : activityStack.peek();
            activityStack.push(top);
        }
        final Intent intent = new Intent(this, UserConfigService.class);
        intent.setAction(LampTask.INTENT_ACTION)
                .putExtra(LampTask.EXTRA_NAME, new String[] {
                        LampTask.Type.STATUS.name(),
                        LampTask.Type.ABOUT.name(),
                        LampTask.Type.SEARCH_HELP.name(),
                        LampTask.Type.LOOKUP_HELP.name(),
                        LampTask.Type.NOTIFICATIONS_HELP.name(),
                        LampTask.Type.SCAN_HELP.name()});
        // Allow splash, introduction, and/or login to be displayed first (that is, before any
        // blocking messages that may be displayed as a result of calling the /status endpoint).
        // That way none of the above will be displayed on top of the blocking message.
        final long DELAY_MILLISECONDS = 1000;
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                startService(intent);
            }
        }, DELAY_MILLISECONDS);
    }

    public static boolean isApplicationConnected() {
        ConnectivityManager cm = (ConnectivityManager) instance.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    synchronized public Tracker getDefaultTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            mTracker = analytics.newTracker(R.xml.global_tracker);
        }
        return mTracker;
    }

    public static String getVersionName() {
        String versionName;
        try {
            versionName = instance.getPackageManager().getPackageInfo(instance.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(instance.getClass().getSimpleName(), e.getMessage());
            versionName = BuildConfig.VERSION_NAME;
        }
        return versionName;
    }

    public static boolean appShouldHandleNetworkChange() {
        return shouldHandleNetworkChange;
    }
}
