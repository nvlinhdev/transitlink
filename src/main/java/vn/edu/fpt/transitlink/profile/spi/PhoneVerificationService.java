package vn.edu.fpt.transitlink.profile.spi;

import vn.edu.fpt.transitlink.profile.spi.dto.PhoneVerificationResult;
import vn.edu.fpt.transitlink.shared.exception.ThirdPartyException;

public interface PhoneVerificationService {
    PhoneVerificationResult verifyPhoneToken(String token) throws ThirdPartyException;
}
