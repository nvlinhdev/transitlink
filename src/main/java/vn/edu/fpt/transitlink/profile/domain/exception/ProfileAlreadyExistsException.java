package vn.edu.fpt.transitlink.profile.domain.exception;

import vn.edu.fpt.transitlink.shared.exception.ConflictException;

import java.util.UUID;

public class ProfileAlreadyExistsException extends ConflictException {
    public ProfileAlreadyExistsException(UUID accountId) {
        super("Profile already exists for account: " + accountId);
    }
}
