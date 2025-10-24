package com.example.connector;

import software.amazon.awssdk.services.bedrockagent.BedrockAgentClient;
import software.amazon.awssdk.services.bedrockagent.model.*;
import java.util.List;
import java.util.Optional;

/**
 * Abstract base class for Knowledge Base data source connectors
 * Provides common functionality for managing data sources within Amazon Bedrock Knowledge Bases
 */
public abstract class DataSourceConnector {
    protected final BedrockAgentClient bedrockClient;
    protected final String knowledgeBaseId;
    protected final ConnectorConfig config;
    
    public DataSourceConnector(BedrockAgentClient client, String knowledgeBaseId, ConnectorConfig config) {
        this.bedrockClient = client;
        this.knowledgeBaseId = knowledgeBaseId;
        this.config = config;
    }
    
    /**
     * Create a new data source with validation
     */
    public abstract CreateDataSourceResponse createDataSource(String name, DataSourceConfiguration dataConfig) 
            throws ConnectorException;
    
    /**
     * Update existing data source
     */
    public abstract UpdateDataSourceResponse updateDataSource(String dataSourceId, DataSourceConfiguration dataConfig) 
            throws ConnectorException;
    
    /**
     * Delete data source with cleanup
     */
    public abstract DeleteDataSourceResponse deleteDataSource(String dataSourceId) throws ConnectorException;
    
    /**
     * Validate data source configuration before operations
     */
    protected abstract boolean validateConfiguration(DataSourceConfiguration config);
    
    /**
     * List all data sources for the knowledge base
     */
    public ListDataSourcesResponse listDataSources() throws ConnectorException {
        try {
            ListDataSourcesRequest request = ListDataSourcesRequest.builder()
                .knowledgeBaseId(knowledgeBaseId)
                .maxResults(config.getMaxResults())
                .build();
            return bedrockClient.listDataSources(request);
        } catch (Exception e) {
            throw new ConnectorException("Failed to list data sources", e);
        }
    }
    
    /**
     * Get data source details with error handling
     */
    public GetDataSourceResponse getDataSource(String dataSourceId) throws ConnectorException {
        try {
            GetDataSourceRequest request = GetDataSourceRequest.builder()
                .knowledgeBaseId(knowledgeBaseId)
                .dataSourceId(dataSourceId)
                .build();
            return bedrockClient.getDataSource(request);
        } catch (Exception e) {
            throw new ConnectorException("Failed to get data source: " + dataSourceId, e);
        }
    }
    
    /**
     * Start ingestion job with monitoring
     */
    public StartIngestionJobResponse startIngestion(String dataSourceId, String clientToken) throws ConnectorException {
        try {
            StartIngestionJobRequest.Builder requestBuilder = StartIngestionJobRequest.builder()
                .knowledgeBaseId(knowledgeBaseId)
                .dataSourceId(dataSourceId);
                
            if (clientToken != null) {
                requestBuilder.clientToken(clientToken);
            }
            
            return bedrockClient.startIngestionJob(requestBuilder.build());
        } catch (Exception e) {
            throw new ConnectorException("Failed to start ingestion for data source: " + dataSourceId, e);
        }
    }
    
    /**
     * Get ingestion job status
     */
    public GetIngestionJobResponse getIngestionJob(String dataSourceId, String ingestionJobId) throws ConnectorException {
        try {
            GetIngestionJobRequest request = GetIngestionJobRequest.builder()
                .knowledgeBaseId(knowledgeBaseId)
                .dataSourceId(dataSourceId)
                .ingestionJobId(ingestionJobId)
                .build();
            return bedrockClient.getIngestionJob(request);
        } catch (Exception e) {
            throw new ConnectorException("Failed to get ingestion job status", e);
        }
    }
    
    /**
     * List ingestion jobs for a data source
     */
    public ListIngestionJobsResponse listIngestionJobs(String dataSourceId) throws ConnectorException {
        try {
            ListIngestionJobsRequest request = ListIngestionJobsRequest.builder()
                .knowledgeBaseId(knowledgeBaseId)
                .dataSourceId(dataSourceId)
                .maxResults(config.getMaxResults())
                .build();
            return bedrockClient.listIngestionJobs(request);
        } catch (Exception e) {
            throw new ConnectorException("Failed to list ingestion jobs", e);
        }
    }
    
    /**
     * Check if data source exists
     */
    public boolean dataSourceExists(String dataSourceId) {
        try {
            getDataSource(dataSourceId);
            return true;
        } catch (ConnectorException e) {
            return false;
        }
    }
    
    // Getters
    public String getKnowledgeBaseId() {
        return knowledgeBaseId;
    }
    
    public ConnectorConfig getConfig() {
        return config;
    }
}