package com.bd.porao.controller;

import com.bd.porao.model.TutorProfile;
import com.bd.porao.repository.TutorProfileRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tutors")

public class TutorController
{
    private final TutorProfileRepository tutors;

    public TutorController(TutorProfileRepository tutors) {
        this.tutors = tutors;
    }

    @GetMapping
    public List<Map<String, Object>> list(@RequestParam(required = false) String q) {
        return tutors.search(q).stream().map(this::toMap).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public Map<String, Object> one(@PathVariable Long id) {
        TutorProfile t = tutors.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Map<String, Object> result = toMap(t);
        result.put("bio", t.getBio());
        result.put("languages", t.getLanguages());
        return result;
    }

    @GetMapping("/near")
    public List<Map<String, Object>> near(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam(defaultValue = "25") double radiusKm) {

        return tutors.findNear(lat, lng, radiusKm).stream().map(row -> {
            TutorProfile t = (TutorProfile) row[0];
            Double distance = (Double) row[1];
            Map<String, Object> result = toMap(t);
            result.put("distanceKm", distance);
            return result;
        }).collect(Collectors.toList());
    }

    private Map<String, Object> toMap(TutorProfile t) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", t.getId());
        map.put("userId", t.getUser().getId());
        map.put("name", t.getUser().getName());
        map.put("subject", t.getSubject());
        map.put("headline", t.getHeadline());
        map.put("ratePerHour", t.getRatePerHour());
        map.put("rating", t.getRating());
        map.put("ratingCount", t.getRatingCount());
        map.put("lat", t.getLat());
        map.put("lng", t.getLng());
        return map;
    }
}
