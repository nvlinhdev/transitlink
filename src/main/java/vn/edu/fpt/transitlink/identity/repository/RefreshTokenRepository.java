package vn.edu.fpt.transitlink.identity.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.fpt.transitlink.identity.entity.Account;
import vn.edu.fpt.transitlink.identity.entity.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    void deleteByAccount(Account account);
    void deleteByToken(String token);
}
