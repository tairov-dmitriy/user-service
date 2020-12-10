package com.dmitriy.userservice.service;

import com.dmitriy.userservice.model.User;
import com.dmitriy.userservice.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

@RunWith(SpringRunner.class)
public class UserServiceTest {

    @TestConfiguration
    static class UserServiceImplTestContextConfiguration {

        @Bean
        public UserService userService() {
            return new UserServiceImpl();
        }
    }

    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    private User testUser;
    private List<User> testList;

    @Before
    public void setUp() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(1990, Calendar.MAY, 5);
        testUser = new User("User", "User name", calendar.getTime(), "+7-111-111-11-11");
        testList = Collections.singletonList(testUser);

        Mockito.when(userRepository.findByLogin(testUser.getLogin())).thenReturn(testUser);
        Mockito.when(userRepository.findByLogin("NotExist")).thenReturn(null);
        Mockito.when(userRepository.findAll()).thenReturn(testList);
    }

    @Test
    public void getUsers() {
        User user = userService.findByLogin("User");
        assertThat(user).isEqualTo(testUser);

        String exceptionMessage = "";
        try {
            userService.findByLogin("NotExist");
            fail("userService.findByLogin(\"NotExist\") failed");
        } catch (Exception ex) {
            exceptionMessage = ex.getMessage();
        }

        assertThat(exceptionMessage).isEqualTo("User 'NotExist' not found");

        Iterable<User> list = userService.findAll();
        assertThat(list).isEqualTo(testList);
    }
}
