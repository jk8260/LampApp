package com.elasalle.lamp.data.repository.impl.realm;

import com.elasalle.lamp.data.DataStoreManager;
import com.elasalle.lamp.data.entity.realm.FieldLookupListEntity;
import com.elasalle.lamp.data.mapper.realm.FieldLookupListEntityMapper;
import com.elasalle.lamp.data.repository.FieldLookupListRepository;
import com.elasalle.lamp.model.user.FieldLookupList;

import io.realm.Realm;
import io.realm.RealmQuery;

public class FieldLookupListRepositoryImpl implements FieldLookupListRepository {

    private final DataStoreManager dataStoreManager;
    private final FieldLookupListEntityMapper mapper;

    public FieldLookupListRepositoryImpl(DataStoreManager dataStoreManager, FieldLookupListEntityMapper mapper) {
        this.dataStoreManager = dataStoreManager;
        this.mapper = mapper;
    }


    @Override
    public void save(FieldLookupList fieldLookupList) {
        Realm realm = dataStoreManager.datasource();
        realm.beginTransaction();
        FieldLookupListEntity entity = mapper.modelToEntity(fieldLookupList);
        realm.copyToRealmOrUpdate(entity);
        realm.commitTransaction();
        realm.close();
    }

    @Override
    public void delete(FieldLookupList fieldLookupList) {
        Realm realm = dataStoreManager.datasource();
        realm.beginTransaction();
        RealmQuery<FieldLookupListEntity> query = realm.where(FieldLookupListEntity.class).equalTo("id", fieldLookupList.getId());
        FieldLookupListEntity entity = query.findFirst();
        entity.deleteFromRealm();
        realm.commitTransaction();
        realm.close();
    }

    @Override
    public FieldLookupList findByFieldId(String fieldId) {
        Realm realm = dataStoreManager.datasource();
        RealmQuery<FieldLookupListEntity> query = realm.where(FieldLookupListEntity.class).equalTo("id", fieldId);
        FieldLookupListEntity entity = query.findFirst();
        FieldLookupList model = mapper.entityToModel(entity);
        realm.close();
        return model;
    }
}
