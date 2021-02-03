package com.elasalle.lamp.data.repository;

import com.elasalle.lamp.model.user.User;

@Repository
public interface UserRepository {
    void save(User user);
    User findByUsername(String username);
}