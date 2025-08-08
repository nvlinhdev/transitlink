package vn.edu.fpt.transitlink.shared.util;

import java.security.Principal;
import java.util.UUID;

public class RequestContextUtil {

    public static UUID getAccountId(Principal principal) {
        return UUID.fromString(principal.getName());
    }
}