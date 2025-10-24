package com.example.connector;

import software.amazon.awssdk.services.bedrockagent.BedrockAgentClient;
import software.amazon.awssdk.services.bedrockagent.model.*;
import java.util.List;
import java.util.UUID;

/**
 * S3-specific data source connector implementation for Knowledge Bases
 */
public class S3DataSourceConnector extends DataSourceConnector {
    
    public S3DataSourceConnector(BedrockAgentClient client, String knowledgeBaseId, ConnectorConfig config) {
        super(client, knowledgeBaseId, config);
    }
    
    public S3DataSourceConnector(BedrockAgentClient client, String knowledgeBaseId) {
        super(client, knowledgeBaseId, ConnectorConfig.defaultConfig());
    }
    
    @Override
    public CreateDataSourceResponse createDataSource(String name, DataSourceConfiguration dataConfig) 
            throws ConnectorException {
        if (config.isValidationEnabled() && !validateConfiguration(dataConfig)) {
            throw new ConnectorException("Invalid S3 data source configuration");
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
            throw new ConnectorException("Failed to create S3 data source: " + name, e);
        }
    }
    
    @Override
    public UpdateDataSourceResponse updateDataSource(String dataSourceId, DataSourceConfiguration dataConfig) 
            throws ConnectorException {
        if (config.isValidationEnabled() && !validateConfiguration(dataConfig)) {
            throw new ConnectorException("Invalid S3 data source configuration");
        }
        
        try {
            UpdateDataSourceRequest request = UpdateDataSourceRequest.builder()
                .knowledgeBaseId(knowledgeBaseId)
                .dataSourceId(dataSourceId)
                .dataSourceConfiguration(dataConfig)
                .build();
            return bedrockClient.updateDataSource(request);
        } catch (Exception e) {
            throw new ConnectorException("Failed to update S3 data source: " + dataSourceId, e);
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
            throw new ConnectorException("Failed to delete S3 data source: " + dataSourceId, e);
        }
    }
    
    @Override
    protected boolean validateConfiguration(DataSourceConfiguration config) {
        if (config == null || config.type() != DataSourceType.S3) {
            return false;
        }
        
        S3DataSourceConfiguration s3Config = config.s3Configuration();
        return s3Config != null && 
               s3Config.bucketArn() != null && 
               !s3Config.bucketArn().isEmpty();
    }
    
    /**
     * Create S3 data source configuration with comprehensive options
     */
    public DataSourceConfiguration createS3Configuration(S3ConfigBuilder configBuilder) {
        S3DataSourceConfiguration.Builder s3Builder = S3DataSourceConfiguration.builder()
            .bucketArn(configBuilder.getBucketArn());
            
        if (configBuilder.getInclusionPrefixes() != null && !configBuilder.getInclusionPrefixes().isEmpty()) {
            s3Builder.inclusionPrefixes(configBuilder.getInclusionPrefixes());
        }
        
        if (configBuilder.getExclusionPrefixes() != null && !configBuilder.getExclusionPrefixes().isEmpty()) {
            s3Builder.exclusionPrefixes(configBuilder.getExclusionPrefixes());
        }
        
        if (configBuilder.getBucketOwnerAccountId() != null) {
            s3Builder.bucketOwnerAccountId(configBuilder.getBucketOwnerAccountId());
        }
        
        return DataSourceConfiguration.builder()
            .type(DataSourceType.S3)
            .s3Configuration(s3Builder.build())
            .build();
    }
    
    /**
     * Simple S3 configuration creation
     */
    public DataSourceConfiguration createS3Configuration(String bucketName, String prefix) {
        return createS3Configuration(
            S3ConfigBuilder.builder()
                .bucketName(bucketName)
                .addInclusionPrefix(prefix)
        );
    }
    
    /**
     * Create S3 configuration with multiple prefixes
     */
    public DataSourceConfiguration createS3Configuration(String bucketName, List<String> inclusionPrefixes, 
                                                        List<String> exclusionPrefixes) {
        S3ConfigBuilder builder = S3ConfigBuilder.builder()
            .bucketName(bucketName);
            
        if (inclusionPrefixes != null) {
            builder.inclusionPrefixes(inclusionPrefixes);
        }
        
        if (exclusionPrefixes != null) {
            builder.exclusionPrefixes(exclusionPrefixes);
        }
        
        return createS3Configuration(builder);
    }
}