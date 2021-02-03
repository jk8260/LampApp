package com.elasalle.lamp.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.widget.Spinner;

import com.elasalle.lamp.LampApp;
import com.elasalle.lamp.R;

@SuppressWarnings("deprecation")
@SuppressLint("NewApi")
public class ResourcesUtil {

    private static final int CURRENT_SDK = Build.VERSION.SDK_INT;
    private static final int MARSHMALLOW = Build.VERSION_CODES.M;
    private static final int LOLLIPOP_API_22 = Build.VERSION_CODES.LOLLIPOP_MR1;
    private static final int LOLLIPOP = Build.VERSION_CODES.LOLLIPOP;
    private static final int KITKAT = Build.VERSION_CODES.KITKAT;

    public static Drawable getDrawable(final int id, @Nullable Theme theme) {
        if (CURRENT_SDK >= LOLLIPOP) {
            return getResources().getDrawable(id, theme);
        } else {
            return getResources().getDrawable(id);
        }
    }

    public static int getColor(final int id, @Nullable Theme theme) {
        if (CURRENT_SDK >= MARSHMALLOW) {
            return getResources().getColor(id, theme);
        } else {
            return getResources().getColor(id);
        }
    }

    public static Resources getResources() {
        return getContext().getResources();
    }

    private static Context getContext() {
        return LampApp.getInstance().getApplicationContext();
    }

    public static String getString(int id) {
        return getResources().getString(id);
    }

    public static void setSpinnerBackgroundTint(Spinner spinner) {
        if (CURRENT_SDK == KITKAT) {
            Drawable drawable = spinner.getBackground().getConstantState().newDrawable();
            drawable.setColorFilter(getResources().getColor(R.color.primary), PorterDuff.Mode.SRC_ATOP);
            spinner.setBackground(drawable);
        }
    }
}
