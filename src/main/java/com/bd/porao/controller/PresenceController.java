package com.bd.porao.controller;

import com.bd.porao.model.User;
import com.bd.porao.repository.UserRepository;
import com.bd.porao.security.JwtAuthenticationToken;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api/presence")
public class PresenceController
{
    private final SimpMessagingTemplate broker;
    private final UserRepository users;

    public PresenceController(SimpMessagingTemplate b, UserRepository u) {
        this.broker = b;
        this.users = u;
    }

    private User currentUser() {
        var auth = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        Long id = Long.valueOf(auth.getToken().getSubject());
        return users.findById(id).orElseThrow();
    }

    record TypingReq(Long conversationId, boolean isTyping) {}
    record ReadReq(Long conversationId, Long lastReadMessageId) {}

    @PostMapping("/typing")
    public void typing(@RequestBody TypingReq r) {
        User me = currentUser();
        broker.convertAndSend("/topic/conversations/" + r.conversationId() + "/presence",
                Map.of("type", "typing", "userId", me.getId(), "isTyping", r.isTyping(),
                        "at", Instant.now().toString()));
    }

    @PostMapping("/read")
    public void read(@RequestBody ReadReq r) {
        User me = currentUser();
        broker.convertAndSend("/topic/conversations/" + r.conversationId() + "/presence",
                Map.of("type", "read", "userId", me.getId(),
                        "lastReadMessageId", r.lastReadMessageId(),
                        "at", Instant.now().toString()));
    }

}
