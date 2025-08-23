package vn.edu.fpt.transitlink.auth.service.impl;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import vn.edu.fpt.transitlink.auth.exception.AuthErrorCode;
import vn.edu.fpt.transitlink.auth.service.GoogleTokenVerifierService;
import vn.edu.fpt.transitlink.shared.exception.BusinessException;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class GoogleTokenVerifierServiceImpl implements GoogleTokenVerifierService {

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientIdMobile;

    @Override
    public GoogleIdToken.Payload verify(String idToken) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(), new JacksonFactory())
                    .setAudience(Collections.singletonList(googleClientIdMobile))
                    .build();

            GoogleIdToken token = verifier.verify(idToken);
            if (token == null) {
                throw new BusinessException(AuthErrorCode.INVALID_CREDENTIALS);
            }
            return token.getPayload();

        } catch (Exception e) {
            throw new BusinessException(AuthErrorCode.INVALID_CREDENTIALS, e);
        }
    }
}
