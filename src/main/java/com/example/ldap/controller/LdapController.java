package com.example.ldap.controller;

import java.util.List;

import com.example.ldap.repository.User;
import com.example.ldap.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.BodyBuilder;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

/**
 * @author carlos-silva
 * @version : $<br/>
 * : $
 * @since 6/20/20 2:45 PM
 */
@RestController
@RequestMapping("/ldap")
public class LdapController {

    @Autowired
    UserService userService;

    @GetMapping
    public ResponseEntity<List<String>> list() {
        return  ResponseEntity.ok(userService.search("*"));
    }

    @PostMapping
    public ResponseEntity<Boolean> autenticate(@RequestBody User user) {
        return  ResponseEntity.ok(userService.authenticate(user));
    }

    @PostMapping("/create")
    public ResponseEntity create(@RequestBody User user) {
        userService.create(user);
        return  ResponseEntity.ok().build();
    }

}
