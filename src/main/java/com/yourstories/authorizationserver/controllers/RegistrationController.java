package com.yourstories.authorizationserver.controllers;

import com.yourstories.authorizationserver.authentication.services.ISecurityUserService;
import com.yourstories.authorizationserver.dto.PasswordDto;
import com.yourstories.authorizationserver.dto.UserDto;
import com.yourstories.authorizationserver.error.InvalidOldPasswordException;
import com.yourstories.authorizationserver.model.User;
import com.yourstories.authorizationserver.model.VerificationToken;
import com.yourstories.authorizationserver.registration.events.OnRegistrationCompleteEvent;
import com.yourstories.authorizationserver.response.GenericResponse;
import com.yourstories.authorizationserver.services.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.util.Locale;
import java.util.UUID;

@RestController
public class RegistrationController {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Autowired
    private IUserService userService;

    @Autowired
    private ISecurityUserService securityUserService;

    @Autowired
    private MessageSource messages;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private Environment env;

    @Value("${http.connector:\"http://\"}")
    String connector;

    public RegistrationController() {
        super();
    }

    // Registration

    @RequestMapping(value = "/user/registration", method = {RequestMethod.POST}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> registerUserAccount(@Valid @RequestBody final UserDto accountDto, final HttpServletRequest request) {
        LOGGER.debug("Registering user account with information: {}", accountDto);

        final User registered = userService.registerNewUserAccount(accountDto);
        eventPublisher.publishEvent(new OnRegistrationCompleteEvent(registered, request.getLocale(), getAppUrl(request)));
        HttpHeaders headers = new HttpHeaders();
        return new ResponseEntity<Object>(new GenericResponse("success"),headers, HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/registrationConfirm", method = {RequestMethod.GET}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> confirmRegistration(final Locale locale, final Model model, @RequestParam("token") final String token) throws UnsupportedEncodingException {
        final String result = userService.validateVerificationToken(token);
        if (result.equals("valid")) {
            final User user = userService.getUser(token);
            System.out.println(user);
            if (user.isUsing2FA()) {
                return new ResponseEntity<Model>(model.addAttribute("qr", userService.generateQRUrl(user)),new HttpHeaders(),HttpStatus.OK);
                //return "redirect:/qrcode.html?lang=" + locale.getLanguage();
            }
            return new ResponseEntity<Model>(model.addAttribute("message", messages.getMessage("message.accountVerified", null, locale)),new HttpHeaders(), HttpStatus.OK);
            //return "redirect:/login?lang=" + locale.getLanguage();
        }

        model.addAttribute("message", messages.getMessage("auth.message." + result, null, locale));
        model.addAttribute("expired", "expired".equals(result));
        model.addAttribute("token", token);
        return new ResponseEntity<Model>(model, new HttpHeaders(), HttpStatus.CONFLICT);
        //return "redirect:/badUser.html?lang=" + locale.getLanguage();
    }

    // user activation - verification

    @RequestMapping(value = "/user/resendRegistrationToken", method = {RequestMethod.GET}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> resendRegistrationToken(final HttpServletRequest request, @RequestParam("token") final String existingToken) {
        final VerificationToken newToken = userService.generateNewVerificationToken(existingToken);
        final User user = userService.getUser(newToken.getToken());
        mailSender.send(constructResendVerificationTokenEmail(getAppUrl(request), request.getLocale(), newToken, user));
        return new ResponseEntity<GenericResponse>(new GenericResponse(messages.getMessage("message.resendToken", null, request.getLocale())), new HttpHeaders(), HttpStatus.OK);
    }

    // Reset password
    @RequestMapping(value = "/user/resetPassword", method = {RequestMethod.POST}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> resetPassword(final HttpServletRequest request, @RequestParam("email") final String userEmail) {
        final User user = userService.findUserByEmail(userEmail);
        if (user != null) {
            final String token = UUID.randomUUID()
                .toString();
            userService.createPasswordResetTokenForUser(user, token);
            mailSender.send(constructResetTokenEmail(getAppUrl(request), request.getLocale(), token, user));
        }
        return new ResponseEntity<GenericResponse>(new GenericResponse(messages.getMessage("message.resetPasswordEmail", null, request.getLocale())), new HttpHeaders(), HttpStatus.CREATED);
    }

    @RequestMapping(value = "/user/changePassword", method = {RequestMethod.GET}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> showChangePasswordPage(final Locale locale, final Model model, @RequestParam("id") final long id, @RequestParam("token") final String token) {
        final String result = securityUserService.validatePasswordResetToken(id, token);
        if (result != null) {
            model.addAttribute("message", messages.getMessage("auth.message." + result, null, locale));
            return new ResponseEntity<Model>(model, new HttpHeaders(), HttpStatus.OK);
            //return "redirect:/login?lang=" + locale.getLanguage();
        }
        return new ResponseEntity<GenericResponse>(new GenericResponse("Failed to update password") ,new HttpHeaders(), HttpStatus.CONFLICT);
        //return "redirect:/updatePassword.html?lang=" + locale.getLanguage();
    }

    @RequestMapping(value = "/user/savePassword", method = {RequestMethod.POST}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> savePassword(final Locale locale, @Valid PasswordDto passwordDto) {
        final User user = (User) SecurityContextHolder.getContext()
            .getAuthentication()
            .getPrincipal();
        userService.changeUserPassword(user, passwordDto.getNewPassword());
        return new ResponseEntity<GenericResponse>(new GenericResponse(messages.getMessage("message.resetPasswordSuc", null, locale)), new HttpHeaders(), HttpStatus.OK);
    }

    // change user password
    @RequestMapping(value = "/user/updatePassword", method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> changeUserPassword(final Locale locale, @Valid PasswordDto passwordDto) {
        final User user = userService.findUserByEmail(((User) SecurityContextHolder.getContext()
            .getAuthentication()
            .getPrincipal()).getEmail());
        if (!userService.checkIfValidOldPassword(user, passwordDto.getOldPassword())) {
            throw new InvalidOldPasswordException();
        }
        userService.changeUserPassword(user, passwordDto.getNewPassword());
        return new ResponseEntity<GenericResponse>(new GenericResponse(messages.getMessage("message.updatePasswordSuc", null, locale)), new HttpHeaders(), HttpStatus.OK);
    }

    @RequestMapping(value = "/user/update/2fa", method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> modifyUser2FA(@RequestParam("use2FA") final boolean use2FA) throws UnsupportedEncodingException {
        final User user = userService.updateUser2FA(use2FA);
        if (use2FA) {
            return new ResponseEntity<GenericResponse>(new GenericResponse(userService.generateQRUrl(user)), new HttpHeaders(), HttpStatus.OK);
        }
        return null;
    }

    // ============== NON-API ============

    private SimpleMailMessage constructResendVerificationTokenEmail(final String contextPath, final Locale locale, final VerificationToken newToken, final User user) {
        final String confirmationUrl = contextPath + "/registrationConfirm?token=" + newToken.getToken();
        final String message = messages.getMessage("message.resendToken", null, locale);
        return constructEmail("Resend Registration Token", message + " \r\n" + confirmationUrl, user);
    }

    private SimpleMailMessage constructResetTokenEmail(final String contextPath, final Locale locale, final String token, final User user) {
        final String url = contextPath + "/user/changePassword?id=" + user.getId() + "&token=" + token;
        final String message = messages.getMessage("message.resetPassword", null, locale);
        return constructEmail("Reset Password", message + " \r\n" + url, user);
    }

    private SimpleMailMessage constructEmail(String subject, String body, User user) {
        final SimpleMailMessage email = new SimpleMailMessage();
        email.setSubject(subject);
        email.setText(body);
        email.setTo(user.getEmail());
        email.setFrom(env.getProperty("support.email"));
        return email;
    }

    private String getAppUrl(HttpServletRequest request) {
        return connector + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    }

}
