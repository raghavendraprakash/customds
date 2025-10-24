package com.example.connector;

import software.amazon.awssdk.services.bedrockagent.BedrockAgentClient;
import software.amazon.awssdk.services.bedrockagent.model.*;
import java.util.UUID;
import java.util.List;
import java.util.Map;

/**
 * KMS Lighthouse repository connector implementation for Knowledge Bases
 * Integrates with KMS Lighthouse document management system
 */
public class KmsLighthouseConnector extends DataSourceConnector {
    
    public KmsLighthouseConnector(BedrockAgentClient client, String knowledgeBaseId, ConnectorConfig config) {
        super(client, knowledgeBaseId, config);
    }
    
    public KmsLighthouseConnector(BedrockAgentClient client, String knowledgeBaseId) {
        super(client, knowledgeBaseId, ConnectorConfig.defaultConfig());
    }
    
    @Override
    public CreateDataSourceResponse createDataSource(String name, DataSourceConfiguration dataConfig) 
            throws ConnectorException {
        if (config.isValidationEnabled() && !validateConfiguration(dataConfig)) {
            throw new ConnectorException("Invalid KMS Lighthouse data source configuration");
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
            throw new ConnectorException("Failed to create KMS Lighthouse data source: " + name, e);
        }
    }
    
    @Override
    public UpdateDataSourceResponse updateDataSource(String dataSourceId, DataSourceConfiguration dataConfig) 
            throws ConnectorException {
        if (config.isValidationEnabled() && !validateConfiguration(dataConfig)) {
            throw new ConnectorException("Invalid KMS Lighthouse data source configuration");
        }
        
        try {
            UpdateDataSourceRequest request = UpdateDataSourceRequest.builder()
                .knowledgeBaseId(knowledgeBaseId)
                .dataSourceId(dataSourceId)
                .dataSourceConfiguration(dataConfig)
                .build();
            return bedrockClient.updateDataSource(request);
        } catch (Exception e) {
            throw new ConnectorException("Failed to update KMS Lighthouse data source: " + dataSourceId, e);
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
            throw new ConnectorException("Failed to delete KMS Lighthouse data source: " + dataSourceId, e);
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
     * Create KMS Lighthouse configuration for document ingestion
     */
    public DataSourceConfiguration createKmsLighthouseConfiguration(KmsLighthouseConfig kmsConfig) {
        // Build URL configuration for KMS Lighthouse API endpoints
        UrlConfiguration.Builder urlConfigBuilder = UrlConfiguration.builder();
        
        // Add seed URLs for document discovery
        for (String endpoint : kmsConfig.getDocumentEndpoints()) {
            urlConfigBuilder.seedUrls(SeedUrl.builder().url(endpoint).build());
        }
        
        // Configure inclusion patterns for KMS Lighthouse document types
        if (kmsConfig.getInclusionPatterns() != null && !kmsConfig.getInclusionPatterns().isEmpty()) {
            urlConfigBuilder.inclusionFilters(kmsConfig.getInclusionPatterns().toArray(new String[0]));
        }
        
        // Configure exclusion patterns
        if (kmsConfig.getExclusionPatterns() != null && !kmsConfig.getExclusionPatterns().isEmpty()) {
            urlConfigBuilder.exclusionFilters(kmsConfig.getExclusionPatterns().toArray(new String[0]));
        }
        
        WebSourceConfiguration sourceConfig = WebSourceConfiguration.builder()
            .urlConfiguration(urlConfigBuilder.build())
            .build();
        
        // Configure crawler with KMS Lighthouse specific settings
        WebCrawlerConfiguration.Builder crawlerBuilder = WebCrawlerConfiguration.builder();
        
        // Set rate limits to respect KMS Lighthouse API limits
        WebCrawlerLimits limits = WebCrawlerLimits.builder()
            .rateLimit(kmsConfig.getRateLimit())
            .build();
        crawlerBuilder.crawlerLimits(limits);
        
        // Configure authentication if provided
        if (kmsConfig.getAuthenticationConfig() != null) {
            // Note: In a real implementation, you would configure authentication
            // This might involve setting up custom headers or OAuth tokens
            // For now, we'll use the basic crawler configuration
        }
        
        WebDataSourceConfiguration webConfig = WebDataSourceConfiguration.builder()
            .sourceConfiguration(sourceConfig)
            .crawlerConfiguration(crawlerBuilder.build())
            .build();
        
        return DataSourceConfiguration.builder()
            .type(DataSourceType.WEB)
            .webConfiguration(webConfig)
            .build();
    }
    
    /**
     * Create simple KMS Lighthouse configuration with basic settings
     */
    public DataSourceConfiguration createKmsLighthouseConfiguration(String baseUrl, 
                                                                   String apiKey,
                                                                   List<String> repositories) {
        KmsLighthouseConfig.Builder configBuilder = KmsLighthouseConfig.builder()
            .baseUrl(baseUrl)
            .apiKey(apiKey)
            .rateLimit(50); // Conservative rate limit
        
        // Add repository-specific endpoints
        for (String repo : repositories) {
            configBuilder.addDocumentEndpoint(baseUrl + "/api/repositories/" + repo + "/documents");
        }
        
        // Add common document type patterns
        configBuilder.addInclusionPattern(".*\\.(pdf|doc|docx|txt|md)$")
                    .addInclusionPattern(".*\\/documents\\/.*")
                    .addExclusionPattern(".*\\/temp\\/.*")
                    .addExclusionPattern(".*\\/archive\\/.*");
        
        return createKmsLighthouseConfiguration(configBuilder.build());
    }
    
    /**
     * Create configuration for specific KMS Lighthouse document categories
     */
    public DataSourceConfiguration createKmsLighthouseConfigurationByCategory(String baseUrl,
                                                                             String apiKey,
                                                                             List<String> categories,
                                                                             Map<String, String> customHeaders) {
        KmsLighthouseConfig.Builder configBuilder = KmsLighthouseConfig.builder()
            .baseUrl(baseUrl)
            .apiKey(apiKey)
            .rateLimit(30);
        
        // Add category-specific endpoints
        for (String category : categories) {
            configBuilder.addDocumentEndpoint(baseUrl + "/api/categories/" + category + "/documents");
        }
        
        // Configure authentication with custom headers
        if (customHeaders != null && !customHeaders.isEmpty()) {
            KmsAuthenticationConfig authConfig = KmsAuthenticationConfig.builder()
                .customHeaders(customHeaders)
                .build();
            configBuilder.authenticationConfig(authConfig);
        }
        
        // Add category-specific inclusion patterns
        configBuilder.addInclusionPattern(".*\\/knowledge\\/.*")
                    .addInclusionPattern(".*\\/procedures\\/.*")
                    .addInclusionPattern(".*\\/guidelines\\/.*")
                    .addExclusionPattern(".*\\/draft\\/.*")
                    .addExclusionPattern(".*\\/obsolete\\/.*");
        
        return createKmsLighthouseConfiguration(configBuilder.build());
    }
    
    /**
     * Start ingestion with KMS Lighthouse specific monitoring
     */
    public StartIngestionJobResponse startKmsLighthouseIngestion(String dataSourceId, 
                                                               KmsIngestionOptions options) 
            throws ConnectorException {
        String clientToken = options.getClientToken();
        if (clientToken == null) {
            clientToken = "kms-lighthouse-" + System.currentTimeMillis();
        }
        
        try {
            StartIngestionJobResponse response = startIngestion(dataSourceId, clientToken);
            
            // Log KMS Lighthouse specific ingestion details
            System.out.println("Started KMS Lighthouse ingestion:");
            System.out.println("- Job ID: " + response.ingestionJob().ingestionJobId());
            System.out.println("- Data Source: " + dataSourceId);
            System.out.println("- Client Token: " + clientToken);
            
            if (options.isMonitoringEnabled()) {
                // In a real implementation, you might set up monitoring hooks here
                System.out.println("- Monitoring: Enabled");
            }
            
            return response;
        } catch (Exception e) {
            throw new ConnectorException("Failed to start KMS Lighthouse ingestion for data source: " + dataSourceId, e);
        }
    }
    
    /**
     * Get KMS Lighthouse specific ingestion statistics
     */
    public KmsIngestionStats getIngestionStats(String dataSourceId, String ingestionJobId) 
            throws ConnectorException {
        GetIngestionJobResponse response = getIngestionJob(dataSourceId, ingestionJobId);
        IngestionJob job = response.ingestionJob();
        
        return KmsIngestionStats.builder()
            .jobId(ingestionJobId)
            .status(job.status().toString())
            .startTime(job.startedAt())
            .endTime(job.updatedAt())
            .statistics(job.statistics())
            .build();
    }
}