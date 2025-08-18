package vn.edu.fpt.transitlink.user.spi;

import vn.edu.fpt.transitlink.shared.exception.ThirdPartyException;
import vn.edu.fpt.transitlink.user.spi.dto.PhoneVerificationResult;

public interface PhoneVerificationService {
    PhoneVerificationResult verifyPhoneToken(String token) throws ThirdPartyException;
}
