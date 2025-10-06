package com.bd.porao.service;

import com.bd.porao.model.TutorProfile;
import com.bd.porao.repository.TutorProfileRepository;
import com.bd.porao.util.Vectors;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SuggestionService
{
    private final TutorProfileRepository tutor;
    private final EmbeddingsService embeddings;
    private final OpenAIService openAI;

    public SuggestionService(TutorProfileRepository tutor, EmbeddingsService embeddings, OpenAIService openAI)
    {
        this.tutor = tutor;
        this.embeddings = embeddings;
        this.openAI = openAI;
    }

    public List<Map<String,Object>> suggest(String reqs, Double lat, Double lng, String subject, Double minRate, Double maxRate, Integer limit, boolean includeExplain)
    {
        var pool = tutor.search(subject);
        if(minRate != null || maxRate != null)
        {
            pool = pool.stream().filter(tp->
            {
                var r = tp.getRatePerHour();
                if(r == null)
                {
                    return false;
                }
                if(minRate != null && r < minRate)
                {
                    return false;
                }
                if(maxRate != null && r > maxRate)
                {
                    return false;
                }
                return true;
            }).collect(Collectors.toList());
        }

        var reqVec = embeddings.embed(reqs);
        return pool.stream().map(tp->
        {
            String tText = String.format("%s teaches %s. Bio: %s. Rate: $%.2f/hr",
                    tp.getUser().getName(), tp.getSubject(),
                    tp.getBio() != null ? tp.getBio():"",
                    tp.getRatePerHour() != null ? tp.getRatePerHour() : 0.0);
            var vec = embeddings.embed(tText);
            var score = Vectors.cosine(reqVec, vec);
            double dist = 0;
            if(lat != null && lng != null && tp.getLat() != null && tp.getLng()!=null)
            {
                dist = haversineKm(lat,lng, tp.getLat(),tp.getLng());
            }
            var result = new HashMap<String,Object>();
            result.put("id", tp.getId());
            result.put("userId", tp.getUser().getId());
            result.put("name",tp.getUser().getName());
            result.put("subject", tp.getSubject());
            result.put("ratePerHour", tp.getRatePerHour());
            result.put("headline", tp.getHeadline());
            result.put("score", score);
            result.put("distanceKm", dist);

            if (includeExplain && score > 0.5)
            {
                String explanation = openAI.explainMatch(
                        reqs,
                        tp.getUser().getName(),
                        tp.getBio() != null ? tp.getBio() : tp.getHeadline(),
                        tp.getSubject(),
                        tp.getRatePerHour() != null ? tp.getRatePerHour() : 0.0,
                        score
                );
                result.put("explanation", explanation);
            }

            return result;
        })
                .sorted((a, b) -> Double.compare((double) b.get("score"), (double) a.get("score")))
                .limit(limit == null ? 10 : limit)
                .collect(Collectors.toList());
    }
    private static double haversineKm(double lat1, double lon1, double lat2, double lon2)
    {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return 6371 * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }
}
