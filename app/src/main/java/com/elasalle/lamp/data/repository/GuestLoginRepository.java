package com.elasalle.lamp.data.repository;

import com.elasalle.lamp.model.guest.GuestLogin;

import java.util.List;

@Repository
public interface GuestLoginRepository {
    void save(GuestLogin guestLogin);
    List<GuestLogin> findAll();
    void deleteAll();
}
