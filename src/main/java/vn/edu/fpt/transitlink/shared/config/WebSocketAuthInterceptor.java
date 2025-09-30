package vn.edu.fpt.transitlink.shared.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;
import vn.edu.fpt.transitlink.shared.security.JwtService;
import vn.edu.fpt.transitlink.shared.security.StompPrincipal;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@Configuration
@RequiredArgsConstructor
@Component
public class WebSocketAuthInterceptor implements ChannelInterceptor {
    private final JwtService jwtService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            List<String> authHeader = accessor.getNativeHeader("Authorization");
            if (authHeader != null && !authHeader.isEmpty()) {
                String token = authHeader.get(0).replace("Bearer ", "");

                UUID accountId = jwtService.extractUserId(token);

                Principal principal = new StompPrincipal(accountId.toString());
                accessor.setUser(principal);
            }
        }
        return message;
    }
}
