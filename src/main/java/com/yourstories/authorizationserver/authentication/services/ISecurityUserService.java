package com.yourstories.authorizationserver.authentication.services;

public interface ISecurityUserService {

    String validatePasswordResetToken(long id, String token);

}
