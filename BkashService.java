package com.bd.porao.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.Map;
@Service
public class BkashService
{

    @Value("${bkash.baseUrl}")
    private String baseUrl;

    @Value("${bkash.username}")
    private String username;

    @Value("${bkash.password}")
    private String password;

    @Value("${bkash.appKey}")
    private String appKey;

    @Value("${bkash.appSecret}")
    private String appSecret;

    private final WebClient webClient;

    public BkashService(WebClient.Builder builder)
    {
        this.webClient = builder.build();
    }

    public String getToken()
    {
        try
        {
            Map<String, String> request = Map.of(
                    "app_key", appKey,
                    "app_secret", appSecret
            );

            Map response = webClient.post()
                    .uri(baseUrl + "/tokenized/checkout/token/grant")
                    .header("username", username)
                    .header("password", password)
                    .header("Content-Type", "application/json")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            return (String) response.get("id_token");
        } catch (Exception e)
        {
            throw new RuntimeException("Failed to get bKash token: " + e.getMessage(), e);
        }
    }
    public Map<String, Object> createPayment(String token, String amount, String callbackUrl)
    {
        try
        {
            Map<String, Object> request = Map.of(
                    "amount", amount,
                    "currency", "BDT",
                    "intent", "sale",
                    "merchantInvoiceNumber", "INV" + System.currentTimeMillis(),
                    "callbackURL", callbackUrl
            );

            Map response = webClient.post()
                    .uri(baseUrl + "/tokenized/checkout/create")
                    .header("Authorization", "Bearer " + token)
                    .header("X-APP-Key", appKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            return response;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create bKash payment: " + e.getMessage(), e);
        }
    }

    public Map<String, Object> executePayment(String token, String paymentID)
    {
        try
        {
            Map response = webClient.post()
                    .uri(baseUrl + "/tokenized/checkout/execute")
                    .header("Authorization", "Bearer " + token)
                    .header("X-APP-Key", appKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(Map.of("paymentID", paymentID))
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            return response;
        } catch (Exception e) {
            throw new RuntimeException("Failed to execute bKash payment: " + e.getMessage(), e);
        }
    }

    public Map<String, Object> queryPayment(String token, String paymentID)
    {
        try {
            Map response = webClient.post()
                    .uri(baseUrl + "/tokenized/checkout/payment/status")
                    .header("Authorization", "Bearer " + token)
                    .header("X-APP-Key", appKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(Map.of("paymentID", paymentID))
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            return response;
        } catch (Exception e) {
            throw new RuntimeException("Failed to query bKash payment: " + e.getMessage(), e);
        }
    }

    public Map<String, Object> refundPayment(String token, String paymentID, String amount, String trxID)
    {
        try {
            Map<String, Object> request = Map.of(
                    "paymentID", paymentID,
                    "amount", amount,
                    "trxID", trxID,
                    "sku", "refund",
                    "reason", "Booking cancelled"
            );

            Map response = webClient.post()
                    .uri(baseUrl + "/tokenized/checkout/payment/refund")
                    .header("Authorization", "Bearer " + token)
                    .header("X-APP-Key", appKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            return response;
        } catch (Exception e)
        {
            throw new RuntimeException("Failed to refund bKash payment: " + e.getMessage(), e);
        }
    }
}