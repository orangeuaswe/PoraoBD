package com.bd.porao.controller;

import com.bd.porao.model.Booking;
import com.bd.porao.model.Role;
import com.bd.porao.model.User;
import com.bd.porao.repository.BookingRepository;
import com.bd.porao.repository.UserRepository;
import com.bd.porao.security.JwtAuthenticationToken;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/bookings")
public class BookingController
{
    private final BookingRepository bookings;
    private final UserRepository users;

    public BookingController(BookingRepository b, UserRepository u) {
        this.bookings = b;
        this.users = u;
    }

    private User currentUser() {
        var auth = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        Long id = Long.valueOf(auth.getToken().getSubject());
        return users.findById(id).orElseThrow();
    }

    record CreateReq(Long tutorId, String startUtc, String endUtc) {}

    @PostMapping
    public Map<String, Object> create(@RequestBody CreateReq r) {
        User me = currentUser();
        if (me.getRole() != Role.STUDENT) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        User tutor = users.findById(r.tutorId()).orElseThrow();

        Booking b = new Booking();
        b.setStudent(me);
        b.setTutor(tutor);
        b.setStartUtc(Instant.parse(r.startUtc()));
        b.setEndUtc(Instant.parse(r.endUtc()));
        b.setStatus("PENDING");

        // Calculate amount
        long minutes = Duration.between(b.getStartUtc(), b.getEndUtc()).toMinutes();
        double hours = Math.max(0.5, minutes / 60.0);
        double rate = tutor.getTutorProfile() != null &&
                tutor.getTutorProfile().getRatePerHour() != null ?
                tutor.getTutorProfile().getRatePerHour() : 20.0;
        b.setAmount(rate * hours);

        bookings.save(b);

        Map<String, Object> result = new HashMap<>();
        result.put("bookingId", b.getId());
        result.put("status", b.getStatus());
        result.put("amount", b.getAmount());
        return result;
    }

    @GetMapping
    public List<Map<String, Object>> mine() {
        User me = currentUser();
        List<Booking> list = me.getRole() == Role.STUDENT ?
                bookings.findByStudentId(me.getId()) :
                bookings.findByTutorId(me.getId());

        return list.stream().map(b -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", b.getId());
            m.put("tutorName", b.getTutor().getName());
            m.put("studentName", b.getStudent().getName());
            m.put("startUtc", b.getStartUtc().toString());
            m.put("endUtc", b.getEndUtc().toString());
            m.put("status", b.getStatus());
            m.put("amount", b.getAmount());
            return m;
        }).collect(Collectors.toList());
    }
}
