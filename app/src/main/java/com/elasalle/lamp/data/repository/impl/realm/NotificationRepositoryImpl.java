package com.elasalle.lamp.data.repository.impl.realm;

import com.elasalle.lamp.data.DataStoreManager;
import com.elasalle.lamp.data.entity.realm.NotificationEntity;
import com.elasalle.lamp.data.mapper.realm.NotificationEntityMapper;
import com.elasalle.lamp.data.repository.NotificationRepository;
import com.elasalle.lamp.model.notification.Notification;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class NotificationRepositoryImpl implements NotificationRepository {

    private final DataStoreManager dataStoreManager;
    private final NotificationEntityMapper mapper;

    public NotificationRepositoryImpl(DataStoreManager dataStoreManager, NotificationEntityMapper mapper) {
        this.dataStoreManager = dataStoreManager;
        this.mapper = mapper;
    }

    @Override
    public void save(Notification notification) {
        Realm realm = dataStoreManager.datasource();
        realm.beginTransaction();
        NotificationEntity entity = mapper.modelToEntity(notification);
        realm.copyToRealmOrUpdate(entity);
        realm.commitTransaction();
        realm.close();
    }

    @Override
    public List<Notification> findAllByCustomerName(String customerName) {
        Realm realm = dataStoreManager.datasource();
        RealmResults<NotificationEntity> results = realm.where(NotificationEntity.class).equalTo("customer", customerName).findAll();
        List<Notification> list = new ArrayList<>();
        for (NotificationEntity entity : results) {
            list.add(mapper.entityToModel(entity));
        }
        realm.close();
        return list;
    }

    @Override
    public List<Notification> findAll() {
        Realm realm = dataStoreManager.datasource();
        RealmResults<NotificationEntity> results = realm.where(NotificationEntity.class).findAll();
        List<Notification> list = new ArrayList<>();
        for (NotificationEntity entity : results) {
            list.add(mapper.entityToModel(entity));
        }
        realm.close();
        return list;
    }

    @Override
    public Notification findOneById(String id) {
        Realm realm = dataStoreManager.datasource();
        realm.beginTransaction();
        RealmQuery<NotificationEntity> query = realm.where(NotificationEntity.class).equalTo("id", id);
        NotificationEntity entity = query.findFirst();
        Notification notification = mapper.entityToModel(entity);
        realm.commitTransaction();
        realm.close();
        return notification;
    }

    @Override
    public void deleteOne(Notification notification) {
        Realm realm = dataStoreManager.datasource();
        realm.beginTransaction();
        RealmQuery<NotificationEntity> query = realm.where(NotificationEntity.class).equalTo("id", notification.id);
        query.findFirst().deleteFromRealm();
        realm.commitTransaction();
        realm.close();
    }

    @Override
    public void deleteAll() {
        Realm realm = dataStoreManager.sessionDatasource();
        realm.beginTransaction();
        RealmResults<NotificationEntity> results = realm.where(NotificationEntity.class).findAll();
        results.deleteAllFromRealm();
        realm.commitTransaction();
        realm.close();
    }
}
