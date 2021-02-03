package com.elasalle.lamp.ui.dashboard;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.elasalle.lamp.LampApp;
import com.elasalle.lamp.R;
import com.elasalle.lamp.client.ApiRequestCallback;
import com.elasalle.lamp.model.user.Customer;
import com.elasalle.lamp.service.LogoManager;
import com.elasalle.lamp.util.ResourcesUtil;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class DashboardFooterFragment extends Fragment {

    @BindView(R.id.dashboard_footer_company_logo) ImageView dashboardFooterCompanyLogo;

    @Inject LogoManager logoManager;

    private Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dashboard_footer, container, false);
        unbinder = ButterKnife.bind(this, view);
        LampApp.userComponent().inject(this);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        displayLogo();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void displayLogo() {
        final Customer customer = LampApp.getSessionManager().getCustomerDetails();
        if (customer != null &&
                !TextUtils.isEmpty(customer.getLogoUrl()) &&
                Patterns.WEB_URL.matcher(customer.getLogoUrl()).matches()) {
                logoManager.downloadCompanyLogo(customer, dashboardFooterCompanyLogo, new ApiRequestCallback() {
                    @Override
                    public void onSuccess() {
                        // no-op
                    }
                    @Override
                    public void onFailure(@NonNull String message) {
                        setDefaultLogo();
                    }
                });
        } else {
            setDefaultLogo();
        }
    }

    private void setDefaultLogo() {
        this.dashboardFooterCompanyLogo.post(new Runnable() {
            @Override
            public void run() {
                Drawable drawable = ResourcesUtil.getDrawable(R.drawable.login_lamp, null);
                dashboardFooterCompanyLogo.setImageDrawable(drawable);
                setLogoTint(R.color.black);
            }
        });
    }

    private void setLogoTint(final int logoTint) {
        this.dashboardFooterCompanyLogo.setColorFilter(ResourcesUtil.getColor(logoTint, null));
    }
}
