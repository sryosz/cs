package com.cs.controller;

import com.cs.dto.UserRepositoryImpl;
import com.cs.entity.User;
import com.cs.exception.user.UserNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController()
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserRepositoryImpl userRepository;

    @GetMapping("/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable("email") String email) {
        User user = userRepository.findByEmail(email);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping()
    public ResponseEntity<List<User>> getAll() {
        List<User> users = userRepository.findAll();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/in-birth-date-range")
    public ResponseEntity<List<User>> getUsersInBirthDateRange(@RequestParam("dateFrom")
                                               LocalDate dateFrom,
                                               @RequestParam("dateTo")
                                               LocalDate dateTo) {
        List<User> users = userRepository.findUsersInBirthDateRange(dateFrom, dateTo);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<User> createUser(@RequestBody @Valid User user) {
        User newUser = userRepository.save(user);
        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    }

    @PutMapping("/{email}")
    public ResponseEntity<User> updateEntireUser(@RequestBody @Valid User user,
                                                 @PathVariable("email") String email) {
        User updatedUser = userRepository.update(email, user);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    @PatchMapping("/{email}")
    public ResponseEntity<User> updatePartialUser(@RequestBody User user,
                                                  @PathVariable("email") String email) {
        User updatedUser = userRepository.patchUser(email, user);

        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    @DeleteMapping("/{email}")
    public ResponseEntity<String> deleteUserByEmail(@PathVariable String email) {
        userRepository.deleteByEmail(email);
        return new ResponseEntity<>("User with email " + email + " was deleted",
                HttpStatus.OK);
    }
}
