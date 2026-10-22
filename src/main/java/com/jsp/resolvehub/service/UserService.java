package com.jsp.resolvehub.service;

import com.jsp.resolvehub.dao.UserRepository;
//import com.jsp.resolvehub.dto.AuthResponse;
//import com.jsp.resolvehub.dto.LoginRequest;
import com.jsp.resolvehub.model.User;
import com.jsp.resolvehub.exception.UserNotFoundException;
import com.jsp.resolvehub.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    public User register(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (userRepository.existsByEmail(user.getEmail())){
            throw new IllegalArgumentException("user already exist");
        }
        return userRepository.save(user);
    }

    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new IllegalArgumentException("user not found"));

        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token=jwtService.generateToken(user);
        return AuthResponse.builder()
                .token(token).build();
    }

    public User loadUserByEmail(String email) {

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("user not found "));
    }
}
