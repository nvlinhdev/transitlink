package vn.edu.fpt.transitlink.profile.service.impl;

import org.springframework.stereotype.Service;
import vn.edu.fpt.transitlink.profile.dto.CreateProfileRequest;
import vn.edu.fpt.transitlink.profile.dto.UpdateBasicInfoRequest;
import vn.edu.fpt.transitlink.profile.dto.UpdatePhoneNumberRequest;
import vn.edu.fpt.transitlink.profile.dto.UserProfileDTO;
import vn.edu.fpt.transitlink.profile.mapper.UserProfileMapper;
import vn.edu.fpt.transitlink.profile.repository.UserProfileRepository;
import vn.edu.fpt.transitlink.profile.service.ProfileService;

import java.security.Principal;
import java.util.List;

@Service
public class ProfileServiceImpl implements ProfileService {
    private final UserProfileRepository profileRepository;
    private final UserProfileMapper userProfileMapper;

    public ProfileServiceImpl(UserProfileRepository profileRepository, UserProfileMapper userProfileMapper) {
        this.profileRepository = profileRepository;
        this.userProfileMapper = UserProfileMapper.INSTANCE;
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
    public UserProfileDTO createProfile(CreateProfileRequest request, Principal principal) {
        return null;
    }

    @Override
    public UserProfileDTO updateBasicInfo(UpdateBasicInfoRequest request, Principal principal) {
        return null;
    }

    @Override
    public UserProfileDTO updatePhoneNumber(UpdatePhoneNumberRequest request, Principal principal) {
        return null;
    }

    @Override
    public void deleteMyProfile(Principal principal) {

    }
}
