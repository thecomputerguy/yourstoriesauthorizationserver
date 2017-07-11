package com.yourstories.authorizationserver.services;

import com.yourstories.authorizationserver.model.User;
import com.yourstories.authorizationserver.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.List;

@Service
public class DefaultUserService implements UserService{
    private static final SecureRandom RANDOM;
    private static final int HASHING_ROUNDS = 10;

    static
    {
        try
        {
            RANDOM = SecureRandom.getInstanceStrong();
        }
        catch(NoSuchAlgorithmException e)
        {
            throw new IllegalStateException(e);
        }
    }

    @Autowired UserRepository userRepository;

    @Override
    @Transactional
    public User loadUserByUsername(String username)
    {
        User principal = userRepository.getByUsername(username);
        // make sure the authorities and password are loaded
        principal.getAuthorities().size();
        principal.getPassword();
        return principal;
    }

    @Override
    @Transactional
    public void saveUser(User principal, String newPassword)
    {
        if(newPassword != null && newPassword.length() > 0)
        {
            String salt = BCrypt.gensalt(HASHING_ROUNDS, RANDOM);
            principal.setHashedPassword(
                    BCrypt.hashpw(newPassword, salt).getBytes()
            );
        }

        this.userRepository.save(principal);
    }

    @Override
    public List<User> getAllUsers() {
        return null;
    }

    @Override
    public User getUser(String id) {
        return null;
    }

    @Override
    public User createUser(User author) {
        return null;
    }

    @Override
    public User updateUser(User author) {
        return null;
    }

    @Override
    public void deleteUser(User author) {

    }

    @Override
    public void deleteUser(String id) {

    }
}
