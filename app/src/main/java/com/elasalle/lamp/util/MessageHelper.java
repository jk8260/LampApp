package com.elasalle.lamp.util;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;

import com.elasalle.lamp.R;

public class MessageHelper {

    public static void displayMessage(@NonNull final Context context, @NonNull final String message, @Nullable final Runnable callback) {
        AlertDialog alertDialog = new AlertDialog
                .Builder(context, R.style.AppTheme_Dialog_Alert)
                .setMessage(message)
                .setPositiveButton(context.getString(R.string.alert_button_text_dismiss), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (callback != null) {
                            callback.run();
                        }
                    }
                })
                .setCancelable(false)
                .create();
        alertDialog.show();
    }

    public static void displayConfirmationMessage(@NonNull final Context context, @NonNull final String message, @Nullable final Runnable positiveCallback, @Nullable final Runnable negativeCallback) {
        AlertDialog alertDialog = new AlertDialog
                .Builder(context, R.style.AppTheme_Dialog_Alert)
                .setMessage(message)
                .setPositiveButton(context.getString(R.string.alert_dialog_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (positiveCallback != null) {
                            positiveCallback.run();
                        }
                    }
                })
                .setNegativeButton(context.getString(R.string.alert_dialog_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (negativeCallback != null) {
                            negativeCallback.run();
                        }
                    }
                })
                .setCancelable(false)
                .create();
        alertDialog.show();
    }

}
