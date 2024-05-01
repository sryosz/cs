package com.cs.dto;

import com.cs.entity.User;

import java.time.LocalDate;
import java.util.List;

public interface UserRepository {

    User findByEmail(String email);
    List<User> findAll();
    User save(User user);
    User update(String email, User user);
    User patchUser(String email, User newUser);
    void deleteByEmail(String email);
    void deleteAll();
    List<User> findUsersInBirthDateRange(LocalDate dateFrom, LocalDate dateTo);
}
