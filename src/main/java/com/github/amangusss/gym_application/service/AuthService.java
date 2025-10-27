package com.github.amangusss.gym_application.service;

import com.github.amangusss.gym_application.dto.auth.AuthDTO;

public interface AuthService {

    boolean login(AuthDTO.Request.Login request);
    boolean changePassword(AuthDTO.Request.ChangePassword request);
}
