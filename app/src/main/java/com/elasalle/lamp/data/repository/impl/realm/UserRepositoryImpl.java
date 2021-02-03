package com.elasalle.lamp.data.repository.impl.realm;

import com.elasalle.lamp.data.DataStoreManager;
import com.elasalle.lamp.data.entity.realm.UserEntity;
import com.elasalle.lamp.data.mapper.realm.UserEntityMapper;
import com.elasalle.lamp.data.repository.UserRepository;
import com.elasalle.lamp.model.user.User;

import javax.inject.Inject;

import io.realm.Realm;
import io.realm.RealmQuery;

public class UserRepositoryImpl implements UserRepository {

    private final DataStoreManager dataStoreManager;
    private final UserEntityMapper userEntityMapper;

    @Inject
    public UserRepositoryImpl(DataStoreManager dataStoreManager, UserEntityMapper userEntityMapper) {
        this.dataStoreManager = dataStoreManager;
        this.userEntityMapper = userEntityMapper;
    }

    @Override
    public void save(User user) {
        Realm realm = dataStoreManager.datasource();
        realm.beginTransaction();
        UserEntity userEntity = userEntityMapper.modelToEntity(user);
        realm.copyToRealmOrUpdate(userEntity);
        realm.commitTransaction();
        realm.close();
    }

    @Override
    public User findByUsername(String username) {
        Realm realm = dataStoreManager.datasource();
        RealmQuery<UserEntity> userEntityRealmQuery = realm.where(UserEntity.class).contains("username", username);
        UserEntity userEntity = userEntityRealmQuery.findFirst();
        User user = userEntityMapper.entityToModel(userEntity);
        realm.close();
        return user;
    }
}
