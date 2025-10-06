package com.bd.porao.controller;

import com.bd.porao.model.AvailabilitySlot;
import com.bd.porao.model.Role;
import com.bd.porao.model.User;
import com.bd.porao.repository.AvailabilitySlotRepository;
import com.bd.porao.repository.UserRepository;
import com.bd.porao.security.JwtAuthenticationToken;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/availability")
public class AvailabilityController
{
    private final AvailabilitySlotRepository repo;
    private final UserRepository users;

    public AvailabilityController(AvailabilitySlotRepository r, UserRepository u) {
        this.repo = r;
        this.users = u;
    }

    private User currentUser() {
        var auth = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        Long id = Long.valueOf(auth.getToken().getSubject());
        return users.findById(id).orElseThrow();
    }

    @GetMapping("/{tutorId}")
    public List<Map<String, Object>> list(
            @PathVariable Long tutorId,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to) {

        Instant start = from == null ? Instant.now() : Instant.parse(from);
        Instant end = to == null ? start.plus(Duration.ofDays(30)) : Instant.parse(to);

        return repo.findByTutorAndBetween(tutorId, start, end).stream()
                .map(s -> Map.of(
                        "id", (Object) s.getId(),
                        "startUtc", s.getStartUtc().toString(),
                        "endUtc", s.getEndUtc().toString(),
                        "recurringWeekly", s.isRecurringWeekly()
                ))
                .collect(Collectors.toList());
    }

    @PostMapping("/mine")
    public void upsertMine(@RequestBody List<Map<String, String>> slots) {
        User me = currentUser();
        if (me.getRole() != Role.TUTOR) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        for (Map<String, String> m : slots) {
            AvailabilitySlot s = new AvailabilitySlot();
            s.setTutor(me);
            s.setStartUtc(Instant.parse(m.get("startUtc")));
            s.setEndUtc(Instant.parse(m.get("endUtc")));
            if (m.containsKey("recurringWeekly")) {
                s.setRecurringWeekly(Boolean.parseBoolean(m.get("recurringWeekly")));
            }
            repo.save(s);
        }
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        User me = currentUser();
        if (me.getRole() != Role.TUTOR) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        AvailabilitySlot slot = repo.findById(id).orElseThrow();
        if (!slot.getTutor().getId().equals(me.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        repo.delete(slot);
    }

}
