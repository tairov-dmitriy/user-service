package com.dmitriy.userservice.controller;

import com.dmitriy.userservice.model.User;
import com.dmitriy.userservice.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private UserService userService;

    private User newUser;

    @Before
    public void setUp() throws JsonProcessingException {
        Calendar calendar = Calendar.getInstance();
        calendar.set(1990, Calendar.MAY, 5);
        User user = new User("User", "User name", calendar.getTime(), "+7-111-111-11-11");
        List<User> list = Collections.singletonList(user);

        calendar.set(1986, Calendar.APRIL, 15);
        newUser = new User("UserLogin", "User Full Name", calendar.getTime(), "+7-222-222-22-22");

        Mockito.when(userService.findAll()).thenReturn(list);
        Mockito.when(userService.findByLogin(user.getLogin())).thenReturn(user);
    }

    @Test
    public void getUsers() throws Exception {
        mvc.perform(get("/api/getUsers")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].login", is("User")))
                .andExpect(jsonPath("$[0].fullName", is("User name")))
                .andExpect(jsonPath("$[0].birthDate", is("05.05.1990")))
                .andExpect(jsonPath("$[0].phone", is("+7-111-111-11-11")))
                .andExpect(jsonPath("$[0].locked", is(false)));

        mvc.perform(get("/api/getUser?login=User")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login", is("User")))
                .andExpect(jsonPath("$.fullName", is("User name")))
                .andExpect(jsonPath("$.birthDate", is("05.05.1990")))
                .andExpect(jsonPath("$.phone", is("+7-111-111-11-11")))
                .andExpect(jsonPath("$.locked", is(false)));
    }

    @Test
    public void addUser() throws Exception {
        mvc.perform(post("/api/addUser")
                .content(mapper.writeValueAsString(newUser))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
