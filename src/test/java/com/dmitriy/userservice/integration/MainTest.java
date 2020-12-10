package com.dmitriy.userservice.integration;

import com.dmitriy.userservice.UserserviceApplication;
import com.dmitriy.userservice.model.User;
import com.dmitriy.userservice.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.validation.ConstraintViolationException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = UserserviceApplication.class)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestPropertySource(locations = "classpath:test.properties")
@Sql(scripts = "classpath:test-schema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class MainTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper mapper;

    @After
    public void resetDb() {
        userRepository.deleteAll();
    }

    @Test
    public void getUsers() throws Exception {

        resetDb();

        Calendar calendar = Calendar.getInstance();
        calendar.set(1990, Calendar.MAY, 5);
        User user = new User("User", "User name", calendar.getTime(), "+7-111-111-11-11");

        userRepository.save(user);

        String formatted = new SimpleDateFormat("dd.MM.yyyy").format(user.getBirthDate());
        mvc.perform(get("/api/getUsers")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].login", is(user.getLogin())))
                .andExpect(jsonPath("$[0].fullName", is(user.getFullName())))
                .andExpect(jsonPath("$[0].birthDate", is(formatted)))
                .andExpect(jsonPath("$[0].phone", is(user.getPhone())))
                .andExpect(jsonPath("$[0].locked", is(false)));

        mvc.perform(get("/api/getUser?login=User")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login", is(user.getLogin())))
                .andExpect(jsonPath("$.fullName", is(user.getFullName())))
                .andExpect(jsonPath("$.birthDate", is(formatted)))
                .andExpect(jsonPath("$.phone", is(user.getPhone())))
                .andExpect(jsonPath("$.locked", is(false)));
    }

    @Test
    public void getNotExistedUser() {

        resetDb();

        try {
            mvc.perform(get("/api/getUser?login=User")
                    .contentType(MediaType.APPLICATION_JSON));
            fail("Test failed");
        } catch (Exception ex) {}
    }

    @Test
    public void addUser() throws Exception {

        resetDb();

        Calendar calendar = Calendar.getInstance();
        calendar.set(1990, Calendar.MAY, 5);
        User user = new User("User", "User name", calendar.getTime(), "+7-111-111-11-11");

        userRepository.save(user);

        User newUser = new User("NewUser", "New user name", calendar.getTime(), "+7-222-222-22-22");

        mvc.perform(post("/api/addUser")
                .content(mapper.writeValueAsString(newUser))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        String formatted = new SimpleDateFormat("dd.MM.yyyy").format(user.getBirthDate());
        mvc.perform(get("/api/getUser?login=NewUser")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login", is(newUser.getLogin())))
                .andExpect(jsonPath("$.fullName", is(newUser.getFullName())))
                .andExpect(jsonPath("$.birthDate", is(formatted)))
                .andExpect(jsonPath("$.phone", is(newUser.getPhone())))
                .andExpect(jsonPath("$.locked", is(false)));
    }

    @Test
    public void addAlreadyExistedUser() {

        resetDb();

        Calendar calendar = Calendar.getInstance();
        calendar.set(1990, Calendar.MAY, 5);
        User user = new User("User", "User name", calendar.getTime(), "+7-111-111-11-11");

        userRepository.save(user);

        User newUser = new User("User", "New user name", calendar.getTime(), "+7-222-222-22-22");

        try {
            mvc.perform(post("/api/addUser")
                    .content(mapper.writeValueAsString(newUser))
                    .contentType(MediaType.APPLICATION_JSON));
            fail("Test failed");
        } catch (Exception ex) {}
    }

    @Test
    public void lockUser() throws Exception {

        resetDb();

        Calendar calendar = Calendar.getInstance();
        calendar.set(1990, Calendar.MAY, 5);
        User user = new User("User", "User name", calendar.getTime(), "+7-111-111-11-11");

        userRepository.save(user);

        mvc.perform(get("/api/getUser?login=User")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login", is(user.getLogin())))
                .andExpect(jsonPath("$.locked", is(false)));

        mvc.perform(get("/api/lockUser?login=User")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mvc.perform(get("/api/getUser?login=User")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login", is(user.getLogin())))
                .andExpect(jsonPath("$.locked", is(true)));

        mvc.perform(get("/api/unlockUser?login=User")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mvc.perform(get("/api/getUser?login=User")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login", is(user.getLogin())))
                .andExpect(jsonPath("$.locked", is(false)));
    }

    @Test
    public void lockNotExistedUser() {

        resetDb();

        try {
            mvc.perform(get("/api/lockUser?login=User")
                    .contentType(MediaType.APPLICATION_JSON));
            fail("Test failed");
        } catch (Exception ex) {}

        try {
            mvc.perform(get("/api/unlockUser?login=User")
                    .contentType(MediaType.APPLICATION_JSON));
            fail("Test failed");
        } catch (Exception ex) {}
    }

    @Test
    public void deleteUser() throws Exception {

        resetDb();

        Calendar calendar = Calendar.getInstance();
        calendar.set(1990, Calendar.MAY, 5);
        User user = new User("User", "User name", calendar.getTime(), "+7-111-111-11-11");

        userRepository.save(user);

        mvc.perform(get("/api/deleteUser?login=" + user.getLogin())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        try {
            mvc.perform(get("/api/getUser?login=User")
                    .contentType(MediaType.APPLICATION_JSON));
            fail("Test failed");
        } catch (Exception ex) {}
    }

    @Test
    public void deleteNotExistedUser() {

        resetDb();

        try {
            mvc.perform(get("/api/deleteUser?login=User")
                    .contentType(MediaType.APPLICATION_JSON));
            fail("Test failed");
        } catch (Exception ex) {}
    }

    @Test
    public void validation() throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.set(1990, Calendar.MAY, 5);
        User user = new User("", "User name", calendar.getTime(), "+7-111-111-11-11");

        mvc.perform(post("/api/addUser")
                .content(mapper.writeValueAsString(user))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());

        user = new User("QwertyuiQwertyuiQwertyuiQwertyui1", "User name", calendar.getTime(), "+7-111-111-11-11");

        mvc.perform(post("/api/addUser")
                .content(mapper.writeValueAsString(user))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());

        user = new User("User", "", calendar.getTime(), "+7-111-111-11-11");

        mvc.perform(post("/api/addUser")
                .content(mapper.writeValueAsString(user))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());

        user = new User(null, "User name", calendar.getTime(), "+7-111-111-11-11");

        mvc.perform(post("/api/addUser")
                .content(mapper.writeValueAsString(user))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());

        user = new User("User", null, calendar.getTime(), "+7-111-111-11-11");

        mvc.perform(post("/api/addUser")
                .content(mapper.writeValueAsString(user))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());

        user = new User("User", "User name", null, "+7-111-111-11-11");

        mvc.perform(post("/api/addUser")
                .content(mapper.writeValueAsString(user))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());

        calendar.set(2090, Calendar.MAY, 5);
        user = new User("User", "User name", calendar.getTime(), "+7-111-111-11-11");

        mvc.perform(post("/api/addUser")
                .content(mapper.writeValueAsString(user))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());

        user = new User("User", "", calendar.getTime(), "+7-111-1t1-11-11");

        mvc.perform(post("/api/addUser")
                .content(mapper.writeValueAsString(user))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }
}
