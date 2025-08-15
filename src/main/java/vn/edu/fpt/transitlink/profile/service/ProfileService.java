package vn.edu.fpt.transitlink.profile.service;

import vn.edu.fpt.transitlink.profile.dto.UserProfileDTO;

import java.security.Principal;
import java.util.List;

public interface ProfileService {
    UserProfileDTO getMyProfile(Principal principal);
    UserProfileDTO getProfileById(String id, Principal principal);
    List<UserProfileDTO> searchProfile(String query, int page, int size, Principal principal);
    UserProfileDTO createProfile(UserProfileDTO profileData, Principal principal);
    UserProfileDTO updateBasicInfo(UserProfileDTO profileData, Principal principal);
    UserProfileDTO updatePhoneNumber(String token, Principal principal);
    void deleteMyProfile(Principal principal);
}
