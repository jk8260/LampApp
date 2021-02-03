package com.elasalle.lamp.model.user;

import com.elasalle.lamp.model.login.LoginResponse;

import java.util.ArrayList;
import java.util.List;

public class User {

    private final String email;
    private final String name;
    private final String username;
    private final String customerId;
    private final List<Customer> customers;
    private final boolean guest;

    public User(String guestEmail) {
        this.email = guestEmail;
        this.guest = true;
        this.customerId = guestEmail;
        this.name = "";
        this.username = "";
        this.customers = new ArrayList<>();
    }

    public User(LoginResponse response, String username) {
        this.email = response.email;
        this.guest = false;
        this.name =  response.name;
        this.username = username;
        this.customerId = response.customerId;
        this.customers = response.customers;
    }

    public User(String email, String name, String username, String customerId, List<Customer> customers, boolean guest) {
        this.email = email;
        this.name = name;
        this.username = username;
        this.customerId = customerId;
        this.customers = customers;
        this.guest = guest;
    }

    public String getEmail() {
        return email;
    }

    public boolean isGuest() {
        return guest;
    }

    public String getCustomerId() {
        return customerId;
    }

    public List<Customer> getCustomers() {
        return customers;
    }

    public String getCompanyNameForCustomer(String customerId) {
        String name = "";
        for(Customer customer : customers) {
            if (customer.getCustomerId().equals(customerId)) {
                name = customer.getName();
                break;
            }
        }
        return name;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public User changeCustomer(final String newCustomerId) {
        return new User(
                this.email,
                this.name,
                this.username,
                newCustomerId,
                this.customers,
                this.guest);
    }
}
