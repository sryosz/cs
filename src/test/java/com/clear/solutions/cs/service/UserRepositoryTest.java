package com.clear.solutions.cs.service;

import com.cs.dto.UserRepositoryImpl;
import com.cs.entity.User;
import com.cs.exception.user.UserNotFoundException;
import com.cs.exception.user.UserValidationException;
import com.cs.patcher.UserPatcher;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

@SpringBootTest(classes = {UserRepositoryTest.class, UserRepositoryImpl.class, UserPatcher.class})
@AutoConfigureMockMvc
public class UserRepositoryTest {

    @Autowired
    private UserRepositoryImpl userRepository;

    @BeforeEach
    public void setUp(){
        User user = new User("user@example.com", "User", "Test",
                LocalDate.of(2001, 11, 11), "Bridgewalk 1", "+380000000000");

        userRepository.save(user);
    }

    @Test
    @DisplayName("Find By Valid Email")
    void whenValidEmail_thenUserIsFound(){
        String email = "user@example.com";

        User user = userRepository.findByEmail(email);

        assertEquals(email, user.getEmail());
    }

    @Test
    @DisplayName("Find By InValid Email")
    void whenInvalidEmail_thenUserNotFoundException(){
        String email = "invalidUser@example.com";

        assertThrows(UserNotFoundException.class,
                () -> userRepository.findByEmail(email));
    }

    @Test
    @DisplayName("Find All")
    void findAllTest(){
        assertEquals(1, userRepository.findAll().size());
    }

    @Test
    @DisplayName("Create User with Valid Data")
    void whenValidUserDataCreate_thenUserIsCreated(){
        User validUser = new User("validUser@example.com", "validUser", "validTest",
                LocalDate.of(1999, 10, 5), "Bridgewalk Valid", "+380000000001");

        assertEquals(1, userRepository.findAll().size());

        userRepository.save(validUser);

        assertEquals(2, userRepository.findAll().size());

        assertEquals(validUser, userRepository.findByEmail(validUser.getEmail()));
    }

    @Test
    @DisplayName("Create User with Invalid Data")
    void whenInValidUserDataCreate_thenUserValidationException(){
        User invalidUser = new User("invalidUser@example.com", "invalidUser", "invalidTest",
                LocalDate.of(2023, 10, 5), "Bridgewalk inValid", "+380000000002"); //invalid birth date

        assertEquals(1, userRepository.findAll().size());

        assertThrows(UserValidationException.class,
                () -> userRepository.save(invalidUser));

        assertEquals(1, userRepository.findAll().size());

    }

    @Test
    @DisplayName("Update User with Valid Data")
    void whenValidUserDataUpdate_thenUserIsUpdated(){
        User updatedUser = userRepository.findByEmail("user@example.com");
        updatedUser.setEmail("validUser@example.com");
        updatedUser.setFirstName("UpdatedUser");

        assertEquals(1, userRepository.findAll().size());

        userRepository.update("user@example.com", updatedUser);

        assertEquals(1, userRepository.findAll().size());

        assertEquals(updatedUser, userRepository.findByEmail(updatedUser.getEmail()));
    }

    @Test
    @DisplayName("Update User with Invalid Data")
    void whenInValidUserDataUpdate_thenUserValidationException(){
        User invalidUpdatedUser = userRepository.findByEmail("user@example.com");
        invalidUpdatedUser.setFirstName("InvalidUser");
        invalidUpdatedUser.setBirthDate(LocalDate.of(2023, 1, 1));

        assertEquals(1, userRepository.findAll().size());

        assertThrows(UserValidationException.class,
                () -> userRepository.update("user@example.com", invalidUpdatedUser));

        assertEquals(1, userRepository.findAll().size());

    }

    @Test
    @DisplayName("Patch User with Valid Data")
    void whenValidUserDataUpdatePatch_thenUserIsUpdated(){
        User user = new User();
        user.setBirthDate(LocalDate.of(2002, 11, 12));
        user.setFirstName("patchedUser");

        assertEquals(1, userRepository.findAll().size());

        assertEquals("User",
                userRepository.findByEmail("user@example.com").getFirstName());

        userRepository.patchUser("user@example.com", user);

        user = userRepository.findByEmail("user@example.com");

        assertEquals(1, userRepository.findAll().size());

        assertEquals(user, userRepository.findByEmail(user.getEmail()));
    }

    @Test
    @DisplayName("Patch User with inValid Data")
    void whenInValidUserDataPatch_thenUserValidationException(){
        User invalidUpdatedUser = new User();
        invalidUpdatedUser.setFirstName("InvalidUser");
        invalidUpdatedUser.setBirthDate(LocalDate.of(2023, 1, 1));

        assertEquals(1, userRepository.findAll().size());

        assertEquals("User",
                userRepository.findByEmail("user@example.com").getFirstName());

        assertThrows(UserValidationException.class,
                () -> userRepository.update("user@example.com", invalidUpdatedUser));

        assertEquals(1, userRepository.findAll().size());

        assertNotEquals(invalidUpdatedUser.getFirstName(),
                userRepository.findByEmail("user@example.com"));

    }

    @Test
    @DisplayName("Delete User with Valid Email")
    void whenValidEmailDelete_thenUserIsDeleted(){
        String email = "user@example.com";

        assertEquals(1, userRepository.findAll().size());

        userRepository.deleteByEmail(email);

        assertEquals(0, userRepository.findAll().size());
    }

    @Test
    @DisplayName("Delete User with Invalid Email")
    void whenInvalidEmailDelete_thenUserNotFoundException(){
        String email = "invalidUser@example.com";

        assertThrows(UserNotFoundException.class,
                () -> userRepository.deleteByEmail(email));
    }

    @Test
    @DisplayName("Delete All")
    void deleteAllTest(){
        assertEquals(1, userRepository.findAll().size());

        userRepository.deleteAll();

        assertEquals(0, userRepository.findAll().size());
    }

    @Test
    @DisplayName("Find Users in Birth Date Range with Valid Dates")
    void whenValidBirthDates_thenUsersAreFound(){
        User user = new User("user1@example.com", "User1", "Test1",
                LocalDate.of(2005, 10, 1), "Bridgewalk 11", "+380000000001");

        userRepository.save(user);

        assertEquals(2, userRepository.findAll().size());

        LocalDate dateFrom = LocalDate.of(2002, 10, 10);
        LocalDate dateTo = LocalDate.of(2006, 10, 10);

        List<User> userList = new ArrayList<>(List.of(user));
        assertIterableEquals(userList, userRepository.findUsersInBirthDateRange(dateFrom, dateTo));
    }

    @Test
    @DisplayName("Find Users in Birth Date Range with Invalid Dates")
    void whenInValidBirthDates_thenUsersAreFound(){
        User user = new User("user1@example.com", "User1", "Test1",
                LocalDate.of(2005, 10, 1), "Bridgewalk 11", "+380000000001");

        userRepository.save(user);

        assertEquals(2, userRepository.findAll().size());

        LocalDate dateFrom = LocalDate.of(2006, 10, 10);
        LocalDate dateTo = LocalDate.of(2002, 10, 10);

        assertEquals(0,
                userRepository.findUsersInBirthDateRange(dateFrom, dateTo).size());
    }

    @AfterEach
    public void cleanUp(){
        userRepository.deleteAll();
    }


}
