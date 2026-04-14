package com.bfhl.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bfhl.api.config.BfhlProperties;
import com.bfhl.api.dto.HealthResponse;

@RestController
public class HealthController {

    private final BfhlProperties properties;

    public HealthController(BfhlProperties properties) {
        this.properties = properties;
    }

    @GetMapping("/health")
    public ResponseEntity<HealthResponse> health() {
        return ResponseEntity.ok(new HealthResponse(properties.getOfficialEmail()));
    }
}
