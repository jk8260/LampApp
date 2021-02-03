package com.elasalle.lamp.scan.newset;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

public class ReorderFieldsTouchHelper extends ItemTouchHelper.SimpleCallback {
    private ReorderFieldsAdapter mAdapter;

    public ReorderFieldsTouchHelper(ReorderFieldsAdapter adapter){
        super(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0);
        this.mAdapter = adapter;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        mAdapter.swap(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

    }

}
