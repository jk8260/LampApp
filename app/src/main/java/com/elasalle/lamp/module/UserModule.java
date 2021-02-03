package com.elasalle.lamp.module;

import com.elasalle.lamp.LampApp;
import com.elasalle.lamp.model.user.Customer;
import com.elasalle.lamp.model.user.User;
import com.elasalle.lamp.security.TokenManager;
import com.elasalle.lamp.service.CustomerDetailsTask;
import com.elasalle.lamp.service.LogoManager;
import com.elasalle.lamp.ui.customer.ChangeCustomerAdapter;
import com.elasalle.lamp.ui.customer.ChangeCustomerManager;
import com.elasalle.lamp.search.SearchOnQueryTextListener;
import com.elasalle.lamp.ui.customer.ChangeCustomerRecyclerViewOnItemTouchListener;

import java.util.List;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;

@Module
@Singleton
public class UserModule {

    @Provides
    public User user() {
        return LampApp.getSessionManager().user();
    }

    @Provides
    public ChangeCustomerAdapter changeCustomerAdapter(User user) {
        List<Customer> customerList = user.getCustomers();
        return new ChangeCustomerAdapter(customerList, user.getCustomerId());
    }

    @Provides
    public ChangeCustomerRecyclerViewOnItemTouchListener changeCustomerRecyclerViewOnItemTouchListener() {
        return new ChangeCustomerRecyclerViewOnItemTouchListener(LampApp.getInstance().getApplicationContext());
    }

    @Provides
    public SearchOnQueryTextListener changeCustomerOnQueryTextListener() {
        return new SearchOnQueryTextListener();
    }

    @Provides
    public ChangeCustomerManager changeCustomerManager(CustomerDetailsTask task) {
        return new ChangeCustomerManager(task);
    }

    @Provides
    public TokenManager tokenManager() {
        return new TokenManager();
    }

    @Provides
    public LogoManager logoManager(OkHttpClient client) {
        return new LogoManager(client);
    }
}
