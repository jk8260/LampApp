package com.elasalle.lamp.data.mapper.realm;

import com.elasalle.lamp.data.entity.realm.NotificationEntity;
import com.elasalle.lamp.data.mapper.Cartographer;
import com.elasalle.lamp.model.notification.Notification;

public class NotificationEntityMapper extends Cartographer<NotificationEntity, Notification> {

    @Override
    public NotificationEntity modelToEntity(Notification model) {
        NotificationEntity entity = new NotificationEntity();
        entity.id = model.id;
        entity.title = model.title;
        entity.createdDate = model.createdDate;
        entity.customer = model.customer;
        entity.target = model.target;
        entity.read = model.read;
        entity.deleted = model.deleted;
        entity.dateRead = model.dateRead;
        return entity;
    }

    @Override
    public Notification entityToModel(NotificationEntity entity) {
        Notification model = new Notification();
        model.id = entity.id;
        model.title = entity.title;
        model.createdDate = entity.createdDate;
        model.customer = entity.customer;
        model.target = entity.target;
        model.read = entity.read;
        model.deleted = entity.deleted;
        model.dateRead = entity.dateRead;
        return model;
    }
}
