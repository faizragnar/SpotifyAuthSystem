package org.example.rigwareauth.service;

import org.example.rigwareauth.dto.AuthUserDTO;
import org.example.rigwareauth.dto.LoginDTO;

import java.util.Map;

public interface AuthenticationServiceInterface {

    String registerUser(AuthUserDTO authUserDTO);

    Map<String, String> loginUser(LoginDTO loginDTO);

    String forgotPassword(String email, String newPassword);

    String resetPassword(String email, String currentPassword, String newPassword);
}