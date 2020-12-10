package com.dmitriy.userservice.service;

import com.dmitriy.userservice.model.User;

public interface UserService {
    User findByLogin(String login);
    Iterable<User> findAll();

    void add(User user);
    void lockByLogin(String login);
    void unlockByLogin(String login);
    void deleteByLogin(String login);
}
