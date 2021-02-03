package com.elasalle.lamp.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

import com.elasalle.lamp.R;
import com.elasalle.lamp.camera.CameraActivity;

public class CameraPermissionsHelper {

    private static final String TAG = CameraPermissionsHelper.class.getSimpleName();

    public static boolean isFeatureCameraAny(Context context) {
        PackageManager pm = context.getPackageManager();
        final boolean isFeatureCameraAny = pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
        if (!isFeatureCameraAny) {
            Toast.makeText(context, context.getString(R.string.error_no_camera), Toast.LENGTH_SHORT).show();
        }
        return isFeatureCameraAny;
    }

    public static boolean isCameraPermissionGranted(Fragment fragment, int requestCode) {
        int permission = ActivityCompat.checkSelfPermission(fragment.getContext(), Manifest.permission.CAMERA);
        if (permission == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            requestCameraPermission(fragment, requestCode);
            return false;
        }
    }

    public static boolean isCameraPermissionGranted(Activity activity, int requestCode) {
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);
        if (permission == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            requestCameraPermission(activity, requestCode);
            return false;
        }
    }

    public static void startCameraActivity(Fragment fragment, String cameraScanTerm) {
        Intent intent = new Intent(fragment.getActivity(), CameraActivity.class);
        intent.putExtra(CameraActivity.INTENT_PARAM_CAMERA_FIELD, cameraScanTerm);
        fragment.startActivityForResult(intent, CameraActivity.INTENT_CODE_CAMERA);
    }

    public static void startCameraActivity(Activity activity, String cameraScanTerm) {
        Intent intent = new Intent(activity, CameraActivity.class);
        intent.putExtra(CameraActivity.INTENT_PARAM_CAMERA_FIELD, cameraScanTerm);
        activity.startActivityForResult(intent, CameraActivity.INTENT_CODE_CAMERA);
    }

    private static void requestCameraPermission(Fragment fragment, int requestCode) {
        Log.i(TAG, "Requesting permission for camera.");
        fragment.requestPermissions(new String[]{Manifest.permission.CAMERA}, requestCode);
    }

    private static void requestCameraPermission(Activity activity, int requestCode) {
        Log.i(TAG, "Requesting permission for camera.");
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, requestCode);
    }

}
