package com.bfhl.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class HealthResponse {

    @JsonProperty("is_success")
    private final boolean success = true;

    @JsonProperty("official_email")
    private final String officialEmail;

    public HealthResponse(String officialEmail) {
        this.officialEmail = officialEmail;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getOfficialEmail() {
        return officialEmail;
    }
}
