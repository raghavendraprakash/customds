package com.example.connector;

import software.amazon.awssdk.services.bedrockagent.model.IngestionJobStatistics;
import java.time.Instant;

/**
 * Statistics and status information for KMS Lighthouse ingestion jobs
 */
public class KmsIngestionStats {
    private final String jobId;
    private final String status;
    private final Instant startTime;
    private final Instant endTime;
    private final IngestionJobStatistics statistics;
    private final long documentsProcessed;
    private final long documentsSuccessful;
    private final long documentsFailed;
    private final double processingRate;
    
    private KmsIngestionStats(Builder builder) {
        this.jobId = builder.jobId;
        this.status = builder.status;
        this.startTime = builder.startTime;
        this.endTime = builder.endTime;
        this.statistics = builder.statistics;
        this.documentsProcessed = builder.documentsProcessed;
        this.documentsSuccessful = builder.documentsSuccessful;
        this.documentsFailed = builder.documentsFailed;
        this.processingRate = builder.processingRate;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    // Getters
    public String getJobId() { return jobId; }
    public String getStatus() { return status; }
    public Instant getStartTime() { return startTime; }
    public Instant getEndTime() { return endTime; }
    public IngestionJobStatistics getStatistics() { return statistics; }
    public long getDocumentsProcessed() { return documentsProcessed; }
    public long getDocumentsSuccessful() { return documentsSuccessful; }
    public long getDocumentsFailed() { return documentsFailed; }
    public double getProcessingRate() { return processingRate; }
    
    /**
     * Get processing duration in seconds
     */
    public long getProcessingDurationSeconds() {
        if (startTime != null && endTime != null) {
            return endTime.getEpochSecond() - startTime.getEpochSecond();
        }
        return 0;
    }
    
    /**
     * Get success rate as percentage
     */
    public double getSuccessRate() {
        if (documentsProcessed > 0) {
            return (double) documentsSuccessful / documentsProcessed * 100.0;
        }
        return 0.0;
    }
    
    /**
     * Check if ingestion is complete
     */
    public boolean isComplete() {
        return "COMPLETE".equalsIgnoreCase(status) || 
               "FAILED".equalsIgnoreCase(status) ||
               "STOPPED".equalsIgnoreCase(status);
    }
    
    /**
     * Check if ingestion was successful
     */
    public boolean isSuccessful() {
        return "COMPLETE".equalsIgnoreCase(status);
    }
    
    @Override
    public String toString() {
        return String.format(
            "KmsIngestionStats{jobId='%s', status='%s', processed=%d, successful=%d, failed=%d, successRate=%.2f%%}",
            jobId, status, documentsProcessed, documentsSuccessful, documentsFailed, getSuccessRate()
        );
    }
    
    public static class Builder {
        private String jobId;
        private String status;
        private Instant startTime;
        private Instant endTime;
        private IngestionJobStatistics statistics;
        private long documentsProcessed;
        private long documentsSuccessful;
        private long documentsFailed;
        private double processingRate;
        
        public Builder jobId(String jobId) {
            this.jobId = jobId;
            return this;
        }
        
        public Builder status(String status) {
            this.status = status;
            return this;
        }
        
        public Builder startTime(Instant startTime) {
            this.startTime = startTime;
            return this;
        }
        
        public Builder endTime(Instant endTime) {
            this.endTime = endTime;
            return this;
        }
        
        public Builder statistics(IngestionJobStatistics statistics) {
            this.statistics = statistics;
            if (statistics != null) {
                this.documentsProcessed = statistics.numberOfDocumentsScanned() != null ? 
                    statistics.numberOfDocumentsScanned() : 0;
                this.documentsSuccessful = statistics.numberOfDocumentsIndexed() != null ? 
                    statistics.numberOfDocumentsIndexed() : 0;
                this.documentsFailed = statistics.numberOfDocumentsFailed() != null ? 
                    statistics.numberOfDocumentsFailed() : 0;
            }
            return this;
        }
        
        public Builder documentsProcessed(long processed) {
            this.documentsProcessed = processed;
            return this;
        }
        
        public Builder documentsSuccessful(long successful) {
            this.documentsSuccessful = successful;
            return this;
        }
        
        public Builder documentsFailed(long failed) {
            this.documentsFailed = failed;
            return this;
        }
        
        public Builder processingRate(double rate) {
            this.processingRate = rate;
            return this;
        }
        
        public KmsIngestionStats build() {
            return new KmsIngestionStats(this);
        }
    }
}