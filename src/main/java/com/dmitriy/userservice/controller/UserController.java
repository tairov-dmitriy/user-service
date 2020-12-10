package com.dmitriy.userservice.controller;

import com.dmitriy.userservice.model.User;
import com.dmitriy.userservice.service.UserService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(description = "All user REST APIs", tags = "Users")
@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;

    @ApiOperation(value = "Get user by login", response = User.class, tags = "Get")
    @GetMapping(value = "/getUser", produces = "application/json; charset=UTF-8")
    public User getUser(
            @ApiParam(name = "login", required = true, value = "User login", example = "Ivanov")
            @RequestParam(value = "login") String login) {
        return userService.findByLogin(login);
    }

    @ApiOperation(value = "Get list of all users", response = User.class, responseContainer = "List", tags = "Get")
    @GetMapping(value = "/getUsers", produces = "application/json; charset=UTF-8")
    public Iterable<User> getUsers() {
        return userService.findAll();
    }

    @ApiOperation(value = "Add new user", tags = "Create")
    @PostMapping(value = "/addUser", produces = "application/json; charset=UTF-8")
    public void addUser(
            @ApiParam(name = "user", required = true,
                    value = "JSON data of the new user. User login must be unique.")
            @RequestBody @Valid User user) {
        userService.add(user);
    }

    @ApiOperation(value = "Lock user", tags = "Locking")
    @GetMapping(value = "/lockUser", produces = "application/json; charset=UTF-8")
    public void lockUser(
            @ApiParam(name = "login", required = true, value = "Login of existing user", example = "Ivanov")
            @RequestParam(value = "login") String login) {
        userService.lockByLogin(login);
    }

    @ApiOperation(value = "Unlock user", tags = "Locking")
    @GetMapping(value = "/unlockUser", produces = "application/json; charset=UTF-8")
    public void unlockUser(
            @ApiParam(name = "login", required = true, value = "Login of existing user", example = "Ivanov")
            @RequestParam(value = "login") String login) {
        userService.unlockByLogin(login);
    }

    @ApiOperation(value = "Delete user", tags = "Delete")
    @GetMapping(value = "/deleteUser", produces = "application/json; charset=UTF-8")
    public void deleteUser(
            @ApiParam(name = "login", required = true, value = "Login of existing user", example = "Ivanov")
            @RequestParam(value = "login") String login) {
        userService.deleteByLogin(login);
    }
}
