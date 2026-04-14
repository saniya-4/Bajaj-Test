package com.bfhl.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.bfhl.api.config.BfhlProperties;
import com.bfhl.api.dto.ApiSuccessResponse;
import com.bfhl.api.service.BfhlService;
import com.fasterxml.jackson.databind.JsonNode;

@RestController
public class BfhlController {

    private final BfhlProperties properties;
    private final BfhlService bfhlService;

    public BfhlController(BfhlProperties properties, BfhlService bfhlService) {
        this.properties = properties;
        this.bfhlService = bfhlService;
    }

    @PostMapping("/bfhl")
    public ResponseEntity<ApiSuccessResponse> bfhl(@RequestBody JsonNode body) {
        Object data = bfhlService.processBfhl(body);
        return ResponseEntity.ok(new ApiSuccessResponse(properties.getOfficialEmail(), data));
    }
}
