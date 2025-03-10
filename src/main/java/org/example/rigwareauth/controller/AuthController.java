package org.example.rigwareauth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.rigwareauth.dto.AuthUserDTO;
import org.example.rigwareauth.dto.LoginDTO;
import org.example.rigwareauth.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/rig/auth")
@CrossOrigin(origins = "*")
@Tag(name = "Authentication API", description = "Endpoints for user authentication")
public class AuthController {

    @Autowired
    AuthenticationService authenticationService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Registers a user and sends a welcome email")
    public String registerUser(@Valid @RequestBody AuthUserDTO authUserDTO) {
        return authenticationService.registerUser(authUserDTO);
    }

    @PostMapping("/login")
    @Operation(summary = "User Login", description = "Allows users to log in and receive a JWT token")
    public Map<String, String> loginUser(@Valid @RequestBody LoginDTO loginDTO) {
        return authenticationService.loginUser(loginDTO);
    }

    @PutMapping("/resetPassword/{email}")
    @Operation(summary = "Reset Password")
    public ResponseEntity<Map<String, String>> resetPassword(
            @PathVariable String email, @RequestBody Map<String, String> passwordRequest) {

        String currentPassword = passwordRequest.get("currentPassword");
        String newPassword = passwordRequest.get("newPassword");

        if (currentPassword == null || newPassword == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Both current and new passwords are required!"));
        }

        String responseMessage = authenticationService.resetPassword(email, currentPassword, newPassword);
        return ResponseEntity.ok(Map.of("message", responseMessage));
    }

}
