package com.elasalle.lamp.data.repository.impl.realm;

import com.elasalle.lamp.data.DataStoreManager;
import com.elasalle.lamp.data.entity.realm.ScanSetEntity;
import com.elasalle.lamp.data.mapper.realm.ScanSetEntityMapper;
import com.elasalle.lamp.data.repository.ScanSetRepository;
import com.elasalle.lamp.model.scan.ScanSet;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class ScanSetRepositoryImpl implements ScanSetRepository {

    private final DataStoreManager dataStoreManager;
    private final ScanSetEntityMapper scanSetEntityMapper;

    @Inject
    public ScanSetRepositoryImpl(DataStoreManager dataStoreManager, ScanSetEntityMapper scanSetEntityMapper) {
        this.dataStoreManager = dataStoreManager;
        this.scanSetEntityMapper = scanSetEntityMapper;
    }

    @Override
    public void save(ScanSet set) {
        Realm realm = dataStoreManager.datasource();
        realm.beginTransaction();
        ScanSetEntity entity = scanSetEntityMapper.modelToEntity(set);
        realm.copyToRealmOrUpdate(entity);
        realm.commitTransaction();
        realm.close();
    }

    @Override
    public List<ScanSet> findAll() {
        Realm realm = dataStoreManager.datasource();
        RealmResults<ScanSetEntity> results = realm.where(ScanSetEntity.class).findAll();
        List<ScanSet> list = new ArrayList<>();
        for (ScanSetEntity entity : results) {
            list.add(scanSetEntityMapper.entityToModel(entity));
        }
        realm.close();
        return list;
    }

    @Override
    public List<ScanSet> findAllByCustomerId(String customerId) {
        Realm realm = dataStoreManager.datasource();
        RealmResults<ScanSetEntity> results = realm.where(ScanSetEntity.class).equalTo("customerId", customerId).findAll();
        List<ScanSet> list = new ArrayList<>();
        for (ScanSetEntity entity : results) {
            list.add(scanSetEntityMapper.entityToModel(entity));
        }
        realm.close();
        return list;
    }

    @Override
    public ScanSet findById(String id) {
        Realm realm = dataStoreManager.datasource();
        realm.beginTransaction();
        RealmQuery<ScanSetEntity> query = realm.where(ScanSetEntity.class).equalTo("id", id);
        ScanSetEntity entity = query.findFirst();
        ScanSet scanSet = scanSetEntityMapper.entityToModel(entity);
        realm.commitTransaction();
        realm.close();
        return scanSet;
    }

    @Override
    public void deleteOne(ScanSet set) {
        Realm realm = dataStoreManager.datasource();
        realm.beginTransaction();
        RealmQuery<ScanSetEntity> query = realm.where(ScanSetEntity.class).equalTo("id", set.getId());
        query.findFirst().deleteFromRealm();
        realm.commitTransaction();
        realm.close();
    }

    @Override
    public void deleteAll() {
        Realm realm = dataStoreManager.sessionDatasource();
        realm.beginTransaction();
        RealmResults<ScanSetEntity> results = realm.where(ScanSetEntity.class).findAll();
        results.deleteAllFromRealm();
        realm.commitTransaction();
        realm.close();
    }
}
