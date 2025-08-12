package vn.edu.fpt.transitlink.profile.service;

import vn.edu.fpt.transitlink.profile.dto.CreateProfileRequest;
import vn.edu.fpt.transitlink.profile.dto.UserProfileDTO;

import java.util.UUID;

public interface ProfileService {
    UserProfileDTO createProfile(UUID accountId, CreateProfileRequest request);
    UserProfileDTO getProfile(UUID accountId);
}
