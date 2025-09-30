package vn.edu.fpt.transitlink.identity.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import vn.edu.fpt.transitlink.shared.exception.BusinessException;

public interface GoogleTokenVerifierService {
    GoogleIdToken.Payload verify(String idToken) throws BusinessException;
}
