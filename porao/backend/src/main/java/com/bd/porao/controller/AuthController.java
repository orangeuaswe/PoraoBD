package com.bd.porao.controller;

import com.bd.porao.model.Role;
import com.bd.porao.model.User;
import com.bd.porao.repository.UserRepository;
import com.bd.porao.service.GoogleTokenVerifier;
import com.bd.porao.service.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController
{
    private final UserRepository users;
    private final PasswordEncoder encode;
    private final JwtService jwtService;
    private final GoogleTokenVerifier googleTokenVerifier;

    public AuthController(UserRepository users, PasswordEncoder encode, JwtService jwtService, GoogleTokenVerifier googleTokenVerifier)
    {
        this.users = users;
        this.encode = encode;
        this.jwtService = jwtService;
        this.googleTokenVerifier = googleTokenVerifier;
    }

    record RegisterReq(String email, String password, String name, String role)
    {

    }
    record LoginReq(String email, String password)
    {

    }
    record GoogleReq(String credential, String role)
    {

    }
    record AuthResponse(String token, Long userId, String email, String name, String role)
    {

    }

    @PostMapping("/register")
    public AuthResponse register(@RequestBody RegisterReq req)
    {
        if(users.findByEmail(req.email()).isPresent())
        {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Account already exists");
        }
        User user = new User();
        user.setEmail(req.email());
        user.setPasswordHash(encode.encode(req.password()));
        user.setName(req.name());
        user.setRole(Role.valueOf(req.role().toUpperCase()));
        user =  users.save(user);
        String token = jwtService.issueToken(user.getId(),user.getEmail(),user.getRole().name());
        return new AuthResponse(token,user.getId(),user.getEmail(),user.getRole().name(),user.getRole().name());
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginReq req)
    {
        User user = users.findByEmail(req.email()).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));
        if(!encode.matches(req.password(), user.getPasswordHash()))
        {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }
        String token = jwtService.issueToken(user.getId(),user.getEmail(),user.getRole().name());
        return new AuthResponse(token, user.getId(),user.getEmail(),user.getRole().name(),user.getRole().name());
    }

    @PostMapping("/google")
    public AuthResponse googleLogin(@RequestBody GoogleReq req)
    {
        try
        {
            GoogleTokenVerifier.GoogleProfile profile = googleTokenVerifier.verify(req.credential());
            String email = profile.email();
            String name = profile.name();
            Optional<User> exist = users.findByEmail(email);
            User user;
            if (exist.isPresent()) {
                user = exist.get();
            } else {
                user = new User();
                user.setEmail(email);
                user.setName(name);
                user.setRole(Role.valueOf(req.role().toUpperCase()));
                user = users.save(user);
            }
            String token = jwtService.issueToken(user.getId(), user.getEmail(), user.getRole().name());
            return new AuthResponse(token, user.getId(), user.getEmail(), user.getRole().name(), user.getRole().name());
        }
        catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid Google credentials");
        }
    }
}
