package vn.edu.fpt.transitlink.identity.service;

import vn.edu.fpt.transitlink.identity.request.LoginRequest;
import vn.edu.fpt.transitlink.identity.dto.TokenData;

public interface AuthService {
    TokenData login(LoginRequest loginRequest);
    TokenData refresh(String refreshToken);
    void logout(String refreshToken);
    TokenData loginWithGoogleMobile(String idTokenString);
}
