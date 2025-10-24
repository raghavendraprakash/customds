package com.example.connector;

import software.amazon.awssdk.services.bedrockagent.BedrockAgentClient;
import software.amazon.awssdk.services.bedrockagent.model.*;
import java.util.UUID;

/**
 * Confluence data source connector implementation for Knowledge Bases
 */
public class ConfluenceDataSourceConnector extends DataSourceConnector {
    
    public ConfluenceDataSourceConnector(BedrockAgentClient client, String knowledgeBaseId, ConnectorConfig config) {
        super(client, knowledgeBaseId, config);
    }
    
    @Override
    public CreateDataSourceResponse createDataSource(String name, DataSourceConfiguration dataConfig) 
            throws ConnectorException {
        if (config.isValidationEnabled() && !validateConfiguration(dataConfig)) {
            throw new ConnectorException("Invalid Confluence data source configuration");
        }
        
        try {
            CreateDataSourceRequest request = CreateDataSourceRequest.builder()
                .knowledgeBaseId(knowledgeBaseId)
                .name(name)
                .dataSourceConfiguration(dataConfig)
                .clientToken(UUID.randomUUID().toString())
                .build();
            return bedrockClient.createDataSource(request);
        } catch (Exception e) {
            throw new ConnectorException("Failed to create Confluence data source: " + name, e);
        }
    }
    
    @Override
    public UpdateDataSourceResponse updateDataSource(String dataSourceId, DataSourceConfiguration dataConfig) 
            throws ConnectorException {
        if (config.isValidationEnabled() && !validateConfiguration(dataConfig)) {
            throw new ConnectorException("Invalid Confluence data source configuration");
        }
        
        try {
            UpdateDataSourceRequest request = UpdateDataSourceRequest.builder()
                .knowledgeBaseId(knowledgeBaseId)
                .dataSourceId(dataSourceId)
                .dataSourceConfiguration(dataConfig)
                .build();
            return bedrockClient.updateDataSource(request);
        } catch (Exception e) {
            throw new ConnectorException("Failed to update Confluence data source: " + dataSourceId, e);
        }
    }
    
    @Override
    public DeleteDataSourceResponse deleteDataSource(String dataSourceId) throws ConnectorException {
        try {
            DeleteDataSourceRequest request = DeleteDataSourceRequest.builder()
                .knowledgeBaseId(knowledgeBaseId)
                .dataSourceId(dataSourceId)
                .build();
            return bedrockClient.deleteDataSource(request);
        } catch (Exception e) {
            throw new ConnectorException("Failed to delete Confluence data source: " + dataSourceId, e);
        }
    }
    
    @Override
    protected boolean validateConfiguration(DataSourceConfiguration config) {
        if (config == null || config.type() != DataSourceType.CONFLUENCE) {
            return false;
        }
        
        ConfluenceDataSourceConfiguration confluenceConfig = config.confluenceConfiguration();
        return confluenceConfig != null && 
               confluenceConfig.sourceConfiguration() != null;
    }
    
    /**
     * Create Confluence configuration
     */
    public DataSourceConfiguration createConfluenceConfiguration(String serverUrl, String secretArn) {
        ConfluenceSourceConfiguration sourceConfig = ConfluenceSourceConfiguration.builder()
            .serverUrl(serverUrl)
            .authType(ConfluenceAuthType.BASIC)
            .credentialsSecretArn(secretArn)
            .build();
            
        ConfluenceCrawlerConfiguration crawlerConfig = ConfluenceCrawlerConfiguration.builder()
            .filterConfiguration(CrawlFilterConfiguration.builder()
                .type(CrawlFilterConfigurationType.PATTERN)
                .build())
            .build();
            
        ConfluenceDataSourceConfiguration confluenceConfig = ConfluenceDataSourceConfiguration.builder()
            .sourceConfiguration(sourceConfig)
            .crawlerConfiguration(crawlerConfig)
            .build();
            
        return DataSourceConfiguration.builder()
            .type(DataSourceType.CONFLUENCE)
            .confluenceConfiguration(confluenceConfig)
            .build();
    }
}