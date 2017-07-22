package com.yourstories.authorizationserver.controllers;

import com.yourstories.authorizationserver.authentication.services.ActiveUserStore;
import com.yourstories.authorizationserver.services.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;

@RestController
public class UserController {

    @Autowired
    ActiveUserStore activeUserStore;

    @Autowired
    IUserService userService;

    @RequestMapping(value = "/loggedUsers", method = {RequestMethod.GET}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> getLoggedUsers(final Locale locale, final Model model) {
        return new ResponseEntity<Model>(model.addAttribute("users", activeUserStore.getUsers()), new HttpHeaders(), HttpStatus.OK);
        //return "users";
    }

    @RequestMapping(value = "/loggedUsersFromSessionRegistry", method = {RequestMethod.GET}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> getLoggedUsersFromSessionRegistry(final Locale locale, final Model model) {
        return new ResponseEntity<Model>(model.addAttribute("users", userService.getUsersFromSessionRegistry()), new HttpHeaders(), HttpStatus.OK);
        //return "users";
    }
}
