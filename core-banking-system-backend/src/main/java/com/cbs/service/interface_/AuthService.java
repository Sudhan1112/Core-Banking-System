package com.cbs.service.interface_;

import com.cbs.model.dto.request.LoginRequest;
import com.cbs.model.dto.response.JwtResponse;

public interface AuthService {
    JwtResponse login(LoginRequest loginRequest);
}
