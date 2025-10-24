package com.example.connector;

import software.amazon.awssdk.services.bedrockagent.BedrockAgentClient;
import software.amazon.awssdk.services.bedrockagent.model.*;
import java.util.UUID;

/**
 * SharePoint data source connector implementation for Knowledge Bases
 */
public class SharePointDataSourceConnector extends DataSourceConnector {
    
    public SharePointDataSourceConnector(BedrockAgentClient client, String knowledgeBaseId, ConnectorConfig config) {
        super(client, knowledgeBaseId, config);
    }
    
    @Override
    public CreateDataSourceResponse createDataSource(String name, DataSourceConfiguration dataConfig) 
            throws ConnectorException {
        if (config.isValidationEnabled() && !validateConfiguration(dataConfig)) {
            throw new ConnectorException("Invalid SharePoint data source configuration");
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
            throw new ConnectorException("Failed to create SharePoint data source: " + name, e);
        }
    }
    
    @Override
    public UpdateDataSourceResponse updateDataSource(String dataSourceId, DataSourceConfiguration dataConfig) 
            throws ConnectorException {
        if (config.isValidationEnabled() && !validateConfiguration(dataConfig)) {
            throw new ConnectorException("Invalid SharePoint data source configuration");
        }
        
        try {
            UpdateDataSourceRequest request = UpdateDataSourceRequest.builder()
                .knowledgeBaseId(knowledgeBaseId)
                .dataSourceId(dataSourceId)
                .dataSourceConfiguration(dataConfig)
                .build();
            return bedrockClient.updateDataSource(request);
        } catch (Exception e) {
            throw new ConnectorException("Failed to update SharePoint data source: " + dataSourceId, e);
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
            throw new ConnectorException("Failed to delete SharePoint data source: " + dataSourceId, e);
        }
    }
    
    @Override
    protected boolean validateConfiguration(DataSourceConfiguration config) {
        if (config == null || config.type() != DataSourceType.SHAREPOINT) {
            return false;
        }
        
        SharePointDataSourceConfiguration sharePointConfig = config.sharePointConfiguration();
        return sharePointConfig != null && 
               sharePointConfig.sourceConfiguration() != null;
    }
    
    /**
     * Create SharePoint configuration
     */
    public DataSourceConfiguration createSharePointConfiguration(String siteUrl, String tenantId, 
                                                               String secretArn) {
        SharePointSourceConfiguration sourceConfig = SharePointSourceConfiguration.builder()
            .siteUrls(siteUrl)
            .tenantId(tenantId)
            .build();
            
        SharePointCrawlerConfiguration crawlerConfig = SharePointCrawlerConfiguration.builder()
            .filterConfiguration(CrawlFilterConfiguration.builder()
                .type(CrawlFilterConfigurationType.PATTERN)
                .build())
            .build();
            
        SharePointDataSourceConfiguration sharePointConfig = SharePointDataSourceConfiguration.builder()
            .sourceConfiguration(sourceConfig)
            .crawlerConfiguration(crawlerConfig)
            .build();
            
        return DataSourceConfiguration.builder()
            .type(DataSourceType.SHAREPOINT)
            .sharePointConfiguration(sharePointConfig)
            .build();
    }
}