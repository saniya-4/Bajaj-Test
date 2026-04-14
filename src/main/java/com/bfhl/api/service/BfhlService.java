package com.bfhl.api.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.bfhl.api.exception.ApiException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

@Service
public class BfhlService {

    private static final int MAX_FIBONACCI_TERMS = 1000;
    private static final Set<String> ALLOWED_KEYS = Set.of("fibonacci", "prime", "lcm", "hcf", "AI");

    private final GeminiClient geminiClient;

    public BfhlService(GeminiClient geminiClient) {
        this.geminiClient = geminiClient;
    }

    public Object processBfhl(JsonNode body) {
        if (body == null || !body.isObject()) {
            throw new ApiException(400, "Request body must be a JSON object.");
        }

        if (body.size() != 1) {
            throw new ApiException(400, "Exactly one operation key is required.");
        }

        var field = body.fields().next();
        String key = field.getKey();
        if (!ALLOWED_KEYS.contains(key)) {
            throw new ApiException(400, "Unsupported operation key.");
        }

        JsonNode payload = field.getValue();

        return switch (key) {
            case "fibonacci" -> handleFibonacci(payload);
            case "prime" -> handlePrime(payload);
            case "lcm" -> handleLcm(payload);
            case "hcf" -> handleHcf(payload);
            case "AI" -> handleAi(payload);
            default -> throw new ApiException(400, "Unsupported operation key.");
        };
    }

    private List<Integer> handleFibonacci(JsonNode payload) {
        if (!isJsonInteger(payload)) {
            throw new ApiException(400, "fibonacci must be a non-negative integer.");
        }
        long n = payload.longValue();
        if (n < 0) {
            throw new ApiException(400, "fibonacci must be a non-negative integer.");
        }
        if (n > MAX_FIBONACCI_TERMS) {
            throw new ApiException(422, "fibonacci exceeds max limit of " + MAX_FIBONACCI_TERMS + ".");
        }
        return fibonacciSeries((int) n);
    }

    private List<Integer> fibonacciSeries(int n) {
        List<Integer> response = new ArrayList<>(n);
        int first = 0;
        int second = 1;
        for (int i = 0; i < n; i++) {
            response.add(first);
            int next = first + second;
            first = second;
            second = next;
        }
        return response;
    }

    private List<Integer> handlePrime(JsonNode payload) {
        String err = validateIntegerArray(payload, true);
        if (err != null) {
            throw new ApiException(400, err);
        }
        ArrayNode arr = (ArrayNode) payload;
        List<Integer> out = new ArrayList<>();
        for (JsonNode n : arr) {
            int v = n.intValue();
            if (isPrime(v)) {
                out.add(v);
            }
        }
        return out;
    }

    private int handleLcm(JsonNode payload) {
        String err = validateIntegerArray(payload, false);
        if (err != null) {
            throw new ApiException(400, err);
        }
        ArrayNode arr = (ArrayNode) payload;
        int acc = arr.get(0).intValue();
        for (int i = 1; i < arr.size(); i++) {
            acc = lcmTwo(acc, arr.get(i).intValue());
        }
        return acc;
    }

    private int handleHcf(JsonNode payload) {
        String err = validateIntegerArray(payload, false);
        if (err != null) {
            throw new ApiException(400, err);
        }
        ArrayNode arr = (ArrayNode) payload;
        int acc = arr.get(0).intValue();
        for (int i = 1; i < arr.size(); i++) {
            acc = gcd(acc, arr.get(i).intValue());
        }
        return acc;
    }

    private String handleAi(JsonNode payload) {
        if (!payload.isTextual() || payload.asText("").trim().isEmpty()) {
            throw new ApiException(400, "AI must be a non-empty string question.");
        }
        try {
            return geminiClient.fetchSingleWord(payload.asText().trim());
        } catch (IllegalStateException e) {
            String message =
                    e.getMessage() != null && !e.getMessage().isBlank()
                            ? e.getMessage()
                            : "AI service is currently unavailable.";
            throw new ApiException(503, message);
        }
    }

    private String validateIntegerArray(JsonNode value, boolean allowEmpty) {
        if (value == null || !value.isArray()) {
            return "Input must be an array of integers.";
        }
        ArrayNode arr = (ArrayNode) value;
        if (!allowEmpty && arr.size() == 0) {
            return "Input array must not be empty.";
        }
        for (JsonNode n : arr) {
            if (!isJsonInteger(n)) {
                return "All array elements must be integers.";
            }
        }
        return null;
    }

    private boolean isJsonInteger(JsonNode n) {
        if (n == null || !n.isNumber()) {
            return false;
        }
        if (n.isIntegralNumber()) {
            return true;
        }
        double d = n.asDouble();
        return !Double.isNaN(d) && !Double.isInfinite(d) && d == Math.rint(d);
    }

    private boolean isPrime(int num) {
        if (num < 2) {
            return false;
        }
        if (num == 2) {
            return true;
        }
        if (num % 2 == 0) {
            return false;
        }
        for (int divisor = 3; divisor * divisor <= num; divisor += 2) {
            if (num % divisor == 0) {
                return false;
            }
        }
        return true;
    }

    private int gcd(int a, int b) {
        int x = Math.abs(a);
        int y = Math.abs(b);
        while (y != 0) {
            int t = x % y;
            x = y;
            y = t;
        }
        return x;
    }

    private int lcmTwo(int a, int b) {
        if (a == 0 || b == 0) {
            return 0;
        }
        return Math.abs((a / gcd(a, b)) * b);
    }
}
