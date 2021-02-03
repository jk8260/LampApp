package com.elasalle.lamp.notification;

import android.support.v7.widget.Toolbar;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import com.elasalle.lamp.R;

import java.util.Locale;

class NotificationsMultiChoiceModeListener implements AbsListView.MultiChoiceModeListener {

    private final AbsListView listView;
    private final NotificationsAdapter adapter;
    private final Toolbar toolbar;
    private int totalSelected;
    private int toolbarHeight;
    private SparseBooleanArray sparseBooleanArray;
    private OnContextualActionBarMenuItemClickListener onContextualActionBarMenuItemClickListener;

    public NotificationsMultiChoiceModeListener(AbsListView listView, Toolbar toolbar, NotificationsAdapter adapter) {
        this.listView = listView;
        this.adapter = adapter;
        this.toolbar = toolbar;
        this.totalSelected = 0;
    }

    @Override
    public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
        this.sparseBooleanArray.append(position, checked);
        final NotificationsAdapter.ViewHolder holder = (NotificationsAdapter.ViewHolder) this.listView.getChildAt(position).getTag();
        holder.isSelected = checked;
        if (checked) {
            this.totalSelected++;
        } else if (this.totalSelected > 0) {
            this.totalSelected--;
        }
        mode.setTitle(String.format(Locale.US, "%d Selected", this.totalSelected));
        this.adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.notifications_edit, menu);
        this.sparseBooleanArray = new SparseBooleanArray();
        this.adapter.setSelectionMode(true);
        collapseToolbar();
        return true;
    }

    private void collapseToolbar() {
        this.toolbar.setVisibility(View.INVISIBLE);
        ViewGroup.LayoutParams params = toolbar.getLayoutParams();
        if (params.height > 0) {
           this.toolbarHeight = params.height;
        }
        params.height = 0;
        this.toolbar.setLayoutParams(params);
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        if (onContextualActionBarMenuItemClickListener != null) {
            onContextualActionBarMenuItemClickListener.onMenuItemClick(item, this.sparseBooleanArray);
            mode.finish();
            return true;
        }
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        this.totalSelected = 0;
        expandToolbar();
        for (int position = 0; position < this.listView.getChildCount(); position++) {
            NotificationsAdapter.ViewHolder holder = (NotificationsAdapter.ViewHolder) this.listView.getChildAt(position).getTag();
            holder.isSelected = false;
        }
    }

    private void expandToolbar() {
        ViewGroup.LayoutParams params = toolbar.getLayoutParams();
        params.height = toolbarHeight;
        this.toolbar.setLayoutParams(params);
        this.toolbar.setVisibility(View.VISIBLE);
        this.adapter.setSelectionMode(false);
    }

    public void setOnContextualActionBarMenuItemClickListener(OnContextualActionBarMenuItemClickListener listener) {
        this.onContextualActionBarMenuItemClickListener = listener;
    }
}
