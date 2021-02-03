package com.elasalle.lamp.login;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.elasalle.lamp.LampApp;
import com.elasalle.lamp.R;
import com.elasalle.lamp.data.repository.GuestLoginRepository;
import com.elasalle.lamp.model.guest.GuestLogin;
import com.elasalle.lamp.model.user.Customer;
import com.elasalle.lamp.model.user.Field;
import com.elasalle.lamp.model.user.SearchType;
import com.elasalle.lamp.model.user.User;
import com.elasalle.lamp.service.LampTask;
import com.elasalle.lamp.service.UserConfigService;
import com.elasalle.lamp.util.ResourcesUtil;

import java.util.ArrayList;
import java.util.UUID;

import javax.inject.Inject;

public class GuestLoginManager {

    private final GuestLoginRepository guestLoginRepository;

    @Inject
    public GuestLoginManager(GuestLoginRepository guestLoginRepository) {
        this.guestLoginRepository = guestLoginRepository;
    }

    public void guestLogin(final GuestLogin guestLogin) {
        startGuestSession(guestLogin.email);
        storeGuestData(guestLogin);
        startGuestTask();
    }

    private void startGuestTask() {
        final Context context = LampApp.getInstance();
        final Intent intent = new Intent(context, UserConfigService.class);
        intent.setAction(LampTask.INTENT_ACTION)
                .putExtra(LampTask.EXTRA_NAME, new String[] {LampTask.Type.GUEST.name()});
        context.startService(intent);
    }

    private void startGuestSession(String email) {
        User guest = new User(email);
        Customer guestCustomer = getGuestCustomer(email);
        guest.getCustomers().add(guestCustomer);
        LampApp.getSessionManager().startSession(guest);
        LampApp.getSessionManager().setCustomerDetails(guestCustomer);
    }

    @NonNull
    private Customer getGuestCustomer(String email) {
        Customer guestCustomer = new Customer();
        guestCustomer.setName(email);
        guestCustomer.setCustomerId(email);
        guestCustomer.setLogoUrl("");
        guestCustomer.setPrivileges(new ArrayList<String>());
        guestCustomer.setSearchTypes(new ArrayList<SearchType>());
        guestCustomer.setFields(new ArrayList<Field>());
        guestCustomer.setToken("");
        setDefaultFieldsForGuest(guestCustomer);
        return guestCustomer;
    }

    private void setDefaultFieldsForGuest(Customer customer) {
        String[] fieldNames = ResourcesUtil.getResources().getStringArray(R.array.guest_fields);
        int index = 0;
        for (String fieldName : fieldNames) {
            Field field = new Field();
            field.setName(fieldName);
            field.setRequired(index++ == 0);
            field.setFieldId(UUID.randomUUID().toString());
            customer.getFields().add(field);
        }
    }

    private void storeGuestData(GuestLogin guestLogin) {
        guestLoginRepository.save(guestLogin);
    }

}
