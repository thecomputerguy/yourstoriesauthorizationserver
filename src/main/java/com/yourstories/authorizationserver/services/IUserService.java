package com.yourstories.authorizationserver.services;

import com.yourstories.authorizationserver.dto.UserDto;
import com.yourstories.authorizationserver.error.UserAlreadyExistException;
import com.yourstories.authorizationserver.model.PasswordResetToken;
import com.yourstories.authorizationserver.model.User;
import com.yourstories.authorizationserver.model.VerificationToken;

import java.io.UnsupportedEncodingException;
import java.util.List;

public interface IUserService {

    User registerNewUserAccount(UserDto accountDto) throws UserAlreadyExistException;

    User getUser(String verificationToken);

    void saveRegisteredUser(User user);

    void deleteUser(User user);

    void createVerificationTokenForUser(User user, String token);

    VerificationToken getVerificationToken(String VerificationToken);

    VerificationToken generateNewVerificationToken(String token);

    void createPasswordResetTokenForUser(User user, String token);

    User findUserByEmail(String email);

    PasswordResetToken getPasswordResetToken(String token);

    User getUserByPasswordResetToken(String token);

    User getUserByID(long id);

    void changeUserPassword(User user, String password);

    boolean checkIfValidOldPassword(User user, String password);

    String validateVerificationToken(String token);

    String generateQRUrl(User user) throws UnsupportedEncodingException;

    User updateUser2FA(boolean use2FA);

    List<String> getUsersFromSessionRegistry();

}
