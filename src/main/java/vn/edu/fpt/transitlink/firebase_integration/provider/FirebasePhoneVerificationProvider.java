package vn.edu.fpt.transitlink.firebase_integration.provider;

import vn.edu.fpt.transitlink.profile.spi.PhoneVerificationService;
import vn.edu.fpt.transitlink.profile.spi.dto.PhoneVerificationResult;
import vn.edu.fpt.transitlink.shared.exception.ThirdPartyException;

public class FirebasePhoneVerificationProvider implements PhoneVerificationService {
    @Override
    public PhoneVerificationResult verifyPhoneToken(String token) throws ThirdPartyException {
        return PhoneVerificationResult.invalid();
    }
}
