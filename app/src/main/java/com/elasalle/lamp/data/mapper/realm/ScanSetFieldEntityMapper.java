package com.elasalle.lamp.data.mapper.realm;

import com.elasalle.lamp.data.entity.realm.ScanSetFieldEntity;
import com.elasalle.lamp.data.mapper.Cartographer;
import com.elasalle.lamp.scan.model.ScanSetField;

import javax.inject.Inject;

public class ScanSetFieldEntityMapper extends Cartographer<ScanSetFieldEntity, ScanSetField> {

    private final FieldEntityMapper fieldEntityMapper;

    @Inject
    public ScanSetFieldEntityMapper(FieldEntityMapper fieldEntityMapper) {
        this.fieldEntityMapper = fieldEntityMapper;
    }

    @Override
    public ScanSetFieldEntity modelToEntity(ScanSetField model) {
        ScanSetFieldEntity entity = new ScanSetFieldEntity();
        entity.field = fieldEntityMapper.modelToEntity(model.getField());
        entity.scanFieldLabel = model.getScanFieldLabel();
        entity.scanFieldValue = model.getScanFieldValue();
        entity.isCarryForward = model.isCarryForward();
        entity.isShowField = model.isShowField();
        entity.order = model.getOrder();
        entity.isSelected = model.isSelected();
        entity.id = model.getId();
        return entity;
    }

    @Override
    public ScanSetField entityToModel(ScanSetFieldEntity entity) {
        ScanSetField model = new ScanSetField(fieldEntityMapper.entityToModel(entity.field));
        model.setScanFieldLabel(entity.scanFieldLabel);
        model.setScanFieldValue(entity.scanFieldValue);
        model.setCarryForward(entity.isCarryForward);
        model.setShowField(entity.isShowField);
        model.setOrder(entity.order);
        model.setSelected(entity.isSelected);
        return model;
    }
}
