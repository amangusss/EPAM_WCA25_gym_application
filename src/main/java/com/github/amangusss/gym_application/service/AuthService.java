package com.github.amangusss.gym_application.service;

import com.github.amangusss.gym_application.dto.auth.AuthDTO;

public interface AuthService {

    AuthDTO.Response.Login login(AuthDTO.Request.Login request);
    void changePassword(AuthDTO.Request.ChangePassword request);
    void logout(String username);
}
