package com.yourstories.authorizationserver.controllers;

import com.yourstories.authorizationserver.captcha.ICaptchaService;
import com.yourstories.authorizationserver.dto.UserDto;
import com.yourstories.authorizationserver.model.User;
import com.yourstories.authorizationserver.registration.events.OnRegistrationCompleteEvent;
import com.yourstories.authorizationserver.response.GenericResponse;
import com.yourstories.authorizationserver.services.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
public class RegistrationCaptchaController {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Autowired
    private IUserService userService;

    @Autowired
    private ICaptchaService captchaService;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Value("${http.connector:\"http://\"}")
    String connector;

    public RegistrationCaptchaController() {
        super();
    }

    // Registration

    @RequestMapping(value = "/user/registrationCaptcha", method = {RequestMethod.POST}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> captchaRegisterUserAccount(@Valid final UserDto accountDto, final HttpServletRequest request) {

        final String response = request.getParameter("g-recaptcha-response");
        captchaService.processResponse(response);

        LOGGER.debug("Registering user account with information: {}", accountDto);

        final User registered = userService.registerNewUserAccount(accountDto);
        eventPublisher.publishEvent(new OnRegistrationCompleteEvent(registered, request.getLocale(), getAppUrl(request)));
        return new ResponseEntity<GenericResponse>(new GenericResponse("success"), new HttpHeaders(), HttpStatus.OK);
    }

    private String getAppUrl(HttpServletRequest request) {
        return connector + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    }

}
