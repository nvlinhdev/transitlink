package vn.edu.fpt.transitlink.profile.service;

import vn.edu.fpt.transitlink.profile.dto.CreateProfileRequest;
import vn.edu.fpt.transitlink.profile.dto.UpdateBasicInfoRequest;
import vn.edu.fpt.transitlink.profile.dto.UpdatePhoneNumberRequest;
import vn.edu.fpt.transitlink.profile.dto.UserProfileDTO;

import java.security.Principal;
import java.util.List;

public interface ProfileService {
    UserProfileDTO getMyProfile(Principal principal);
    UserProfileDTO getProfileById(String id, Principal principal);
    List<UserProfileDTO> searchProfile(String query, int page, int size, Principal principal);
    UserProfileDTO createProfile(CreateProfileRequest request, Principal principal);
    UserProfileDTO updateBasicInfo(UpdateBasicInfoRequest request, Principal principal);
    UserProfileDTO updatePhoneNumber(UpdatePhoneNumberRequest request, Principal principal);
    void deleteMyProfile(Principal principal);
}
