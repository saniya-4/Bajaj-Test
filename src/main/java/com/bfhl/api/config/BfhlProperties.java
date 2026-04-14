package com.bfhl.api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "bfhl")
public class BfhlProperties {

    /**
     * Official email returned in API responses (from EMAIL env).
     */
    private String officialEmail = "your.chitkara.email@chitkara.edu.in";

    /**
     * Gemini API key (from GEMINI_API_KEY env).
     */
    private String geminiApiKey = "";

    public String getOfficialEmail() {
        return officialEmail;
    }

    public void setOfficialEmail(String officialEmail) {
        this.officialEmail = officialEmail;
    }

    public String getGeminiApiKey() {
        return geminiApiKey;
    }

    public void setGeminiApiKey(String geminiApiKey) {
        this.geminiApiKey = geminiApiKey;
    }
}
