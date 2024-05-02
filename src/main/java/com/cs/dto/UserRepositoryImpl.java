package com.cs.dto;

import com.cs.entity.User;
import com.cs.exception.user.UserAlreadyExistsException;
import com.cs.exception.user.UserNotFoundException;
import com.cs.exception.user.UserValidationException;
import com.cs.patcher.UserPatcher;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class UserRepositoryImpl implements UserRepository{

    private final Environment env;
    private Map<String, User> users;

    private UserPatcher patcher;

    @Autowired
    public UserRepositoryImpl(Environment env, UserPatcher patcher){
         users = new HashMap<>();
         this.env = env;
         this.patcher = patcher;
    }

    @Override
    public User findByEmail(String email) {
        if(!isUserExists(email)){
            throw new UserNotFoundException("User with email " +
                    email +
                    " was not found");
        }

        return users.get(email);
    }

    @Override
    public List<User> findAll() {
        return users.values().stream().toList();
    }

    @Override
    public User save(User user){
        if(isUserExists(user.getEmail())){
            throw new UserAlreadyExistsException("User with email " +
                                                user.getEmail() +
                                                " already exists");
        }
        validateUser(user);
        users.put(user.getEmail(), user);

        return user;
    }

    @Override
    public User update(String email, User user) {
        validateUser(user);
        users.remove(email);
        users.put(user.getEmail(), user);

        return user;
    }

    @Override
    public User patchUser(String email, User newUser) {
        User existingUser = findByEmail(email);
        try{
            newUser = patcher.patch(existingUser, newUser);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e.getMessage());
        }

        users.remove(email);

        return save(newUser);
    }

    @Override
    public void deleteByEmail(String email) {
        if(!isUserExists(email)){
            throw new UserNotFoundException("User with email " +
                                            email +
                                            " was not found");
        }
        users.remove(email);
    }

    @Override
    public void deleteAll() {
        users.clear();
    }


    @Override
    public List<User> findUsersInBirthDateRange(LocalDate dateFrom, LocalDate dateTo) {
        return users.values().stream()
                .filter(user -> user.getBirthDate().isAfter(dateFrom)
                        && user.getBirthDate().isBefore(dateTo))
                .toList();
    }

    private void validateUser(User user){
        int minAge = Integer.parseInt(env.getProperty("user.properties.min-age"));

        if(user.getBirthDate().isAfter(LocalDate.now().minusDays(1))){
            throw new UserValidationException("Invalid birth date");
        }else if(ChronoUnit.YEARS.between(user.getBirthDate(), LocalDate.now()) < minAge){
            throw new UserValidationException("User must be 18 years old");
        }
    }

    public boolean isUserExists(String email){
        return users.containsKey(email);
    }
}
