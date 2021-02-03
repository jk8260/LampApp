package com.elasalle.lamp.scan.newset;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.elasalle.lamp.R;
import com.elasalle.lamp.util.GoogleAnalyticsHelper;
import com.elasalle.lamp.util.MessageHelper;
import com.elasalle.lamp.util.ResourcesUtil;

public class NewScanSetDialogFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Dialog alertDialog = new AlertDialog.Builder(getActivity())
                .setView(R.layout.scan_new_scan_set_dialog)
                .create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                alertDialog.findViewById(R.id.button_ok).setOnClickListener(getOnCreateNewScanSetClickListener(dialog, alertDialog));
                alertDialog.findViewById(R.id.button_cancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                setUpNameInputField(alertDialog);
            }
        });
        return alertDialog;
    }

    @NonNull
    private View.OnClickListener getOnCreateNewScanSetClickListener(final DialogInterface dialog, final Dialog alertDialog) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText editText = (EditText) alertDialog.findViewById(R.id.scan_new_scan_set_name);
                final String name = editText.getText().toString();
                if (TextUtils.isEmpty(name)) {
                    MessageHelper.displayMessage(getActivity(), getString(R.string.error_empty_scan_set_name), null);
                } else {
                    sendAnalyticsEvent();
                    displayNewScanSetScreen(name);
                    dialog.dismiss();
                }
            }
        };
    }

    private void setUpNameInputField(Dialog alertDialog) {
        View editText = alertDialog.findViewById(R.id.scan_new_scan_set_name);
        if (editText != null) {
            editText.requestFocus();
            editText.getBackground().setColorFilter(ResourcesUtil.getColor(R.color.primary, null), PorterDuff.Mode.SRC_IN);
            displayKeyboard(editText);
        }
    }

    private void displayKeyboard(View editText) {
        final InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
    }

    protected void displayNewScanSetScreen(String name) {
        Intent intent = new Intent(getActivity(), NewScanSetActivity.class);
        intent.putExtra(NewScanSetActivity.KEY_TITLE, name.trim());
        startActivity(intent);
    }

    private void sendAnalyticsEvent() {
        GoogleAnalyticsHelper.sendAnalyticsEvent(
                getString(R.string.analytics_scan),
                getString(R.string.analytics_action_click_new_scan_set));
    }
}
