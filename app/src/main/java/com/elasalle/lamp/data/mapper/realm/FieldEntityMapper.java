package com.elasalle.lamp.data.mapper.realm;

import com.elasalle.lamp.data.entity.realm.FieldEntity;
import com.elasalle.lamp.data.entity.realm.RealmString;
import com.elasalle.lamp.data.mapper.Cartographer;
import com.elasalle.lamp.model.user.Field;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmList;

public class FieldEntityMapper extends Cartographer<FieldEntity, Field> {

    @Override
    public FieldEntity modelToEntity(Field model) {
        FieldEntity entity = new FieldEntity();
        entity.fieldId = model.getFieldId();
        entity.name = model.getName();
        entity.type = model.getType();
        entity.options = mapOptions(model.getOptions());
        entity.isRequired = model.isRequired();
        entity.target = model.getTarget();
        entity.lastUpdate = model.getLastUpdate();
        return entity;
    }

    @Override
    public Field entityToModel(FieldEntity entity) {
        Field field = new Field();
        field.setFieldId(entity.fieldId);
        field.setName(entity.name);
        field.setType(entity.type);
        field.setOptions(mapOptionsEntity(entity.options));
        field.setRequired(entity.isRequired);
        field.setTarget(entity.target);
        field.setLastUpdate(entity.lastUpdate);
        return field;
    }

    private RealmList<RealmString> mapOptions(List<Object> options) {
        RealmList<RealmString> realmStrings = new RealmList<>();
        for (Object option : options) {
            realmStrings.add(new RealmString((String) option));
        }
        return realmStrings;
    }

    private List<Object> mapOptionsEntity(RealmList<RealmString> optionsEntity) {
        List<Object> objects = new ArrayList<>();
        for (RealmString string : optionsEntity) {
            objects.add(string.value);
        }
        return objects;
    }

}
