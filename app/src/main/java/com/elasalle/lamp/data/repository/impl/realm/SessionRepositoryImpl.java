package com.elasalle.lamp.data.repository.impl.realm;

import com.elasalle.lamp.data.DataStoreManager;
import com.elasalle.lamp.data.entity.realm.CustomerEntity;
import com.elasalle.lamp.data.entity.realm.UserEntity;
import com.elasalle.lamp.data.mapper.realm.CustomerEntityMapper;
import com.elasalle.lamp.data.mapper.realm.UserEntityMapper;
import com.elasalle.lamp.data.repository.SessionRepository;
import com.elasalle.lamp.model.user.Customer;
import com.elasalle.lamp.model.user.User;

import javax.inject.Inject;

import io.realm.Realm;
import io.realm.RealmResults;

public class SessionRepositoryImpl implements SessionRepository {

    private final DataStoreManager dataStoreManager;
    private final UserEntityMapper userEntityMapper;
    private final CustomerEntityMapper customerEntityMapper;

    @Inject
    public SessionRepositoryImpl(DataStoreManager dataStoreManager, UserEntityMapper userEntityMapper, CustomerEntityMapper customerEntityMapper) {
        this.dataStoreManager = dataStoreManager;
        this.userEntityMapper = userEntityMapper;
        this.customerEntityMapper = customerEntityMapper;
    }

    @Override
    public void saveUser(User user) {
        deleteUser();
        Realm realm = dataStoreManager.sessionDatasource();
        realm.beginTransaction();
        UserEntity userEntity = userEntityMapper.modelToEntity(user);
        realm.copyToRealmOrUpdate(userEntity);
        realm.commitTransaction();
        realm.close();
    }

    @Override
    public void saveCustomer(Customer customer) {
        deleteCustomer();
        Realm realm = dataStoreManager.sessionDatasource();
        realm.beginTransaction();
        CustomerEntity customerEntity = customerEntityMapper.modelToEntity(customer);
        realm.copyToRealmOrUpdate(customerEntity);
        realm.commitTransaction();
        realm.close();
    }

    @Override
    public User getUser() {
        Realm realm = dataStoreManager.sessionDatasource();
        RealmResults<UserEntity> results = realm.where(UserEntity.class).findAll();
        if (results.size() > 0) {
            UserEntity entity = results.first();
            User user = userEntityMapper.entityToModel(entity);
            realm.close();
            return user;
        } else {
            realm.close();
            return null;
        }
    }

    @Override
    public Customer getCustomer() {
        Realm realm = dataStoreManager.sessionDatasource();
        RealmResults<CustomerEntity> results = realm.where(CustomerEntity.class).findAll();
        if (results.size() > 0) {
            CustomerEntity entity = results.first();
            Customer customer = customerEntityMapper.entityToModel(entity);
            realm.close();
            return customer;
        } else {
            realm.close();
            return null;
        }
    }

    @Override
    public void clear() {
        Realm realm = dataStoreManager.sessionDatasource();
        realm.beginTransaction();
        realm.deleteAll();
        realm.commitTransaction();
        realm.close();
    }

    private void deleteUser() {
        Realm realm = dataStoreManager.sessionDatasource();
        realm.beginTransaction();
        RealmResults<UserEntity> results = realm.where(UserEntity.class).findAll();
        results.deleteAllFromRealm();
        realm.commitTransaction();
        realm.close();
    }

    private void deleteCustomer() {
        Realm realm = dataStoreManager.sessionDatasource();
        realm.beginTransaction();
        RealmResults<CustomerEntity> results = realm.where(CustomerEntity.class).findAll();
        results.deleteAllFromRealm();
        realm.commitTransaction();
        realm.close();
    }
}
