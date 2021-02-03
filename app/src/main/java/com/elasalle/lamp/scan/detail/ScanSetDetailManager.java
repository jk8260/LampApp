package com.elasalle.lamp.scan.detail;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.elasalle.lamp.LampApp;
import com.elasalle.lamp.R;
import com.elasalle.lamp.data.repository.NewScanSetRepository;
import com.elasalle.lamp.data.repository.ScanSetRepository;
import com.elasalle.lamp.model.scan.Asset;
import com.elasalle.lamp.model.scan.ScanSet;
import com.elasalle.lamp.model.user.Field;
import com.elasalle.lamp.scan.model.NewScanSet;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import javax.inject.Inject;

public class ScanSetDetailManager {

    private static final String TAG = ScanSetDetailManager.class.getSimpleName();

    private final ScanSetRepository scanSetRepository;
    private final NewScanSetRepository newScanSetRepository;

    @Inject
    public ScanSetDetailManager(ScanSetRepository scanSetRepository, NewScanSetRepository newScanSetRepository) {
        this.scanSetRepository = scanSetRepository;
        this.newScanSetRepository = newScanSetRepository;
    }

    public NewScanSet getConfiguredScanSetForScanSet(ScanSet scanSet) {
        return newScanSetRepository.findById(scanSet.getId());
    }

    public void duplicate(ScanSet scanSet, Runnable successCallback, Runnable failureCallback) {
        try {
            final NewScanSet duplicate = duplicateConfiguredScanSet(scanSet);
            duplicateScanSet(duplicate);
            if (successCallback != null) {
                successCallback.run();
            }
        } catch (Exception e) {
            Log.e(ScanSetDetailManager.class.getSimpleName(), e.getMessage(), e);
            if (failureCallback != null) {
                failureCallback.run();
            }
        }
    }

    @NonNull
    private NewScanSet duplicateConfiguredScanSet(ScanSet scanSet) {
        final String newId = UUID.randomUUID().toString();
        final NewScanSet newScanSet = getConfiguredScanSetForScanSet(scanSet);
        final SimpleDateFormat sdf = new SimpleDateFormat("M/d/yy", Locale.US);
        String newName = newScanSet.getName() + "-" + sdf.format(new Date());
        newName = generateDuplicateScanSetName(newName);
        final NewScanSet duplicate = new NewScanSet(newName, newId, newScanSet.getCustomerId());
        duplicate.setAssets(new ArrayList<Asset>());
        duplicate.setScanSetFields(newScanSet.getScanSetFields());
        newScanSetRepository.save(duplicate);
        return duplicate;
    }

    private String generateDuplicateScanSetName(String newName) {
        final List<NewScanSet> newScanSetList = newScanSetRepository.findAll();
        if (nameExists(newName, newScanSetList)) {
            int i = 1;
            while (true) {
                newName = newName.replaceAll("(\\s+\\(\\d+\\))", "") + String.format(Locale.US, " (%d)", i++);
                if (!nameExists(newName, newScanSetList)) {
                    break;
                }
            }
        }
        return newName;
    }

    private boolean nameExists(final String name, final List<NewScanSet> newScanSetList) {
        boolean exists = false;
        for (NewScanSet scanSet : newScanSetList) {
            if (scanSet.getName().contains(name)) {
                exists = true;
                break;
            }
        }
        return exists;
    }

    private void duplicateScanSet(NewScanSet duplicate) {
        final ScanSet duplicateScanSet = new ScanSet();
        duplicateScanSet.setId(duplicate.getId());
        duplicateScanSet.setCustomerId(duplicate.getCustomerId());
        duplicateScanSet.setName(duplicate.getName());
        duplicateScanSet.setAssets(new ArrayList<Asset>());
        duplicateScanSet.setModifiedDate(new Date());
        duplicateScanSet.setSyncDate(null);
        scanSetRepository.save(duplicateScanSet);
    }

    public void save(ScanSet scanSet) {
        scanSetRepository.save(scanSet);
        NewScanSet newScanSet = getConfiguredScanSetForScanSet(scanSet);
        newScanSet.setAssets(scanSet.getAssets());
        newScanSetRepository.save(newScanSet);
    }

    public void save(NewScanSet newScanSet) {
        newScanSetRepository.save(newScanSet);
        ScanSet scanSet = scanSetRepository.findById(newScanSet.getId());
        scanSet.setAssets(newScanSet.getAssets());
        scanSetRepository.save(scanSet);
    }

    public void updateForRemovedAsset(NewScanSet newScanSet, ScanSet scanSet) {
        scanSet.setAssets(newScanSet.getAssets());
        save(scanSet);
    }

    public void shareCSV(Activity activity, ScanSet scanSet, NewScanSet configuredScanSet) {
            try {
                save(configuredScanSet);
                generateCsv(activity, configuredScanSet, scanSet);
                sendCsv(activity, scanSet);
            } catch (IOException e) {
                Log.e(TAG, e.getMessage(), e);
                Toast.makeText(activity.getApplicationContext(), activity.getString(R.string.error_scan_set_csv_failure_message), Toast.LENGTH_LONG).show();
            }
        }

    private void sendCsv(Activity activity, ScanSet scanSet) {
        @SuppressWarnings("ConstantConditions")
        File file = FileUtils.getFile(activity.getExternalCacheDir(), scanSet.getName() + ".csv");
        Uri uri = Uri.fromFile(file);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.setType("text/csv");
        activity.startActivity(Intent.createChooser(intent, activity.getString(R.string.action_share)));
    }

    @SuppressLint("SetWorldReadable")
    private void generateCsv(Activity activity, NewScanSet configuredScanSet, ScanSet scanSet) throws IOException {
        final String COMMA = ",";
        final String NEWLINE = "\n";
        final StringBuilder stringBuilder = new StringBuilder();
        boolean isFieldsNameSet = false;
        for (final Asset asset : configuredScanSet.getAssets()) {
            if (!isFieldsNameSet) {
                addCsvAssetHeader(COMMA, NEWLINE, stringBuilder, asset);
                isFieldsNameSet = true;
            }
            addCsvAssetValues(COMMA, NEWLINE, stringBuilder, asset);
        }
        saveCsvToFile(activity, stringBuilder, scanSet);
    }

    private void addCsvAssetValues(String COMMA, String NEWLINE, StringBuilder stringBuilder, Asset asset) {
        for (final Field field : asset.getFields()) {
            String key;
            if (Field.LOOKUP_LIST_TYPE.equals(field.getType())) {
                key = field.getFieldId() + Field.LOOKUP_LIST_FIELD_ID_DISPLAY_SUFFIX;
            } else {
                key = field.getFieldId();
            }
            final String value = asset.getFieldIdValueMap().get(key);
            stringBuilder.append(getUnicodeString(value));
            stringBuilder.append(COMMA);
        }
        stringBuilder.deleteCharAt(stringBuilder.lastIndexOf(COMMA));
        stringBuilder.append(NEWLINE);
    }

    private void addCsvAssetHeader(String COMMA, String NEWLINE, StringBuilder stringBuilder, Asset asset) {
        for (final Field field : asset.getFields()) {
            final String fieldName = field.getName();
            stringBuilder.append(getUnicodeString(fieldName));
            stringBuilder.append(COMMA);
        }
        stringBuilder.deleteCharAt(stringBuilder.lastIndexOf(COMMA));
        stringBuilder.append(NEWLINE);
    }

    @NonNull
    private String getUnicodeString(final String value) {
        return new String(value.getBytes(), Charset.forName("UTF-8"));
    }

    @SuppressLint("SetWorldReadable")
    private void saveCsvToFile(Activity activity, StringBuilder stringBuilder, ScanSet scanSet) throws IOException {
        FileUtils.deleteQuietly(LampApp.getInstance().getExternalCacheDir());
        File file = new File(activity.getExternalCacheDir(), scanSet.getName() + ".csv");
        //noinspection ResultOfMethodCallIgnored
        file.setReadable(true, false);
        FileUtils.writeStringToFile(file, stringBuilder.toString());
    }
}
