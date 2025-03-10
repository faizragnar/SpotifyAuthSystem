package org.example.rigwareauth.service;

import org.example.rigwareauth.dto.AuthUserDTO;
import org.example.rigwareauth.dto.LoginDTO;
import org.example.rigwareauth.model.AuthUser;
import org.example.rigwareauth.repository.AuthUserRepository;
import org.example.rigwareauth.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthenticationService implements AuthenticationServiceInterface {

    private final AuthUserRepository authUserRepository;
    private final EmailService emailService;
    private final JwtUtil jwtUtil;

    @Autowired
    public AuthenticationService(AuthUserRepository authUserRepository,
                                 EmailService emailService,
                                 JwtUtil jwtUtil) {
        this.authUserRepository = authUserRepository;
        this.emailService = emailService;
        this.jwtUtil = jwtUtil;
    }

    public String registerUser(AuthUserDTO authUserDTO) {
        if (authUserRepository.findByEmail(authUserDTO.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered!");
        }

        AuthUser newUser = new AuthUser();
        newUser.setFirstName(authUserDTO.getFirstName());
        newUser.setLastName(authUserDTO.getLastName());
        newUser.setEmail(authUserDTO.getEmail());
        newUser.setPassword(authUserDTO.getPassword()); // ⚠️ Storing password as plain text

        authUserRepository.save(newUser);

        if (emailService != null) {
            emailService.sendEmail(authUserDTO.getEmail(), "Welcome to Spotify!", "Thank you for registering!");
        } else {
            throw new RuntimeException("Email service is not initialized properly.");
        }

        return "User registered successfully!";
    }

    public Map<String, String> loginUser(LoginDTO loginDTO) {
        Optional<AuthUser> user = authUserRepository.findByEmail(loginDTO.getEmail());

        if (user.isPresent() && user.get().getPassword().equals(loginDTO.getPassword())) {
            String token = jwtUtil.generateToken(loginDTO.getEmail());

            Map<String, String> response = new HashMap<>();
            response.put("message", "Login successful!");
            response.put("token", token);
            return response;
        }

        throw new RuntimeException("Invalid credentials");
    }

    public String forgotPassword(String email, String newPassword) {
        Optional<AuthUser> userOptional = authUserRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            throw new RuntimeException("Sorry! We cannot find the user email: " + email);
        }

        AuthUser user = userOptional.get();
        user.setPassword(newPassword); // ⚠️ Storing password as plain text
        authUserRepository.save(user);

        emailService.sendEmail(email, "Password Changed", "Your password has been updated successfully.");

        return "Password has been changed successfully!";
    }

    public String resetPassword(String email, String currentPassword, String newPassword) {
        Optional<AuthUser> userOptional = authUserRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found with email: " + email);
        }

        AuthUser user = userOptional.get();

        if (!user.getPassword().equals(currentPassword)) {
            throw new RuntimeException("Current password is incorrect!");
        }

        user.setPassword(newPassword); // ⚠️ Storing password as plain text
        authUserRepository.save(user);

        emailService.sendEmail(email, "Password Reset", "Your password has been updated successfully.");

        return "Password reset successfully!";
    }
}
