package com.elasalle.lamp.data.mapper.realm;

import com.elasalle.lamp.data.entity.realm.CustomerEntity;
import com.elasalle.lamp.data.entity.realm.UserEntity;
import com.elasalle.lamp.data.mapper.Cartographer;
import com.elasalle.lamp.model.user.Customer;
import com.elasalle.lamp.model.user.User;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.realm.RealmList;

public class UserEntityMapper extends Cartographer<UserEntity, User> {

    private final CustomerEntityMapper customerEntityMapper;

    @Inject
    public UserEntityMapper(CustomerEntityMapper customerEntityMapper) {
        this.customerEntityMapper = customerEntityMapper;
    }

    @Override
    public UserEntity modelToEntity(User model) {
        UserEntity userEntity = new UserEntity();
        userEntity.customerId = model.getCustomerId();
        userEntity.email = model.getEmail();
        userEntity.username = model.getUsername();
        userEntity.name = model.getName();
        userEntity.guest = model.isGuest();
        userEntity.customers = mapCustomers(model.getCustomers());
        return userEntity;
    }

    private RealmList<CustomerEntity> mapCustomers(List<Customer> customers) {
        final RealmList<CustomerEntity> customerEntities = new RealmList<>();
        for (Customer customer : customers) {
            CustomerEntity customerEntity = customerEntityMapper.modelToEntity(customer);
            customerEntities.add(customerEntity);
        }
        return customerEntities;
    }

    @Override
    public User entityToModel(UserEntity entity) {
        return new User(
                entity.email,
                entity.name,
                entity.username,
                entity.customerId,
                mapCustomerEntities(entity.customers),
                entity.guest
        );
    }

    private List<Customer> mapCustomerEntities(List<CustomerEntity> entities) {
        List<Customer> customerList = new ArrayList<>();
        for (CustomerEntity entity : entities) {
            Customer customer = customerEntityMapper.entityToModel(entity);
            customerList.add(customer);
        }
        return customerList;
    }
}
