package com.elasalle.lamp.data.mapper.realm;

import android.support.annotation.NonNull;

import com.elasalle.lamp.data.entity.realm.AssetEntity;
import com.elasalle.lamp.data.entity.realm.ScanSetEntity;
import com.elasalle.lamp.data.mapper.Cartographer;
import com.elasalle.lamp.model.scan.Asset;
import com.elasalle.lamp.model.scan.ScanSet;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.realm.RealmList;

public class ScanSetEntityMapper extends Cartographer<ScanSetEntity, ScanSet> {

    private final AssetEntityMapper assetEntityMapper;

    @Inject
    public ScanSetEntityMapper(AssetEntityMapper assetEntityMapper) {
        this.assetEntityMapper = assetEntityMapper;
    }

    @Override
    public ScanSetEntity modelToEntity(final ScanSet model) {
        final ScanSetEntity entity = new ScanSetEntity();
        entity.id = model.getId();
        entity.customerId = model.getCustomerId();
        entity.name = model.getName();
        entity.assets = mapAssets(model.getAssets());
        entity.modifiedDate = model.getModifiedDate();
        entity.syncDate = model.getSyncDate();
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

    @Override
    public ScanSet entityToModel(final ScanSetEntity entity) {
        final ScanSet scanSet = new ScanSet();
        scanSet.setId(entity.id);
        scanSet.setCustomerId(entity.customerId);
        scanSet.setName(entity.name);
        scanSet.setModifiedDate(entity.modifiedDate);
        scanSet.setSyncDate(entity.syncDate);
        scanSet.setAssets(mapAssetEntity(entity));
        return scanSet;
    }

    @NonNull
    private List<Asset> mapAssetEntity(ScanSetEntity entity) {
        final List<Asset> assets = new ArrayList<>();
        if (entity != null && entity.assets != null) {
            for (AssetEntity assetEntity : entity.assets) {
                final Asset asset = assetEntityMapper.entityToModel(assetEntity);
                assets.add(asset);
            }
        }
        return assets;
    }
}
