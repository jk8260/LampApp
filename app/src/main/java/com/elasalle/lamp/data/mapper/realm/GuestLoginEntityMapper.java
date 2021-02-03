package com.elasalle.lamp.data.mapper.realm;

import com.elasalle.lamp.data.entity.realm.GuestLoginEntity;
import com.elasalle.lamp.data.mapper.Cartographer;
import com.elasalle.lamp.model.guest.GuestLogin;

public class GuestLoginEntityMapper extends Cartographer<GuestLoginEntity, GuestLogin> {
    @Override
    public GuestLoginEntity modelToEntity(GuestLogin model) {
        GuestLoginEntity guestLoginEntity = new GuestLoginEntity();
        guestLoginEntity.email = model.email;
        guestLoginEntity.platform = model.platform;
        guestLoginEntity.appVersion = model.appVersion;
        guestLoginEntity.timestamp = model.timestamp;
        return guestLoginEntity;
    }

    @Override
    public GuestLogin entityToModel(GuestLoginEntity entity) {
        return new GuestLogin(
                entity.email,
                entity.timestamp,
                entity.platform,
                entity.appVersion
        );
    }
}
