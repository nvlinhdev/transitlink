package vn.edu.fpt.transitlink.notification.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.transitlink.notification.dto.NotificationTokenDTO;
import vn.edu.fpt.transitlink.notification.entity.NotificationToken;
import vn.edu.fpt.transitlink.notification.enumeration.NotificationTokenStatus;
import vn.edu.fpt.transitlink.notification.mapper.NotificationTokenMapper;
import vn.edu.fpt.transitlink.notification.repository.NotificationTokenRepository;
import vn.edu.fpt.transitlink.notification.request.RegisterTokenRequest;
import vn.edu.fpt.transitlink.notification.service.NotificationTokenService;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationTokenServiceImpl implements NotificationTokenService {

    private final NotificationTokenRepository notificationTokenRepository;
    private final NotificationTokenMapper mapper;

    @Override
    public NotificationTokenDTO registerToken(UUID accountId, RegisterTokenRequest request) {
        // Kiểm tra xem token đã tồn tại chưa
        return notificationTokenRepository.findByToken(request.getToken())
                .map(existing -> {
                    // Nếu đã có thì update lại account + platform + status
                    existing.setAccountId(accountId);
                    existing.setPlatform(request.getPlatform());
                    existing.setStatus(NotificationTokenStatus.ACTIVE);
                    return mapper.toDTO(notificationTokenRepository.save(existing));
                })
                .orElseGet(() -> {
                    // Nếu chưa có thì tạo mới
                    NotificationToken nt = new NotificationToken();
                    nt.setAccountId(accountId);
                    nt.setToken(request.getToken());
                    nt.setPlatform(request.getPlatform());
                    nt.setStatus(NotificationTokenStatus.ACTIVE);
                    return mapper.toDTO(notificationTokenRepository.save(nt));
                });
    }


    @Override
    public void deactivateToken(String token) {
        notificationTokenRepository.findByToken(token).ifPresent(nt -> {
            nt.setStatus(NotificationTokenStatus.LOGGED_OUT);
            notificationTokenRepository.save(nt);
        });
    }

    @Override
    public List<NotificationTokenDTO> getActiveTokens(UUID accountId) {
        List<NotificationToken> tokens = notificationTokenRepository.findAllByAccountIdAndStatus(accountId, NotificationTokenStatus.ACTIVE);
        return mapper.toDTOList(tokens);
    }
}
