package com.dmitriy.userservice.repository;

import com.dmitriy.userservice.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, String> {
    User findByLogin(String login);
    void deleteByLogin(String login);
}
