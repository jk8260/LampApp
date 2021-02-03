package com.elasalle.lamp.data.repository.impl.realm;

import com.elasalle.lamp.data.DataStoreManager;
import com.elasalle.lamp.data.entity.realm.GuestLoginEntity;
import com.elasalle.lamp.data.mapper.realm.GuestLoginEntityMapper;
import com.elasalle.lamp.data.repository.GuestLoginRepository;
import com.elasalle.lamp.model.guest.GuestLogin;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.realm.Realm;
import io.realm.RealmResults;

public class GuestLoginRepositoryImpl implements GuestLoginRepository {

    private final DataStoreManager dataStoreManager;
    private final GuestLoginEntityMapper guestLoginEntityMapper;

    @Inject
    public GuestLoginRepositoryImpl(DataStoreManager dataStoreManager, GuestLoginEntityMapper guestLoginEntityMapper) {
        this.dataStoreManager = dataStoreManager;
        this.guestLoginEntityMapper = guestLoginEntityMapper;
    }

    @Override
    public void save(GuestLogin guestLogin) {
        Realm realm = dataStoreManager.datasource();
        realm.beginTransaction();
        GuestLoginEntity guestLoginEntity = guestLoginEntityMapper.modelToEntity(guestLogin);
        realm.copyToRealmOrUpdate(guestLoginEntity);
        realm.commitTransaction();
        realm.close();
    }

    @Override
    public List<GuestLogin> findAll() {
        Realm realm = dataStoreManager.datasource();
        RealmResults<GuestLoginEntity> results = realm.where(GuestLoginEntity.class).findAll();
        List<GuestLogin> guestLoginList = new ArrayList<>();
        for (GuestLoginEntity entity : results) {
            GuestLogin guestLogin = guestLoginEntityMapper.entityToModel(entity);
            guestLoginList.add(guestLogin);
        }
        realm.close();
        return guestLoginList;
    }

    @Override
    public void deleteAll() {
        Realm realm = dataStoreManager.datasource();
        realm.beginTransaction();
        RealmResults<GuestLoginEntity> results = realm.where(GuestLoginEntity.class).findAll();
        results.deleteAllFromRealm();
        realm.commitTransaction();
        realm.close();
    }
}
