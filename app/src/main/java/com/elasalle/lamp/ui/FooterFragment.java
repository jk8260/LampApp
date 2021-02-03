package com.elasalle.lamp.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.elasalle.lamp.LampApp;
import com.elasalle.lamp.R;
import com.elasalle.lamp.model.user.User;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class FooterFragment extends Fragment {

    @BindView(R.id.footer_company_name) TextView footerCompanyName;

    private Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.footer_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);
        LampApp.userComponent().inject(this);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onResume() {
        super.onResume();
        final User user = LampApp.getSessionManager().user();
        if (user.isGuest()) {
            this.footerCompanyName.setText(getString(R.string.lasalle_solutions));
        } else {
            this.footerCompanyName.setText(user.getCompanyNameForCustomer(user.getCustomerId()));
        }
    }
}
