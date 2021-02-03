package com.elasalle.lamp.camera;

import android.content.Context;
import android.graphics.Rect;

import com.google.zxing.client.android.camera.CameraManager;

public class LampCameraManager extends CameraManager {

    private ViewFinder viewfinder;

    public LampCameraManager(Context context) {
        super(context);
    }


    @Override
    public synchronized Rect getFramingRect() {
        if (viewfinder != null) {
            if (framingRect == null) {
                if (camera == null) {
                    return null;
                }
                framingRect = new Rect(viewfinder.getWidth() / 4, viewfinder.getHeight() / 4, viewfinder.getWidth() * 3/4, viewfinder.getHeight() * 3/4);
            }
            return framingRect;
        } else {
            return null;
        }
    }

    public void setViewfinder(ViewFinder viewfinder) {
        this.viewfinder = viewfinder;
    }
}
