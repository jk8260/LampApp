package com.elasalle.lamp.scan.newset;

import android.os.Bundle;

import com.elasalle.lamp.scan.model.NewScanSet;

public class RenameScanSetDialogFragment extends NewScanSetDialogFragment {

    public static final String KEY_ARGUMENT = "Argument";
    private Runnable onRenameCallback;

    @Override
    protected void displayNewScanSetScreen(String name) {
        final Bundle arguments = getArguments();
        if (arguments != null) {
            final NewScanSet newScanSet = arguments.getParcelable(KEY_ARGUMENT);
            if (newScanSet != null) {
                newScanSet.setName(name);
            }
        }
        if (onRenameCallback != null) {
            onRenameCallback.run();
        }
    }

    public void setOnRenameCallback(final Runnable onRenameCallback) {
        this.onRenameCallback = onRenameCallback;
    }
}
