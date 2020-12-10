package com.dmitriy.userservice.repository;

import com.dmitriy.userservice.model.User;
import com.google.common.collect.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Calendar;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
@TestPropertySource(locations = "classpath:test.properties")
public class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void getUser() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(1990, Calendar.MAY, 5);
        User user = new User("User", "User name", calendar.getTime(), "+7-111-111-11-11");
        entityManager.persist(user);
        entityManager.flush();

        User found = userRepository.findByLogin(user.getLogin());
        assertThat(found.getLogin()).isEqualTo(user.getLogin());
        assertThat(found.getFullName()).isEqualTo(user.getFullName());
        assertThat(found.getBirthDate()).isEqualTo(user.getBirthDate());
        assertThat(found.getPhone()).isEqualTo(user.getPhone());
        assertThat(found.getLocked()).isEqualTo(false);

        List<User> foundAll = Lists.newArrayList(userRepository.findAll());
        assertEquals(foundAll.size(), 1);
        assertThat(foundAll.get(0).getLogin()).isEqualTo(user.getLogin());
        assertThat(foundAll.get(0).getFullName()).isEqualTo(user.getFullName());
        assertThat(foundAll.get(0).getBirthDate()).isEqualTo(user.getBirthDate());
        assertThat(foundAll.get(0).getPhone()).isEqualTo(user.getPhone());
        assertThat(foundAll.get(0).getLocked()).isEqualTo(false);
    }

    @Test
    public void addUser() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(1990, Calendar.MAY, 5);
        User user = new User("User", "User name", calendar.getTime(), "+7-111-111-11-11");

        User saved = userRepository.save(user);
        assertThat(saved).isEqualTo(user);

        User found = userRepository.findByLogin("User");
        assertThat(found.getLogin()).isEqualTo(user.getLogin());
        assertThat(found.getFullName()).isEqualTo(user.getFullName());
        assertThat(found.getBirthDate()).isEqualTo(user.getBirthDate());
        assertThat(found.getPhone()).isEqualTo(user.getPhone());
        assertThat(found.getLocked()).isEqualTo(false);
    }

    @Test
    public void lockUser() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(1990, Calendar.MAY, 5);
        User user = new User("User", "User name", calendar.getTime(), "+7-111-111-11-11");

        userRepository.save(user);

        User found = userRepository.findByLogin(user.getLogin());
        assertThat(found.getLocked()).isEqualTo(false);

        user.setLocked(true);
        userRepository.save(user);

        found = userRepository.findByLogin(user.getLogin());
        assertThat(found.getLocked()).isEqualTo(true);

        user.setLocked(false);
        userRepository.save(user);

        found = userRepository.findByLogin(user.getLogin());
        assertThat(found.getLocked()).isEqualTo(false);
    }

    @Test
    public void deleteUser() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(1990, Calendar.MAY, 5);
        User user = new User("User", "User name", calendar.getTime(), "+7-111-111-11-11");

        userRepository.save(user);

        userRepository.deleteByLogin(user.getLogin());

        User found = userRepository.findByLogin(user.getLogin());
        assertThat(found).isEqualTo(null);
        List<User> foundAll = Lists.newArrayList(userRepository.findAll());
        assertEquals(foundAll.size(), 0);
    }
}
