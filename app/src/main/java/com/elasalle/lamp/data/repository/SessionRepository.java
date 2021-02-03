package com.elasalle.lamp.data.repository;

import com.elasalle.lamp.model.user.Customer;
import com.elasalle.lamp.model.user.User;

@Repository
public interface SessionRepository {
    void saveUser(User user);
    void saveCustomer(Customer customer);
    User getUser();
    Customer getCustomer();
    void clear();
}
