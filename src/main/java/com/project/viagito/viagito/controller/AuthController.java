package com.project.viagito.viagito.controller;

import com.project.viagito.viagito.model.User;
import com.project.viagito.viagito.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;

    public AuthController (UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public User registerUserController(@RequestBody User user) {
        return userService.userRegistryService(user);
    }
}
