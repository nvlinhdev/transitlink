package vn.edu.fpt.transitlink.profile.infrastructure.repository;

import vn.edu.fpt.transitlink.profile.domain.model.UserProfile;
import vn.edu.fpt.transitlink.profile.domain.repository.UserProfileRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaUserProfileRepository implements UserProfileRepository {

    private final SpringDataUserProfileRepository springDataRepo;

    public JpaUserProfileRepository(SpringDataUserProfileRepository springDataRepo) {
        this.springDataRepo = springDataRepo;
    }

    @Override
    public Optional<UserProfile> findByAccountId(UUID accountId) {
        return springDataRepo.findByAccountId(accountId);
    }

    @Override
    public Optional<UserProfile> findById(UUID id) {
        return springDataRepo.findById(id);
    }

    @Override
    public void save(UserProfile profile) {
        springDataRepo.save(profile);
    }

    @Override
    public boolean existsByAccountId(UUID accountId) {
        return springDataRepo.existsByAccountId(accountId);
    }
}
