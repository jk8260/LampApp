package com.elasalle.lamp.notification;

import android.util.SparseBooleanArray;
import android.view.MenuItem;

public interface OnContextualActionBarMenuItemClickListener {
    void onMenuItemClick(MenuItem menuItem, SparseBooleanArray sparseBooleanArray);
}
