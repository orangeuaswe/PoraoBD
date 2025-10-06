package com.bd.porao.service;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OpenAIEmbeddingService implements EmbeddingsService
{
    private final OpenAIService open;
    private final Map<String, float[]> cache = new ConcurrentHashMap<>();
    public OpenAIEmbeddingService(OpenAIService open)
    {
        this.open = open;
    }
    @Override
    public float [] embed(String text)
    {
        if(text == null || text.isBlank())
        {
            return new float[1536];
        }
        return cache.computeIfAbsent(text,open::embed);
    }
}
