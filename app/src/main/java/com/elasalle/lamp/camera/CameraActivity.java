package com.elasalle.lamp.camera;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.elasalle.lamp.LampApp;
import com.elasalle.lamp.R;
import com.elasalle.lamp.util.ResourcesUtil;
import com.google.zxing.Result;
import com.google.zxing.client.android.CaptureActivity;
import com.google.zxing.client.android.ViewfinderView;
import com.google.zxing.client.android.camera.CameraManager;
import com.google.zxing.client.android.result.ResultHandler;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CameraActivity extends CaptureActivity {

    public static final String INTENT_PARAM_CAMERA_FIELD = "field";
    public static final String INTENT_PARAM_BARCODE = "code";
    public static final int INTENT_CODE_CAMERA = 1;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.camera_field_name) TextView fieldName;
    @BindView(R.id.focused_barcode_text) TextView barcodeText;

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(R.layout.capture);
        ButterKnife.bind(this);
        toolbar.setNavigationIcon(ResourcesUtil.getDrawable(R.drawable.action_back, null));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        String scan = getIntent().getStringExtra(INTENT_PARAM_CAMERA_FIELD);
        fieldName.setText(scan);
        LampApp.getInstance().getDefaultTracker().setScreenName(getString(R.string.analytics_camera));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return true;
    }

    @Override
    protected void handleDecodeInternally(Result rawResult, ResultHandler resultHandler, Bitmap barcode) {
        barcodeText.setText(rawResult.getText());
        returnBarcode(null);
    }

    @Override
    protected void handleDecodeExternally(Result rawResult, ResultHandler resultHandler, Bitmap barcode) {
        handleDecodeInternally(rawResult, resultHandler, barcode);
    }

    @Override
    protected void resetStatusView() {
        viewfinderView.setVisibility(View.VISIBLE);
        lastResult = null;
    }

    public void returnBarcode(View view) {
        if (barcodeText != null) {
            Intent returnIntent = new Intent();
            returnIntent.putExtra(INTENT_PARAM_BARCODE, barcodeText.getText().toString());
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        } else {
            Toast.makeText(this, getResources().getString(R.string.no_barcode_detected_message), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected CameraManager getCameraManagerNewInstance() {
        LampCameraManager cameraManager = new LampCameraManager(getApplicationContext());
        viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
        cameraManager.setViewfinder((ViewFinder) viewfinderView);
        cameraManager.setManualFramingRect(viewfinderView.getWidth(), viewfinderView.getHeight());
        return cameraManager;
    }
}
