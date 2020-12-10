package com.dmitriy.userservice.service;

import com.dmitriy.userservice.model.User;
import com.dmitriy.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    private User getUser(String login) {
        User user = userRepository.findByLogin(login);
        if (user == null)
            throw new IllegalArgumentException("User '" + login + "' not found");

        return user;
    }

    @Transactional
    @Override
    public User findByLogin(String login) {
        return getUser(login);
    }

    @Transactional
    @Override
    public Iterable<User> findAll() {
        return userRepository.findAll();
    }

    @Transactional
    @Override
    public void add(User user) {
        if (userRepository.findByLogin(user.getLogin()) != null)
            throw new IllegalArgumentException("User '" + user.getLogin() + "' already exist");

        userRepository.save(user);
    }

    @Transactional
    @Override
    public void lockByLogin(String login) {
        User user = getUser(login);
        user.setLocked(true);
        userRepository.save(user);
    }

    @Transactional
    @Override
    public void unlockByLogin(String login) {
        User user = getUser(login);
        user.setLocked(false);
        userRepository.save(user);
    }

    @Transactional
    @Override
    public void deleteByLogin(String login) {
        getUser(login);
        userRepository.deleteByLogin(login);
    }
}
