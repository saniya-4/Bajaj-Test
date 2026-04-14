package com.bfhl.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ApiErrorResponse {

    @JsonProperty("is_success")
    private final boolean success = false;

    @JsonProperty("official_email")
    private final String officialEmail;

    private final String error;

    public ApiErrorResponse(String officialEmail, String error) {
        this.officialEmail = officialEmail;
        this.error = error;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getOfficialEmail() {
        return officialEmail;
    }

    public String getError() {
        return error;
    }
}
