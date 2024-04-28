package com.cs.patcher;

import com.cs.entity.User;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

@Component
public class UserPatcher {
    public User patch(User existingUser, User newUser) throws IllegalAccessException {
        Class<?> userClass = User.class;
        Field[] userFields = userClass.getDeclaredFields();

        for(Field field : userFields){
            field.setAccessible(true); // open the access in case they are private

            Object value = field.get(newUser);
            if(value!=null){
                field.set(existingUser, value);
            }
            field.setAccessible(false); // close the access
        }

        return existingUser;
    }
}
