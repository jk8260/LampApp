package com.elasalle.lamp.data.mapper.realm;

import com.elasalle.lamp.data.entity.realm.FieldLookupListEntity;
import com.elasalle.lamp.data.entity.realm.LookupListItemEntity;
import com.elasalle.lamp.data.mapper.Cartographer;
import com.elasalle.lamp.model.user.Field;
import com.elasalle.lamp.model.user.FieldLookupList;
import com.elasalle.lamp.model.user.LookupListItem;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmList;

public class FieldLookupListEntityMapper extends Cartographer<FieldLookupListEntity, FieldLookupList> {

    private final FieldEntityMapper fieldEntityMapper;
    private final LookupLIstItemEntityMapper lookupLIstItemEntityMapper;

    public FieldLookupListEntityMapper(FieldEntityMapper fieldEntityMapper, LookupLIstItemEntityMapper lookupLIstItemEntityMapper) {
        this.fieldEntityMapper = fieldEntityMapper;
        this.lookupLIstItemEntityMapper = lookupLIstItemEntityMapper;
    }

    @Override
    public FieldLookupListEntity modelToEntity(FieldLookupList model) {
        FieldLookupListEntity entity = new FieldLookupListEntity();
        entity.id = model.getId();
        entity.field = fieldEntityMapper.modelToEntity(model.getField());
        entity.lookupListItems = mapLookupListItemsFromModel(model.getLookupListItems());
        return entity;
    }

    private RealmList<LookupListItemEntity> mapLookupListItemsFromModel(List<LookupListItem> lookupListItems) {
        RealmList<LookupListItemEntity> realmList = new RealmList<>();
        for (LookupListItem item : lookupListItems) {
            realmList.add(lookupLIstItemEntityMapper.modelToEntity(item));
        }
        return realmList;
    }

    @Override
    public FieldLookupList entityToModel(FieldLookupListEntity entity) {
        Field field = fieldEntityMapper.entityToModel(entity.field);
        FieldLookupList model = new FieldLookupList(field);
        model.setLookupListItems(mapLookupListItemsFromEntity(entity.lookupListItems));
        return model;
    }

    private List<LookupListItem> mapLookupListItemsFromEntity(RealmList<LookupListItemEntity> lookupListItems) {
        List<LookupListItem> list = new ArrayList<>();
        for (LookupListItemEntity itemEntity : lookupListItems) {
            list.add(lookupLIstItemEntityMapper.entityToModel(itemEntity));
        }
        return list;
    }
}
