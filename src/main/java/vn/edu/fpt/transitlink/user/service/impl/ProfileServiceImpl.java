package vn.edu.fpt.transitlink.user.service.impl;

import org.springframework.stereotype.Service;
import vn.edu.fpt.transitlink.user.dto.UserProfileDTO;
import vn.edu.fpt.transitlink.user.mapper.UserProfileMapper;
import vn.edu.fpt.transitlink.user.repository.UserProfileRepository;
import vn.edu.fpt.transitlink.user.service.ProfileService;

import java.security.Principal;
import java.util.List;

@Service
public class ProfileServiceImpl implements ProfileService {
    private final UserProfileRepository profileRepository;
    private final UserProfileMapper userProfileMapper;

    public ProfileServiceImpl(UserProfileRepository profileRepository, UserProfileMapper userProfileMapper) {
        this.profileRepository = profileRepository;
        this.userProfileMapper = userProfileMapper;


    }

    @Override
    public UserProfileDTO getMyProfile(Principal principal) {
        return null;
    }

    @Override
    public UserProfileDTO getProfileById(String id, Principal principal) {
        return null;
    }

    @Override
    public List<UserProfileDTO> searchProfile(String query, int page, int size, Principal principal) {
        return List.of();
    }

    @Override
    public UserProfileDTO createProfile(UserProfileDTO profileData, Principal principal) {
        return null;
    }

    @Override
    public UserProfileDTO updateBasicInfo(UserProfileDTO profileData, Principal principal) {
        return null;
    }

    @Override
    public UserProfileDTO updatePhoneNumber(String token, Principal principal) {
        return null;
    }

    @Override
    public void deleteMyProfile(Principal principal) {

    }
}
