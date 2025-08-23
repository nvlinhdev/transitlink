package vn.edu.fpt.transitlink.user.spi;

import org.springframework.modulith.NamedInterface;
import vn.edu.fpt.transitlink.shared.exception.ThirdPartyException;
import vn.edu.fpt.transitlink.user.spi.dto.PhoneVerificationResult;

@NamedInterface
public interface PhoneVerificationService {
    PhoneVerificationResult verifyPhoneToken(String token) throws ThirdPartyException;
}
