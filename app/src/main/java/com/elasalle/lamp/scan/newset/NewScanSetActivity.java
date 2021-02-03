package com.elasalle.lamp.scan.newset;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.elasalle.lamp.LampApp;
import com.elasalle.lamp.R;
import com.elasalle.lamp.analytics.AnalyticsActivity;
import com.elasalle.lamp.model.user.Customer;
import com.elasalle.lamp.model.user.Field;
import com.elasalle.lamp.scan.ScanSetManager;
import com.elasalle.lamp.scan.model.NewScanSet;
import com.elasalle.lamp.scan.model.ScanSetField;
import com.elasalle.lamp.search.SearchOnQueryTextListener;
import com.elasalle.lamp.ui.FooterFragment;
import com.elasalle.lamp.util.DisplayHelper;
import com.elasalle.lamp.util.ResourcesUtil;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NewScanSetActivity extends AnalyticsActivity implements NewScanSetBaseFragment.NewScanSetInterface {

    public static final String KEY_TITLE = "title";
    public static final String KEY_EDIT = "edit";
    public static final String KEY_EDIT_SCAN_SET_ID = "Edit Scan Set Id";

    private static final String KEY_NEW_SCAN_SET = "New Scan Set";
    private static final String KEY_CURRENT_FRAGMENT = "Current Fragment";
    private static final String FRAGMENT_TAG_FIELDS = "Display Fields";
    private static final String FRAGMENT_TAG_SELECT_FIELDS = "Select Fields";
    private static final String FRAGMENT_TAG_FIELD_SETTINGS = "Field Settings";
    private static final String FRAGMENT_TAG_REORDER_FIELDS = "Reorder Fields";

    private NewScanSet newScanSet;
    private String currentFragmentTag;

    @BindView(R.id.scan_set_add_fields) FloatingActionButton fabAddFields;
    @BindView(R.id.scan_set_done) FloatingActionButton fabDone;

    @Inject ScanSetManager scanSetManager;
    @Inject RenameScanSetDialogFragment renameScanSetDialogFragment;

    @Override
    protected void setAnalyticsScreenName() {
        mTracker.setScreenName(getString(R.string.analytics_new_scan_set));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_scan_set);
        ButterKnife.bind(this);
        LampApp.scanSetComponent().inject(this);
        setupActionBar();
        setUpFooterFragment();
        setUpFabs();
        String title;
        final boolean isEdit = getIntent().getBooleanExtra(KEY_EDIT, false);
        if (savedInstanceState == null) {
            if (isEdit) {
                final String id = getIntent().getStringExtra(KEY_EDIT_SCAN_SET_ID);
                newScanSet = scanSetManager.getScanSet(id);
                title = newScanSet.getName();
            } else {
                title = getIntent().getStringExtra(KEY_TITLE);
                newScanSet = new NewScanSet(title, scanSetManager.getCustomerId());
                addInitialFields();
            }
            showFieldsFragment();
        } else {
            title = savedInstanceState.getString(KEY_TITLE);
            newScanSet = savedInstanceState.getParcelable(KEY_NEW_SCAN_SET);
            Fragment fragment = getSupportFragmentManager().getFragment(savedInstanceState, KEY_CURRENT_FRAGMENT);
            restoreFragment(fragment);
        }
        setTitle(title);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String fieldName = null;
        if (currentFragmentTag.equals(FRAGMENT_TAG_FIELD_SETTINGS)) {
            Fragment fragment = getCurrentFragment();
            fieldName = ((FieldSettingsFragment) fragment).getScanSetField().getScanFieldLabel();
        }
        setTitleForTag(currentFragmentTag, fieldName);
    }

    @Override
    public void onBackPressed() {
        if (currentFragmentTag.equals(FRAGMENT_TAG_FIELDS)) {
            super.onBackPressed();
        } else {
            showFieldsFragment();
        }
    }

    private void restoreFragment(Fragment fragment) {
        currentFragmentTag = fragment.getTag();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment, currentFragmentTag)
                .commit();
        invalidateOptionsMenu();
        if (currentFragmentTag.equals(FRAGMENT_TAG_FIELDS)) {
            setTitle(newScanSet.getName());
            setupNavigationIconAsBack();
            fabAddFields.setVisibility(View.VISIBLE);
        } else {
            String fieldName = null;
            if (currentFragmentTag.equals(FRAGMENT_TAG_FIELD_SETTINGS)) {
                fieldName = ((FieldSettingsFragment) fragment).getScanSetField().getScanFieldLabel();
            } else if (currentFragmentTag.equals(FRAGMENT_TAG_SELECT_FIELDS)) {
                getSupportFragmentManager().executePendingTransactions();
            }
            setTitleForTag(fragment.getTag(), fieldName);
            setupNavigationIconAsClose();
            fabAddFields.setVisibility(View.GONE);
        }
    }

    private void setUpFabs() {
        fabAddFields.setVisibility(View.VISIBLE);
        fabAddFields.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectFieldsFragment();
            }
        });
        fabDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (currentFragmentTag) {
                    case FRAGMENT_TAG_FIELDS: {
                        if (newScanSet.getScanSetFields().size() > 0) {
                            scanSetManager.saveNewScanSet(newScanSet);
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), getString(R.string.scan_set_need_field_message), Toast.LENGTH_LONG).show();
                        }
                        break;
                    }
                    case FRAGMENT_TAG_SELECT_FIELDS: {
                        showFieldsFragment();
                        break;
                    }
                    case FRAGMENT_TAG_FIELD_SETTINGS: {
                        showFieldsFragment();
                        break;
                    }
                    case FRAGMENT_TAG_REORDER_FIELDS: {
                        showFieldsFragment();
                        break;
                    }
                }
            }
        });
    }

    private void setUpFooterFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.footer, new FooterFragment())
                .commit();
    }

    private void showFieldsFragment() {
        fabAddFields.setVisibility(View.VISIBLE);
        currentFragmentTag = FRAGMENT_TAG_FIELDS;
        setTitleForTag(FRAGMENT_TAG_FIELDS, null);
        setupNavigationIconAsBack();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, new FieldsFragment(), currentFragmentTag)
                .commit();
        invalidateOptionsMenu();
    }

    private void showSelectFieldsFragment() {
        fabAddFields.setVisibility(View.GONE);
        currentFragmentTag = FRAGMENT_TAG_SELECT_FIELDS;
        setTitleForTag(FRAGMENT_TAG_SELECT_FIELDS, null);
        setupNavigationIconAsClose();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, new SelectFieldsFragment(), currentFragmentTag)
                .commit();
        getSupportFragmentManager().executePendingTransactions();
        invalidateOptionsMenu();
    }

    private void showFieldSettingsFragment(Bundle arguments, String fieldName) {
        fabAddFields.setVisibility(View.GONE);
        currentFragmentTag = FRAGMENT_TAG_FIELD_SETTINGS;
        setTitleForTag(FRAGMENT_TAG_FIELD_SETTINGS, fieldName);
        setupNavigationIconAsClose();
        Fragment fragment = new FieldSettingsFragment();
        fragment.setArguments(arguments);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment, currentFragmentTag)
                .commit();
        invalidateOptionsMenu();
    }

    private void showReorderFieldsFragment() {
        fabAddFields.setVisibility(View.GONE);
        currentFragmentTag = FRAGMENT_TAG_REORDER_FIELDS;
        setTitleForTag(FRAGMENT_TAG_REORDER_FIELDS, null);
        setupNavigationIconAsClose();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, new ReorderFieldsFragment(), currentFragmentTag)
                .commit();
        invalidateOptionsMenu();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        String title = getIntent().getStringExtra(KEY_TITLE);
        outState.putString(KEY_TITLE, title);
        outState.putParcelable(KEY_NEW_SCAN_SET, newScanSet);
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(currentFragmentTag);
        getSupportFragmentManager().putFragment(outState, KEY_CURRENT_FRAGMENT, fragment);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.scan_set_new_menu, menu);
        menu.findItem(R.id.action_search).setVisible(false);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (currentFragmentTag.equals(FRAGMENT_TAG_SELECT_FIELDS)) {
            MenuItem menuItem = menu.findItem(R.id.action_search);
            menu.findItem(R.id.action_search).setVisible(true);
            setupSearch(menuItem);
        } else {
            menu.findItem(R.id.action_search).setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();
        switch (id) {
            case android.R.id.home: {
                onBackPressed();
                break;
            }
            case R.id.action_scan_reorder_fields: {
                showReorderFieldsFragment();
                break;
            }
            case R.id.action_scan_rename_scan_set: {
                final Bundle arguments = new Bundle();
                arguments.putParcelable(RenameScanSetDialogFragment.KEY_ARGUMENT, newScanSet);
                renameScanSetDialogFragment.setArguments(arguments);
                renameScanSetDialogFragment.setOnRenameCallback(new Runnable() {
                    @Override
                    public void run() {
                        setTitleForTag(currentFragmentTag, null);
                    }
                });
                renameScanSetDialogFragment.show(getSupportFragmentManager().beginTransaction(), "renameDialog");
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupActionBar() {
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupNavigationIconAsClose() {
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(ResourcesUtil.getDrawable(R.drawable.action_close, null));
        }
    }

    private void setupNavigationIconAsBack() {
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(ResourcesUtil.getDrawable(R.drawable.action_back, null));
        }
    }

    @Override
    public List<ScanSetField> getFields() {
        return newScanSet.getScanSetFields();
    }

    public void addInitialFields() {
        Customer customer = LampApp.getSessionManager().getCustomerDetails();
        List<Field> fields = customer.getFields();
        int order = 0;
        for (Field field : fields) {
            if (field.isRequired()) {
                ScanSetField scanSetField = new ScanSetField(field);
                scanSetField.setOrder(order);
                newScanSet.addScanSetField(scanSetField);
                order++;
            }
        }
    }

    @Override
    public void addField(ScanSetField scanSetField) {
        if (!newScanSet.getScanSetFields().contains(scanSetField)) {
            final int order = newScanSet.getScanSetFields().size();
            scanSetField.setSelected(true);
            scanSetField.setOrder(order);
            newScanSet.getScanSetFields().add(scanSetField);
        }
    }

    @Override
    public void removeField(ScanSetField scanSetField) {
        final String scanFieldLabel = scanSetField.getScanFieldLabel();
        Iterator<ScanSetField> iterator = newScanSet.getScanSetFields().iterator();
        while (iterator.hasNext()) {
            ScanSetField field = iterator.next();
            if (field.getScanFieldLabel().equals(scanFieldLabel)) {
                iterator.remove();
                break;
            }
        }
    }

    @Override
    public List<ScanSetField> getAllFields() {
        Customer customer = LampApp.getSessionManager().getCustomerDetails();
        List<Field> fields = customer.getFields();
        List<ScanSetField> scanSetFields = new LinkedList<>();
        for (Field field : fields) {
            ScanSetField scanSetField = new ScanSetField(field);
            if(newScanSet.getScanSetFields().contains(scanSetField)) {
                scanSetField.setSelected(true);
            }
            scanSetFields.add(scanSetField);
        }
        return scanSetFields;
    }

    @Override
    public void onFieldSettingsSelected(ScanSetField scanSetField) {
        Bundle arguments = new Bundle();
        arguments.putParcelable(FieldSettingsFragment.KEY_ARGUMENT, scanSetField);
        showFieldSettingsFragment(arguments, scanSetField.getScanFieldLabel());
    }

    private void setTitleForTag(final String tag, String fieldName) {
        String title;
        switch (tag) {
            case FRAGMENT_TAG_FIELDS: {
                title = newScanSet.getName();
                break;
            }
            case FRAGMENT_TAG_SELECT_FIELDS: {
                title = getString(R.string.select_fields);
                break;
            }
            case FRAGMENT_TAG_FIELD_SETTINGS: {
                title = fieldName + " " + getString(R.string.field_setting_suffix);
                break;
            }
            case FRAGMENT_TAG_REORDER_FIELDS: {
                title = getString(R.string.reorder_fields);
                break;
            }
            default: {
                title = newScanSet.getName();
                break;
            }
        }
        setTitle(title);
    }

    private Fragment getCurrentFragment() {
        return getSupportFragmentManager().findFragmentByTag(currentFragmentTag);
    }

    private void setupSearch(MenuItem menuItem) {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG_SELECT_FIELDS);
        if (fragment != null && currentFragmentTag.equals(FRAGMENT_TAG_SELECT_FIELDS)) {
            SearchOnQueryTextListener searchOnQueryTextListener = new SearchOnQueryTextListener();
            searchOnQueryTextListener.setFilterable(((SelectFieldsFragment) fragment).getAdapter());
            SearchView searchView = (SearchView) menuItem.getActionView();
            searchView.setMaxWidth(DisplayHelper.getWiderScreenWidth());
            searchView.setOnQueryTextListener(searchOnQueryTextListener);
        }
    }

}
