package com.elasalle.lamp.data.mapper.realm;

import com.elasalle.lamp.data.entity.realm.AssetEntity;
import com.elasalle.lamp.data.entity.realm.FieldEntity;
import com.elasalle.lamp.data.entity.realm.RealmKeyValuePair;
import com.elasalle.lamp.data.entity.realm.RealmMap;
import com.elasalle.lamp.data.mapper.Cartographer;
import com.elasalle.lamp.model.scan.Asset;
import com.elasalle.lamp.model.user.Field;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.realm.RealmList;

public class AssetEntityMapper extends Cartographer<AssetEntity, Asset> {

    private final FieldEntityMapper fieldEntityMapper;

    @Inject
    public AssetEntityMapper(FieldEntityMapper fieldEntityMapper) {
        this.fieldEntityMapper = fieldEntityMapper;
    }

    @Override
    public AssetEntity modelToEntity(Asset model) {
        final AssetEntity entity = new AssetEntity();
        entity.id = model.getId();
        entity.fields = new RealmList<>();
        for (final Field field : model.getFields()) {
            entity.fields.add(fieldEntityMapper.modelToEntity(field));
        }
        entity.fieldIdValueMap = mapFieldIdValueMap(model.getFieldIdValueMap(), model);
        return entity;
    }

    private RealmMap mapFieldIdValueMap(Map<String, String> fieldIdValueMap, Asset asset) {
        RealmMap realmMap = new RealmMap();
        realmMap.id = asset.getId();
        realmMap.map = new RealmList<>();
        for (Map.Entry<String, String> entry : fieldIdValueMap.entrySet()) {
            RealmKeyValuePair keyValuePair = new RealmKeyValuePair(entry.getKey(), entry.getValue());
            realmMap.map.add(keyValuePair);
        }
        return realmMap;
    }

    @Override
    public Asset entityToModel(AssetEntity entity) {
        final Asset asset = new Asset();
        asset.setId(entity.id);
        final List<Field> fields = new ArrayList<>();
        for (final FieldEntity fieldEntity : entity.fields) {
            final Field field = fieldEntityMapper.entityToModel(fieldEntity);
            fields.add(field);
        }
        asset.setFields(fields);
        asset.setFieldIdValueMap(mapFieldIdValueMapEntity(entity.fieldIdValueMap));
        return asset;
    }

    private Map<String, String> mapFieldIdValueMapEntity(RealmMap fieldIdValueMap) {
        final Map<String, String> map = new HashMap<>();
        for (RealmKeyValuePair keyValuePair : fieldIdValueMap.map) {
            map.put(keyValuePair.key, keyValuePair.value);
        }
        return map;
    }

}
