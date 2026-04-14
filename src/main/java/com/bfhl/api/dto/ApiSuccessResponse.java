package com.bfhl.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ApiSuccessResponse {

    @JsonProperty("is_success")
    private final boolean success = true;

    @JsonProperty("official_email")
    private final String officialEmail;

    private final Object data;

    public ApiSuccessResponse(String officialEmail, Object data) {
        this.officialEmail = officialEmail;
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getOfficialEmail() {
        return officialEmail;
    }

    public Object getData() {
        return data;
    }
}
