package com.elasalle.lamp.data.repository.impl.realm;

import com.elasalle.lamp.data.DataStoreManager;
import com.elasalle.lamp.data.entity.realm.NewScanSetEntity;
import com.elasalle.lamp.data.mapper.realm.NewScanSetEntityMapper;
import com.elasalle.lamp.data.repository.NewScanSetRepository;
import com.elasalle.lamp.scan.model.NewScanSet;

import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class NewScanSetRepositoryImpl implements NewScanSetRepository {

    private final DataStoreManager dataStoreManager;
    private final NewScanSetEntityMapper mapper;

    @Inject
    public NewScanSetRepositoryImpl(DataStoreManager dataStoreManager, NewScanSetEntityMapper mapper) {
        this.dataStoreManager = dataStoreManager;
        this.mapper = mapper;
    }

    @Override
    public void save(NewScanSet newScanSet) {
        Realm realm = dataStoreManager.datasource();
        realm.beginTransaction();
        NewScanSetEntity entity = mapper.modelToEntity(newScanSet);
        realm.copyToRealmOrUpdate(entity);
        realm.commitTransaction();
        realm.close();
    }

    @Override
    public List<NewScanSet> findAll() {
        Realm realm = dataStoreManager.datasource();
        RealmResults<NewScanSetEntity> results = realm.where(NewScanSetEntity.class).findAll();
        List<NewScanSet> list = new LinkedList<>();
        for (NewScanSetEntity entity : results) {
            list.add(mapper.entityToModel(entity));
        }
        realm.close();
        return list;
    }

    @Override
    public List<NewScanSet> findAllByCustomerId(String customerId) {
        Realm realm = dataStoreManager.datasource();
        RealmResults<NewScanSetEntity> results = realm.where(NewScanSetEntity.class).equalTo("customerId", customerId).findAll();
        List<NewScanSet> list = new LinkedList<>();
        for (NewScanSetEntity entity : results) {
            list.add(mapper.entityToModel(entity));
        }
        realm.close();
        return list;
    }

    @Override
    public NewScanSet findById(String id) {
        Realm realm = dataStoreManager.datasource();
        realm.beginTransaction();
        RealmQuery<NewScanSetEntity> query = realm.where(NewScanSetEntity.class).equalTo("id", id);
        NewScanSetEntity entity = query.findFirst();
        NewScanSet newScanSet = mapper.entityToModel(entity);
        realm.commitTransaction();
        realm.close();
        return newScanSet;
    }

    @Override
    public void deleteOne(NewScanSet newScanSet) {
        Realm realm = dataStoreManager.datasource();
        realm.beginTransaction();
        RealmQuery<NewScanSetEntity> query = realm.where(NewScanSetEntity.class).equalTo("id", newScanSet.getId());
        query.findFirst().deleteFromRealm();
        realm.commitTransaction();
        realm.close();
    }

    @Override
    public void deleteAll() {
        Realm realm = dataStoreManager.sessionDatasource();
        realm.beginTransaction();
        RealmResults<NewScanSetEntity> results = realm.where(NewScanSetEntity.class).findAll();
        results.deleteAllFromRealm();
        realm.commitTransaction();
        realm.close();
    }
}
