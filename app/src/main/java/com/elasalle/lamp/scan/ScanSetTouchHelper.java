package com.elasalle.lamp.scan;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.elasalle.lamp.R;
import com.elasalle.lamp.util.MessageHelper;
import com.elasalle.lamp.util.ResourcesUtil;

import java.lang.ref.WeakReference;

public class ScanSetTouchHelper extends ItemTouchHelper.SimpleCallback {

    private final ScanSetAdapter mAdapter;
    private final WeakReference<Context> context;
    private Runnable onItemRemovedCallback;

    public ScanSetTouchHelper(ScanSetAdapter adapter, Context context) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.mAdapter = adapter;
        this.context = new WeakReference<>(context);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
        MessageHelper.displayConfirmationMessage(
                context.get(),
                ResourcesUtil.getString(R.string.delete_scan_set_message),
                new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.remove(viewHolder.getAdapterPosition());
                        if (onItemRemovedCallback != null) {
                            onItemRemovedCallback.run();
                        }
                    }
                },
                new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.notifyDataSetChanged();
                    }
                });
    }

    public void onItemRemoved(Runnable callback) {
        this.onItemRemovedCallback = callback;
    }
}
