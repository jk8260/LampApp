package com.elasalle.lamp.util;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.elasalle.lamp.LampApp;
import com.elasalle.lamp.R;

public class DisplayHelper {

    public static int getWiderScreenWidth() {
        WindowManager wm = getWindowManager();
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.y > size.x ? size.y : size.x;
    }

    public static boolean isPortraitMode() {
        final int orientation = getResources().getConfiguration().orientation;
        return orientation == Configuration.ORIENTATION_PORTRAIT;
    }

    public static int getPixelsDp(float pixels) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (pixels * scale + 0.5f);
    }

    public static boolean isTablet() {
        return getResources().getBoolean(R.bool.large_layout);
    }

    private static Resources getResources() {
        return getContext().getResources();
    }

    private static Context getContext() {
        return LampApp.getInstance();
    }

    public static int getScreenWidth() {
        return getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        return getDisplayMetrics().heightPixels;
    }

    private static WindowManager getWindowManager() {
        return (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
    }

    private static DisplayMetrics getDisplayMetrics() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics;
    }
}
