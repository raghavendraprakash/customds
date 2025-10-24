package com.example.connector;

/**
 * Options for KMS Lighthouse ingestion jobs
 */
public class KmsIngestionOptions {
    private final String clientToken;
    private final boolean monitoringEnabled;
    private final int batchSize;
    private final boolean enableParallelProcessing;
    private final String notificationTopicArn;
    private final boolean extractMetadata;
    private final int retryAttempts;
    
    private KmsIngestionOptions(Builder builder) {
        this.clientToken = builder.clientToken;
        this.monitoringEnabled = builder.monitoringEnabled;
        this.batchSize = builder.batchSize;
        this.enableParallelProcessing = builder.enableParallelProcessing;
        this.notificationTopicArn = builder.notificationTopicArn;
        this.extractMetadata = builder.extractMetadata;
        this.retryAttempts = builder.retryAttempts;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static KmsIngestionOptions defaultOptions() {
        return builder().build();
    }
    
    // Getters
    public String getClientToken() { return clientToken; }
    public boolean isMonitoringEnabled() { return monitoringEnabled; }
    public int getBatchSize() { return batchSize; }
    public boolean isParallelProcessingEnabled() { return enableParallelProcessing; }
    public String getNotificationTopicArn() { return notificationTopicArn; }
    public boolean isMetadataExtractionEnabled() { return extractMetadata; }
    public int getRetryAttempts() { return retryAttempts; }
    
    public static class Builder {
        private String clientToken;
        private boolean monitoringEnabled = true;
        private int batchSize = 100;
        private boolean enableParallelProcessing = true;
        private String notificationTopicArn;
        private boolean extractMetadata = true;
        private int retryAttempts = 3;
        
        public Builder clientToken(String clientToken) {
            this.clientToken = clientToken;
            return this;
        }
        
        public Builder enableMonitoring(boolean enable) {
            this.monitoringEnabled = enable;
            return this;
        }
        
        public Builder batchSize(int batchSize) {
            this.batchSize = batchSize;
            return this;
        }
        
        public Builder enableParallelProcessing(boolean enable) {
            this.enableParallelProcessing = enable;
            return this;
        }
        
        public Builder notificationTopicArn(String topicArn) {
            this.notificationTopicArn = topicArn;
            return this;
        }
        
        public Builder extractMetadata(boolean extract) {
            this.extractMetadata = extract;
            return this;
        }
        
        public Builder retryAttempts(int attempts) {
            this.retryAttempts = attempts;
            return this;
        }
        
        public KmsIngestionOptions build() {
            return new KmsIngestionOptions(this);
        }
    }
}