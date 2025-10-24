package com.example.connector;

import java.util.ArrayList;
import java.util.List;

/**
 * Configuration class for KMS Lighthouse repository connector
 */
public class KmsLighthouseConfig {
    private final String baseUrl;
    private final String apiKey;
    private final List<String> documentEndpoints;
    private final List<String> inclusionPatterns;
    private final List<String> exclusionPatterns;
    private final int rateLimit;
    private final KmsAuthenticationConfig authenticationConfig;
    private final boolean enableMetadataExtraction;
    private final int maxDocumentSize;
    
    private KmsLighthouseConfig(Builder builder) {
        this.baseUrl = builder.baseUrl;
        this.apiKey = builder.apiKey;
        this.documentEndpoints = new ArrayList<>(builder.documentEndpoints);
        this.inclusionPatterns = new ArrayList<>(builder.inclusionPatterns);
        this.exclusionPatterns = new ArrayList<>(builder.exclusionPatterns);
        this.rateLimit = builder.rateLimit;
        this.authenticationConfig = builder.authenticationConfig;
        this.enableMetadataExtraction = builder.enableMetadataExtraction;
        this.maxDocumentSize = builder.maxDocumentSize;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    // Getters
    public String getBaseUrl() { return baseUrl; }
    public String getApiKey() { return apiKey; }
    public List<String> getDocumentEndpoints() { return documentEndpoints; }
    public List<String> getInclusionPatterns() { return inclusionPatterns; }
    public List<String> getExclusionPatterns() { return exclusionPatterns; }
    public int getRateLimit() { return rateLimit; }
    public KmsAuthenticationConfig getAuthenticationConfig() { return authenticationConfig; }
    public boolean isMetadataExtractionEnabled() { return enableMetadataExtraction; }
    public int getMaxDocumentSize() { return maxDocumentSize; }
    
    public static class Builder {
        private String baseUrl;
        private String apiKey;
        private List<String> documentEndpoints = new ArrayList<>();
        private List<String> inclusionPatterns = new ArrayList<>();
        private List<String> exclusionPatterns = new ArrayList<>();
        private int rateLimit = 50;
        private KmsAuthenticationConfig authenticationConfig;
        private boolean enableMetadataExtraction = true;
        private int maxDocumentSize = 10485760; // 10MB default
        
        public Builder baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }
        
        public Builder apiKey(String apiKey) {
            this.apiKey = apiKey;
            return this;
        }
        
        public Builder addDocumentEndpoint(String endpoint) {
            this.documentEndpoints.add(endpoint);
            return this;
        }
        
        public Builder documentEndpoints(List<String> endpoints) {
            this.documentEndpoints.addAll(endpoints);
            return this;
        }
        
        public Builder addInclusionPattern(String pattern) {
            this.inclusionPatterns.add(pattern);
            return this;
        }
        
        public Builder inclusionPatterns(List<String> patterns) {
            this.inclusionPatterns.addAll(patterns);
            return this;
        }
        
        public Builder addExclusionPattern(String pattern) {
            this.exclusionPatterns.add(pattern);
            return this;
        }
        
        public Builder exclusionPatterns(List<String> patterns) {
            this.exclusionPatterns.addAll(patterns);
            return this;
        }
        
        public Builder rateLimit(int rateLimit) {
            this.rateLimit = rateLimit;
            return this;
        }
        
        public Builder authenticationConfig(KmsAuthenticationConfig authConfig) {
            this.authenticationConfig = authConfig;
            return this;
        }
        
        public Builder enableMetadataExtraction(boolean enable) {
            this.enableMetadataExtraction = enable;
            return this;
        }
        
        public Builder maxDocumentSize(int maxSize) {
            this.maxDocumentSize = maxSize;
            return this;
        }
        
        public KmsLighthouseConfig build() {
            if (baseUrl == null || baseUrl.isEmpty()) {
                throw new IllegalArgumentException("Base URL is required for KMS Lighthouse configuration");
            }
            return new KmsLighthouseConfig(this);
        }
    }
}