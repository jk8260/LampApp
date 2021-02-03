package com.elasalle.lamp.ui.dashboard.menu;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.elasalle.lamp.LampApp;
import com.elasalle.lamp.R;
import com.elasalle.lamp.analytics.AnalyticsActivity;
import com.elasalle.lamp.model.user.User;
import com.elasalle.lamp.ui.customer.ChangeCustomerActivity;
import com.elasalle.lamp.util.ResourcesUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyAccountActivity extends AnalyticsActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.my_account_customer_company) TextView company;
    @BindView(R.id.my_account_info_name) TextView name;
    @BindView(R.id.my_account_info_name_title) TextView nameTitle;
    @BindView(R.id.my_account_info_username) TextView username;
    @BindView(R.id.my_account_info_username_title) TextView usernameTitle;
    @BindView(R.id.my_account_info_email) TextView email;
    @BindView(R.id.my_account_customer_title_layout) View customerTitleLayout;
    @BindView(R.id.my_account_customer_content_layout) View customerContentLayout;
    @BindView(R.id.changeCustomerButton) View changeCustomerButton;

    @Override
    protected void setAnalyticsScreenName() {
        mTracker.setScreenName(getString(R.string.analytics_my_account));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);
        ButterKnife.bind(this);
        LampApp.userComponent().inject(this);
        setupToolbar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUserInfo();
    }

    private void setUserInfo() {
        final User user = LampApp.getSessionManager().user();
        if(user.isGuest()) {
            setGuestInfo(user);
        } else {
            company.setText(user.getCompanyNameForCustomer(user.getCustomerId()));
            name.setText(user.getName());
            username.setText(user.getUsername());
            email.setText(user.getEmail());
            if (user.getCustomers().size() <= 1 || !LampApp.isApplicationConnected()) {
                changeCustomerButton.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void setGuestInfo(User user) {
        customerTitleLayout.setVisibility(View.GONE);
        customerContentLayout.setVisibility(View.GONE);
        name.setVisibility(View.GONE);
        nameTitle.setVisibility(View.GONE);
        username.setVisibility(View.GONE);
        usernameTitle.setVisibility(View.GONE);
        email.setText(user.getEmail());
    }

    private void setupToolbar() {
        toolbar.setTitle(getString(R.string.my_account_title));
        Drawable icon = ResourcesUtil.getDrawable(R.drawable.action_home, null);
        toolbar.setNavigationIcon(icon);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void changeCustomer(View view) {
        startActivity(new Intent(this, ChangeCustomerActivity.class));
    }
}
