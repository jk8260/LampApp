package com.elasalle.lamp.data.mapper.realm;

import android.support.annotation.NonNull;

import com.elasalle.lamp.data.entity.realm.AssetEntity;
import com.elasalle.lamp.data.entity.realm.NewScanSetEntity;
import com.elasalle.lamp.data.entity.realm.ScanSetFieldEntity;
import com.elasalle.lamp.data.mapper.Cartographer;
import com.elasalle.lamp.model.scan.Asset;
import com.elasalle.lamp.scan.model.NewScanSet;
import com.elasalle.lamp.scan.model.ScanSetField;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import io.realm.RealmList;

public class NewScanSetEntityMapper extends Cartographer<NewScanSetEntity, NewScanSet> {

    private final ScanSetFieldEntityMapper scanSetFieldEntityMapper;
    private final AssetEntityMapper assetEntityMapper;

    public NewScanSetEntityMapper(ScanSetFieldEntityMapper scanSetFieldEntityMapper, AssetEntityMapper assetEntityMapper) {
        this.scanSetFieldEntityMapper = scanSetFieldEntityMapper;
        this.assetEntityMapper = assetEntityMapper;
    }

    @Override
    public NewScanSetEntity modelToEntity(NewScanSet model) {
        NewScanSetEntity entity = new NewScanSetEntity();
        entity.id = model.getId();
        entity.customerId = model.getCustomerId();
        entity.name = model.getName();
        entity.scanSetFields = mapFields(model.getScanSetFields());
        entity.assets = mapAssets(model.getAssets());
        return entity;
    }

    @NonNull
    private RealmList<AssetEntity> mapAssets(final List<Asset> assets) {
        final RealmList<AssetEntity> list = new RealmList<>();
        if (assets != null) {
            for (final Asset asset : assets) {
                final AssetEntity entity = assetEntityMapper.modelToEntity(asset);
                list.add(entity);
            }
        }
        return list;
    }

    private RealmList<ScanSetFieldEntity> mapFields(List<ScanSetField> scanSetFields) {
        RealmList<ScanSetFieldEntity> scanSetFieldEntityRealmList = new RealmList<>();
        for (ScanSetField scanSetField : scanSetFields) {
            scanSetFieldEntityRealmList.add(scanSetFieldEntityMapper.modelToEntity(scanSetField));
        }
        return scanSetFieldEntityRealmList;
    }

    @Override
    public NewScanSet entityToModel(NewScanSetEntity entity) {
        NewScanSet model = new NewScanSet(entity.name, entity.id, entity.customerId);
        model.setScanSetFields(mapFieldsEntity(entity.scanSetFields));
        model.setAssets(mapAssetEntity(entity));
        return model;
    }


    @NonNull
    private List<Asset> mapAssetEntity(NewScanSetEntity entity) {
        final List<Asset> assets = new ArrayList<>();
        if (entity != null && entity.assets != null) {
            for (AssetEntity assetEntity : entity.assets) {
                final Asset asset = assetEntityMapper.entityToModel(assetEntity);
                assets.add(asset);
            }
        }
        return assets;
    }

    private List<ScanSetField> mapFieldsEntity(RealmList<ScanSetFieldEntity> scanSetFields) {
        List<ScanSetField> list = new LinkedList<>();
        for (ScanSetFieldEntity entity : scanSetFields) {
            list.add(scanSetFieldEntityMapper.entityToModel(entity));
        }
        return list;
    }
}
