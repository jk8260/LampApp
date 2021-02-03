package com.elasalle.lamp.data.repository;

import com.elasalle.lamp.model.notification.Notification;

import java.util.List;

@Repository
public interface NotificationRepository {
    void save(Notification notification);
    List<Notification> findAllByCustomerName(String customerName);
    List<Notification> findAll();
    Notification findOneById(String id);
    void deleteOne(Notification notification);
    void deleteAll();
}
