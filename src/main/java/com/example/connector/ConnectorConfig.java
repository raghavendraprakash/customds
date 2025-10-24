package com.example.connector;

/**
 * Configuration class for data source connectors
 */
public class ConnectorConfig {
    private final int maxResults;
    private final int retryAttempts;
    private final long retryDelayMs;
    private final boolean enableValidation;
    
    private ConnectorConfig(Builder builder) {
        this.maxResults = builder.maxResults;
        this.retryAttempts = builder.retryAttempts;
        this.retryDelayMs = builder.retryDelayMs;
        this.enableValidation = builder.enableValidation;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static ConnectorConfig defaultConfig() {
        return builder().build();
    }
    
    // Getters
    public int getMaxResults() { return maxResults; }
    public int getRetryAttempts() { return retryAttempts; }
    public long getRetryDelayMs() { return retryDelayMs; }
    public boolean isValidationEnabled() { return enableValidation; }
    
    public static class Builder {
        private int maxResults = 50;
        private int retryAttempts = 3;
        private long retryDelayMs = 1000;
        private boolean enableValidation = true;
        
        public Builder maxResults(int maxResults) {
            this.maxResults = maxResults;
            return this;
        }
        
        public Builder retryAttempts(int retryAttempts) {
            this.retryAttempts = retryAttempts;
            return this;
        }
        
        public Builder retryDelayMs(long retryDelayMs) {
            this.retryDelayMs = retryDelayMs;
            return this;
        }
        
        public Builder enableValidation(boolean enableValidation) {
            this.enableValidation = enableValidation;
            return this;
        }
        
        public ConnectorConfig build() {
            return new ConnectorConfig(this);
        }
    }
}