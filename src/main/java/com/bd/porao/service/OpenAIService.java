package com.bd.porao.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.List;
import java.util.Map;
@Service
public class OpenAIService
{
    @Value("${openai.apiKey}")
    private String apiKey;
    @Value("${openai.baseUrl}")
    private String baseUrl;
    @Value("${openai.embeddingModel}")
    private String embeddingModel;
    @Value("${openai.chatModel}")
    private String chatModel;

    private final WebClient webClient;
    public OpenAIService(WebClient.Builder builder)
    {
        this.webClient = builder.baseUrl(baseUrl).build();
    }
    public float[] embed (String text)
    {
        if(text == null || text.isBlank())
        {
            return new float[1536];
        }
        Map<String, Object> request = Map.of(
                "input", text,
                "model", embeddingModel
        );
        try
        {
            Map response = webClient.post()
                    .uri("/embeddings")
                    .header("Authorization", "Bearer"+apiKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
            List<Map> data = (List<Map>) response.get("data");
            List<Double> embedding = (List<Double>) data.get(0).get("embedding");
            float[] result = new float[embedding.size()];
            for(int i = 0; i < embedding.size(); i++)
            {
                result[i] = embedding.get(i).floatValue();
            }
            return result;
        }
        catch (Exception e)
        {
            throw new RuntimeException("OpenAI embedding has failed: "+e.getMessage(),e);
        }
    }

    public String explainMatch(String req, String tutorName, String tutorBio, String subject, double rate, double matchScore)
    {
        List<Map<String,String>> message = List.of(
                Map.of("role", "system", "content","You are a helpful assistant explaining why a tutor matches a student's needs. " +
                                "Be concise (2-3 sentences max), friendly, and focus on the key reasons."),
                Map.of("role","user", "content", String.format(
                        "Student needs: %s\n\nTutor: %s\nSubject: %s\nRate: $%.2f/hr\nBio: %s\n\n" + "Explain briefly why this is a good match.",
                        req, tutorName, subject, rate,
                        tutorBio != null ? tutorBio : "No bio available"
                ))
        );
        Map<String, Object> request = Map.of(
                "model", chatModel,
                "messages", message,
                "max_tokens", 150,
                "temperature", 0.7
        );

        try
        {

            Map response = webClient.post()
                    .uri("/chat/completions")
                    .header("Authorization","Bearer"+apiKey)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
            List<Map> options = (List<Map>) response.get("options");
            Map messages = (Map) options.get(0).get("message");
            return (String) messages.get("match");
        }
        catch (Exception e)
        {
            return "Great match based on your requirements!";
        }
    }

}
