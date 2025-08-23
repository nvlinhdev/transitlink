package vn.edu.fpt.transitlink.auth.service;

import vn.edu.fpt.transitlink.auth.dto.LoginRequest;
import vn.edu.fpt.transitlink.auth.dto.RegisterRequest;
import vn.edu.fpt.transitlink.auth.dto.TokenData;

public interface AuthService {
    boolean register(RegisterRequest registerRequest);
    TokenData login(LoginRequest loginRequest);
    TokenData refresh(String refreshToken);
    void logout(String refreshToken);
    TokenData loginWithGoogleMobile(String idTokenString);
}
