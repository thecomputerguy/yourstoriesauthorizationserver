package com.yourstories.authorizationserver.services;

import com.yourstories.authorizationserver.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Validated
public interface UserService extends UserDetailsService
{

    List<User> getAllUsers();
    User getUser(String id);
    User createUser(User author);
    User updateUser(User author);
    void deleteUser(User author);
    void deleteUser(String id);
    @Override
    User loadUserByUsername(String username);

    void saveUser(
            @NotNull(message = "{validate.authenticate.saveUser}") @Valid
                    User principal,
            String newPassword
    );
}
