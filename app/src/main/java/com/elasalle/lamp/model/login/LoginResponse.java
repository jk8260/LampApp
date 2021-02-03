package com.elasalle.lamp.model.login;

import com.elasalle.lamp.model.user.Customer;

import java.util.List;

public class LoginResponse {
    public String token;
    public String name;
    public String email;
    public List<Customer> customers;
    public String customerId;
}
