package com.bd.porao.controller;

import com.bd.porao.service.SuggestionService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/suggest")

public class SuggestController
{
    private final SuggestionService svc;

    public SuggestController(SuggestionService s) {
        this.svc = s;
    }

    record Req(String requirement, String subject, Double lat, Double lng,
               Double minRate, Double maxRate, Integer limit, Boolean includeExplanations) {}

    @PostMapping
    public List<Map<String, Object>> suggest(@RequestBody Req r) {
        return svc.suggest(
                r.requirement(),
                r.lat(),
                r.lng(),
                r.subject(),
                r.minRate(),
                r.maxRate(),
                r.limit(),
                r.includeExplanations() != null ? r.includeExplanations() : true
        );
    }
}
