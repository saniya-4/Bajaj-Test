package com.bfhl.api.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.bfhl.api.config.BfhlProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class GeminiClient {

    private static final String ENDPOINT =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash-lite:generateContent";

    private static final Pattern WORD = Pattern.compile("[A-Za-z]+");

    private final RestTemplate restTemplate;
    private final BfhlProperties properties;
    private final ObjectMapper objectMapper;

    public GeminiClient(RestTemplate restTemplate, BfhlProperties properties, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.properties = properties;
        this.objectMapper = objectMapper;
    }

    public String fetchSingleWord(String question) {
        String apiKey = properties.getGeminiApiKey();
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("GEMINI_API_KEY is missing.");
        }

        String prompt = "Respond with exactly one word only.\nQuestion: " + question;

        Map<String, Object> generationConfig = new HashMap<>();
        generationConfig.put("temperature", 0.1);
        generationConfig.put("maxOutputTokens", 8);

        Map<String, Object> part = Map.of("text", prompt);
        Map<String, Object> content = Map.of("parts", List.of(part));
        Map<String, Object> body = new HashMap<>();
        body.put("contents", List.of(content));
        body.put("generationConfig", generationConfig);

        String url = UriComponentsBuilder.fromUriString(ENDPOINT).queryParam("key", apiKey).build(true).toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        try {
            ResponseEntity<String> response =
                    restTemplate.postForEntity(url, new HttpEntity<>(body, headers), String.class);
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode textNode =
                    root.path("candidates").path(0).path("content").path("parts").path(0).path("text");
            String text = textNode.isMissingNode() ? "" : textNode.asText("");
            Matcher matcher = WORD.matcher(text.trim());
            if (!matcher.find()) {
                throw new IllegalStateException("AI response was empty.");
            }
            return matcher.group();
        } catch (HttpStatusCodeException e) {
            String message = extractGeminiErrorMessage(e.getResponseBodyAsString());
            throw new IllegalStateException(message != null ? message : e.getMessage());
        } catch (IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException(
                    e.getMessage() != null ? e.getMessage() : "AI service is currently unavailable.");
        }
    }

    private String extractGeminiErrorMessage(String responseBody) {
        if (responseBody == null || responseBody.isBlank()) {
            return null;
        }
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode msg = root.path("error").path("message");
            return msg.isMissingNode() || msg.asText("").isBlank() ? null : msg.asText();
        } catch (Exception ignored) {
            return null;
        }
    }
}
