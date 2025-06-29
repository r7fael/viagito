package com.project.viagito.viagito.controller;

import com.project.viagito.viagito.model.User;
import com.project.viagito.viagito.service.JwtService;
import com.project.viagito.viagito.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthController (UserService userService, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public User registerUserController(@RequestBody User user) {
        return userService.userRegistryService(user);
    }

    @PostMapping("/login")
    public LoginResponseDTO loginResponseDTOController(@RequestBody LoginRequestDTO loginRequestDTOController) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginRequestDTOController.email(), loginRequestDTOController.password());

        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        String jwtToken = jwtService.generateToken((org.springframework.security.core.userdetails.User) authentication.getPrincipal());

        return new LoginResponseDTO(jwtToken);
    }

}
