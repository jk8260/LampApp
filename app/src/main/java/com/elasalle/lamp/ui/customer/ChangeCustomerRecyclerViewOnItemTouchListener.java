package com.elasalle.lamp.ui.customer;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.elasalle.lamp.model.user.Customer;

import java.lang.ref.WeakReference;

public class ChangeCustomerRecyclerViewOnItemTouchListener implements RecyclerView.OnItemTouchListener {

    private WeakReference<RecyclerView> recyclerViewWeakReference;
    private final GestureDetector gestureDetector;

    public ChangeCustomerRecyclerViewOnItemTouchListener(Context context) {
        this.gestureDetector = new GestureDetector(context, new ChangeCustomerSimpleOnGestureListener());
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        final View child = rv.findChildViewUnder(e.getX(), e.getY());
        if (child != null && gestureDetector.onTouchEvent(e)) {
            final int position = rv.getChildAdapterPosition(child);
            selectCustomer(position);
        }
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }

    private void selectCustomer(final int position) {
        final ChangeCustomerAdapter adapter = (ChangeCustomerAdapter) recyclerViewWeakReference.get().getAdapter();
        final Customer customerSelected = adapter.getCustomerAtPosition(position);
        adapter.setSelectedCustomerId(customerSelected.getCustomerId());
        adapter.notifyDataSetChanged();
    }

    private class ChangeCustomerSimpleOnGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            super.onSingleTapUp(e);
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            super.onLongPress(e);
            final View child = recyclerViewWeakReference.get().findChildViewUnder(e.getX(), e.getY());
            final int position = recyclerViewWeakReference.get().getChildAdapterPosition(child);
            selectCustomer(position);
        }
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        this.recyclerViewWeakReference = new WeakReference<>(recyclerView);
    }

}
