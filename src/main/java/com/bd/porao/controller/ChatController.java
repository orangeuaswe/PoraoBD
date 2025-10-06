package com.bd.porao.controller;

import com.bd.porao.model.*;
import com.bd.porao.repository.*;
import com.bd.porao.security.JwtAuthenticationToken;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class ChatController
{
    private final ConversationRepository convRepo;
    private final MessageRepository msgRepo;
    private final UserRepository users;
    private final SimpMessagingTemplate broker;

    public ChatController(ConversationRepository c, MessageRepository m,
                          UserRepository u, SimpMessagingTemplate b) {
        this.convRepo = c;
        this.msgRepo = m;
        this.users = u;
        this.broker = b;
    }

    private User currentUser() {
        var auth = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        Long id = Long.valueOf(auth.getToken().getSubject());
        return users.findById(id).orElseThrow();
    }

    record CreateConvReq(Long tutorId) {}
    record ConvRes(Long id, Long tutorId, String tutorName, Long studentId, String studentName) {}
    record SendMsgReq(Long conversationId, String content) {}
    record MsgRes(Long id, Long conversationId, Long senderId, String senderName,
                  String content, Instant sentAt) {}

    @PostMapping("/conversations")
    public ConvRes start(@RequestBody CreateConvReq req) {
        User me = currentUser();
        if (me.getRole() != Role.STUDENT) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        User tutor = users.findById(req.tutorId()).orElseThrow();
        Conversation c = convRepo.findBetween(me.getId(), tutor.getId())
                .orElseGet(() -> {
                    Conversation nc = new Conversation();
                    nc.setStudent(me);
                    nc.setTutor(tutor);
                    return convRepo.save(nc);
                });

        return new ConvRes(c.getId(), tutor.getId(), tutor.getName(),
                me.getId(), me.getName());
    }

    @GetMapping("/conversations")
    public List<ConvRes> mine() {
        User me = currentUser();
        return convRepo.findForUser(me.getId()).stream()
                .map(c -> new ConvRes(c.getId(), c.getTutor().getId(), c.getTutor().getName(),
                        c.getStudent().getId(), c.getStudent().getName()))
                .collect(Collectors.toList());
    }

    @GetMapping("/conversations/{id}/messages")
    public List<MsgRes> history(@PathVariable Long id) {
        return msgRepo.findByConversation(id).stream()
                .map(m -> new MsgRes(m.getId(), id, m.getSender().getId(),
                        m.getSender().getName(), m.getContent(), m.getSentAt()))
                .collect(Collectors.toList());
    }

    @PostMapping("/messages")
    public MsgRes send(@RequestBody SendMsgReq req) {
        User me = currentUser();
        Conversation conv = convRepo.findById(req.conversationId()).orElseThrow();

        if (!conv.getStudent().getId().equals(me.getId()) &&
                !conv.getTutor().getId().equals(me.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        Message m = new Message();
        m.setConversation(conv);
        m.setSender(me);
        m.setContent(req.content());
        Message saved = msgRepo.save(m);

        MsgRes res = new MsgRes(saved.getId(), conv.getId(), me.getId(),
                me.getName(), saved.getContent(), saved.getSentAt());
        broker.convertAndSend("/topic/conversations/" + conv.getId(), res);
        return res;
    }
}
