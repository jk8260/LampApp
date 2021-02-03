package com.elasalle.lamp.data.mapper.realm;

import com.elasalle.lamp.data.entity.realm.LookupListItemEntity;
import com.elasalle.lamp.data.mapper.Cartographer;
import com.elasalle.lamp.model.user.LookupListItem;

public class LookupLIstItemEntityMapper extends Cartographer<LookupListItemEntity, LookupListItem> {

    @Override
    public LookupListItemEntity modelToEntity(LookupListItem model) {
        LookupListItemEntity entity = new LookupListItemEntity();
        entity.id = model.getId();
        entity.value = model.getValue();
        return entity;
    }

    @Override
    public LookupListItem entityToModel(LookupListItemEntity entity) {
        LookupListItem model = new LookupListItem();
        model.setId(entity.id);
        model.setValue(entity.value);
        return model;
    }

}
