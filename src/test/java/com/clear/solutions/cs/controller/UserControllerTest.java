package com.clear.solutions.cs.controller;

import com.cs.controller.UserController;
import com.cs.dto.UserRepositoryImpl;
import com.cs.entity.User;
import com.cs.exception.user.UserNotFoundException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.awaitility.Awaitility.given;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = UserController.class)
@WebMvcTest
public class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserRepositoryImpl userRepository;

    @Autowired
    private ObjectMapper mapper;

    @SneakyThrows
    public User createUser(){
        User user = new User("user1@example.com", "User1", "Test1",
                LocalDate.of(2001, 11, 11), "Bridgewalk 1", "+380000000111");

        mapper.registerModule(new JavaTimeModule());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        String requestBody = mapper.writeValueAsString(user);

        when(userRepository.findByEmail(user.getEmail())).thenReturn(user);

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated());

        return user;
    }

    @Test
    @DisplayName(value = "Create User with Valid Data")
    @SneakyThrows
    public void createUserWithValidDataTest(){
        User user = new User("user@example.com", "User", "Test",
                LocalDate.of(2002, 7, 5), "Bridgewalk", "+38000000000");

        mapper.registerModule(new JavaTimeModule());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        String requestBody = mapper.writeValueAsString(user);

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated());

        when(userRepository.findByEmail(user.getEmail())).thenReturn(user);

        mvc.perform(get("/users/{email}", user.getEmail()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(user.getEmail()))
                .andExpect(jsonPath("$.firstName").value(user.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(user.getLastName()))
                .andExpect(jsonPath("$.birthDate").value(user.getBirthDate().toString()))
                .andExpect(jsonPath("$.address").value(user.getAddress()))
                .andExpect(jsonPath("$.phone").value(user.getPhone()));

    }

    @Test
    @DisplayName(value = "Create User with Invalid Data")
    @SneakyThrows
    public void createUserWithInvalidDataTest(){
        User user = new User("invalidUserData", "User1", "Test1",
                LocalDate.of(2001, 11, 11), "Bridgewalk 1", "+380000000111");

        mapper.registerModule(new JavaTimeModule());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        String requestBody = mapper.writeValueAsString(user);

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());


    }

    @Test
    @DisplayName("Update User with Valid Data")
    @SneakyThrows
    public void updateUserWithValidData(){
        User user = createUser();

        User updatedUser = new User("updatedUser@example.com", "updatedUser", "Test1",
                LocalDate.of(2000, 11, 11), "Bridgewalk upd", "+380000000222");

        mapper.registerModule(new JavaTimeModule());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        String requestBody = mapper.writeValueAsString(updatedUser);

        when(userRepository.findByEmail(updatedUser.getEmail())).thenReturn(updatedUser);

        mvc.perform(put("/users/{email}", user.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());

        mvc.perform(get("/users/{email}", updatedUser.getEmail()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(updatedUser.getEmail()))
                .andExpect(jsonPath("$.firstName").value(updatedUser.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(updatedUser.getLastName()))
                .andExpect(jsonPath("$.birthDate").value(updatedUser.getBirthDate().toString()))
                .andExpect(jsonPath("$.address").value(updatedUser.getAddress()))
                .andExpect(jsonPath("$.phone").value(updatedUser.getPhone()));
    }

    @Test
    @DisplayName("Update User with Invalid Data")
    @SneakyThrows
    public void updateUserWithInvalidData(){
        User user = createUser();

        User updatedUser = new User("updatedUser", "updatedUser", "Test1",
                LocalDate.of(2022, 11, 11), "Bridgewalk upd", "+380000000222");

        mapper.registerModule(new JavaTimeModule());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        String requestBody = mapper.writeValueAsString(updatedUser);

        MvcResult mvcResult = mvc.perform(put("/users/{email}", user.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andReturn();

        //add get/email test
    }

    @Test
    @DisplayName("Patch User with Valid Data")
    @SneakyThrows
    public void patchUserWithValidData(){
        User patchedUser = createUser();

        patchedUser.setBirthDate(LocalDate.of(2002, 11, 12));
        patchedUser.setFirstName("patchedUser");

        mapper.registerModule(new JavaTimeModule());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        String requestBody = mapper.writeValueAsString(patchedUser);

        mvc.perform(patch("/users/{email}", patchedUser.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());

        mvc.perform(get("/users/{email}", patchedUser.getEmail()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(patchedUser.getEmail()))
                .andExpect(jsonPath("$.firstName").value(patchedUser.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(patchedUser.getLastName()))
                .andExpect(jsonPath("$.birthDate").value(patchedUser.getBirthDate().toString()))
                .andExpect(jsonPath("$.address").value(patchedUser.getAddress()))
                .andExpect(jsonPath("$.phone").value(patchedUser.getPhone()));

    }

    @Test
    @DisplayName(value = "Find User with Valid Data")
    @SneakyThrows
    public void findUserWithValidEmailTest(){
        User user = createUser();

        mvc.perform(get("/users/{email}", user.getEmail()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(user.getEmail()))
                .andExpect(jsonPath("$.firstName").value(user.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(user.getLastName()))
                .andExpect(jsonPath("$.birthDate").value(user.getBirthDate().toString()))
                .andExpect(jsonPath("$.address").value(user.getAddress()))
                .andExpect(jsonPath("$.phone").value(user.getPhone()));

    }

    @Test //remake idk
    @DisplayName("Find Users in Birth Date Range with Valid Dates")
    @SneakyThrows
    public void findUsersInValidBirthDateRange(){
        LocalDate dateFrom = LocalDate.of(2000, 1, 1);
        LocalDate dateTo = LocalDate.of(2004, 10, 31);

        User user = createUser();

        when(userRepository.findUsersInBirthDateRange(dateFrom, dateTo)).thenReturn(List.of(user));

        mvc.perform(get("/users/in-birth-date-range")
                        .param("dateFrom", dateFrom.toString())
                        .param("dateTo", dateTo.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @DisplayName(value = "Find All Users")
    @SneakyThrows
    public void findAllTest(){
        User user = createUser();
        List<User> users = new ArrayList<>(List.of(user));

        when(userRepository.findAll()).thenReturn(users);

        MvcResult mvcResult = mvc.perform(get("/users"))
                .andExpect(status().isOk()).andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        List<User> responseUsersList = mapper.readValue(response, new TypeReference<>() {});

        assertEquals(1, responseUsersList.size());

        assertEquals(users.get(0).toString(), responseUsersList.get(0).toString());
    }

    @Test
    @DisplayName("Delete User with Valid Email")
    @SneakyThrows
    public void deleteUserWithValidEmail(){
        User user = createUser();

        MvcResult mvcResult = mvc.perform(delete("/users/{email}", user.getEmail())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();


    }


}
