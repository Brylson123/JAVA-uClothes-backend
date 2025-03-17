package com.uClothes.uClothes.service;

import com.uClothes.uClothes.domain.User;
import com.uClothes.uClothes.domain.UserRole;
import com.uClothes.uClothes.dto.ResponseUserDTO;
import com.uClothes.uClothes.repositories.UserRepository;
import com.uClothes.uClothes.security.UserLoginRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtUtilService jwtUtil;

    public UserService(JwtUtilService jwtUtil, PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    public ResponseUserDTO registerUser(User user) {
        if (this.userRepository.findByEmail(user.getEmail()) != null)
            return new ResponseUserDTO(false, "User with this email already exists.");
        user.setPassword(this.passwordEncoder.encode(user.getPassword()));
        if(user.getRole() == null){
            user.setRole(UserRole.USER);
        }
        this.userRepository.save(user);
        return new ResponseUserDTO(true, "User registered successfully.");
    }

    public ResponseUserDTO loginUser(UserLoginRequest loginRequest, HttpServletResponse response) {
        User user = this.userRepository.findByEmail(loginRequest.getEmail());
        if (user != null && this.passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            String token = this.jwtUtil.generateToken(user);
            user.setCurrentTokenId(token);
            this.userRepository.save(user);
            ResponseCookie cookie = ResponseCookie.from("jwt", token)
                    .httpOnly(true)
                    .secure(true)
                    .maxAge(3600).path("/")
                    .domain(".uclothes.pl")
                    .sameSite("none").build();
            response.setHeader("Set-Cookie", cookie.toString());
            return new ResponseUserDTO(true, user.getRole(), user.getEmail(), token);
        }
        return new ResponseUserDTO(false, "Invalid email or password.");
    }

    public User findUserByToken(String token) {
        return this.userRepository.findByCurrentTokenId(token);
    }

    public ResponseUserDTO logoutUser(String username, HttpServletResponse response) {
        try {
            User user = this.userRepository.findByEmail(username);
            if (user != null) {
                user.setCurrentTokenId(null);
                this.userRepository.save(user);
                String cookieValue = ResponseCookie.from("jwt", "")
                        .httpOnly(true)
                        .secure(true)
                        .sameSite("none")
                        .domain(".uclothes.pl")
                        .maxAge(0)
                        .path("/")
                        .build().toString();
                response.addHeader("Set-Cookie", cookieValue);
                return new ResponseUserDTO(true);
            }
            return new ResponseUserDTO(false, "User not found.");
        } catch (Exception e) {
            return new ResponseUserDTO(false, "Error during logout: " + e.getMessage());
        }
    }

    public ResponseUserDTO getUserByToken(String token) {
        User user = findUserByToken(token);
        if (user == null) {
            return new ResponseUserDTO(false, "Invalid token.");
        }
        return new ResponseUserDTO(true, user.getRole(), user.getEmail());
    }
}
