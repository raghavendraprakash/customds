package com.example.connector;

import software.amazon.awssdk.services.bedrockagent.BedrockAgentClient;

/**
 * Factory class for creating Knowledge Base data source connectors
 */
public class ConnectorFactory {
    
    public enum ConnectorType {
        S3,
        WEB_CRAWLER,
        SHAREPOINT,
        CONFLUENCE
    }
    
    /**
     * Create a data source connector with default configuration
     */
    public static DataSourceConnector createConnector(ConnectorType type, 
                                                     BedrockAgentClient client, 
                                                     String knowledgeBaseId) {
        return createConnector(type, client, knowledgeBaseId, ConnectorConfig.defaultConfig());
    }
    
    /**
     * Create a data source connector with custom configuration
     */
    public static DataSourceConnector createConnector(ConnectorType type, 
                                                     BedrockAgentClient client, 
                                                     String knowledgeBaseId,
                                                     ConnectorConfig config) {
        switch (type) {
            case S3:
                return new S3DataSourceConnector(client, knowledgeBaseId, config);
            case WEB_CRAWLER:
                return new WebCrawlerDataSourceConnector(client, knowledgeBaseId, config);
            case SHAREPOINT:
                return new SharePointDataSourceConnector(client, knowledgeBaseId, config);
            case CONFLUENCE:
                return new ConfluenceDataSourceConnector(client, knowledgeBaseId, config);
            default:
                throw new IllegalArgumentException("Unsupported connector type: " + type);
        }
    }
    
    /**
     * Get available connector types
     */
    public static ConnectorType[] getAvailableTypes() {
        return ConnectorType.values();
    }
}