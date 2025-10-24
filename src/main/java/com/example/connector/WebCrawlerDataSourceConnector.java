package com.example.connector;

import software.amazon.awssdk.services.bedrockagent.BedrockAgentClient;
import software.amazon.awssdk.services.bedrockagent.model.*;
import java.util.List;
import java.util.UUID;

/**
 * Web Crawler data source connector implementation for Knowledge Bases
 */
public class WebCrawlerDataSourceConnector extends DataSourceConnector {
    
    public WebCrawlerDataSourceConnector(BedrockAgentClient client, String knowledgeBaseId, ConnectorConfig config) {
        super(client, knowledgeBaseId, config);
    }
    
    @Override
    public CreateDataSourceResponse createDataSource(String name, DataSourceConfiguration dataConfig) 
            throws ConnectorException {
        if (config.isValidationEnabled() && !validateConfiguration(dataConfig)) {
            throw new ConnectorException("Invalid Web Crawler data source configuration");
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
            throw new ConnectorException("Failed to create Web Crawler data source: " + name, e);
        }
    }
    
    @Override
    public UpdateDataSourceResponse updateDataSource(String dataSourceId, DataSourceConfiguration dataConfig) 
            throws ConnectorException {
        if (config.isValidationEnabled() && !validateConfiguration(dataConfig)) {
            throw new ConnectorException("Invalid Web Crawler data source configuration");
        }
        
        try {
            UpdateDataSourceRequest request = UpdateDataSourceRequest.builder()
                .knowledgeBaseId(knowledgeBaseId)
                .dataSourceId(dataSourceId)
                .dataSourceConfiguration(dataConfig)
                .build();
            return bedrockClient.updateDataSource(request);
        } catch (Exception e) {
            throw new ConnectorException("Failed to update Web Crawler data source: " + dataSourceId, e);
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
            throw new ConnectorException("Failed to delete Web Crawler data source: " + dataSourceId, e);
        }
    }
    
    @Override
    protected boolean validateConfiguration(DataSourceConfiguration config) {
        if (config == null || config.type() != DataSourceType.WEB) {
            return false;
        }
        
        WebDataSourceConfiguration webConfig = config.webConfiguration();
        return webConfig != null && 
               webConfig.sourceConfiguration() != null &&
               webConfig.sourceConfiguration().urlConfiguration() != null;
    }
    
    /**
     * Create Web Crawler configuration for single URL
     */
    public DataSourceConfiguration createWebCrawlerConfiguration(String startUrl, int crawlDepth) {
        UrlConfiguration urlConfig = UrlConfiguration.builder()
            .seedUrls(SeedUrl.builder().url(startUrl).build())
            .build();
            
        WebSourceConfiguration sourceConfig = WebSourceConfiguration.builder()
            .urlConfiguration(urlConfig)
            .build();
            
        WebCrawlerConfiguration crawlerConfig = WebCrawlerConfiguration.builder()
            .crawlerLimits(WebCrawlerLimits.builder()
                .rateLimit(100)
                .build())
            .build();
            
        WebDataSourceConfiguration webConfig = WebDataSourceConfiguration.builder()
            .sourceConfiguration(sourceConfig)
            .crawlerConfiguration(crawlerConfig)
            .build();
            
        return DataSourceConfiguration.builder()
            .type(DataSourceType.WEB)
            .webConfiguration(webConfig)
            .build();
    }
}