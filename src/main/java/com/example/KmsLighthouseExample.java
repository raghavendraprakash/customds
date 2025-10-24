package com.example;

import com.example.connector.*;
import software.amazon.awssdk.services.bedrockagent.BedrockAgentClient;
import software.amazon.awssdk.services.bedrockagent.model.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Example usage of KMS Lighthouse connector with Amazon Knowledge Bases
 */
public class KmsLighthouseExample {
    
    public static void main(String[] args) {
        // Note: Authentication will be overridden in target repository
        BedrockAgentClient client = BedrockAgentClient.builder().build();
        
        try {
            // Example 1: Basic KMS Lighthouse Integration
            demonstrateBasicKmsLighthouseIntegration(client, "kb-lighthouse-123");
            
            // Example 2: Advanced KMS Lighthouse with Categories
            demonstrateAdvancedKmsLighthouseIntegration(client, "kb-lighthouse-123");
            
            // Example 3: KMS Lighthouse with Custom Authentication
            demonstrateKmsLighthouseWithAuthentication(client, "kb-lighthouse-123");
            
        } catch (ConnectorException e) {
            System.err.println("KMS Lighthouse Connector Error: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Unexpected Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            client.close();
        }
    }
    
    private static void demonstrateBasicKmsLighthouseIntegration(BedrockAgentClient client, 
                                                               String knowledgeBaseId) 
            throws ConnectorException {
        System.out.println("=== Basic KMS Lighthouse Integration ===");
        
        // Create KMS Lighthouse connector
        KmsLighthouseConnector kmsConnector = (KmsLighthouseConnector) 
            ConnectorFactory.createConnector(
                ConnectorFactory.ConnectorType.KMS_LIGHTHOUSE,
                client,
                knowledgeBaseId
            );
        
        // Configure basic KMS Lighthouse connection
        DataSourceConfiguration kmsConfig = kmsConnector.createKmsLighthouseConfiguration(
            "https://lighthouse.company.com",
            "your-api-key",
            Arrays.asList("technical-docs", "procedures", "knowledge-base")
        );
        
        // Create data source
        CreateDataSourceResponse createResponse = kmsConnector.createDataSource(
            "KMS Lighthouse Technical Documentation",
            kmsConfig
        );
        
        String dataSourceId = createResponse.dataSource().dataSourceId();
        System.out.println("Created KMS Lighthouse data source: " + dataSourceId);
        
        // Start ingestion with default options
        KmsIngestionOptions ingestionOptions = KmsIngestionOptions.builder()
            .enableMonitoring(true)
            .batchSize(50)
            .extractMetadata(true)
            .build();
            
        StartIngestionJobResponse ingestionResponse = kmsConnector.startKmsLighthouseIngestion(
            dataSourceId,
            ingestionOptions
        );
        
        String jobId = ingestionResponse.ingestionJob().ingestionJobId();
        System.out.println("Started KMS Lighthouse ingestion job: " + jobId);
        
        // Monitor ingestion progress
        KmsIngestionStats stats = kmsConnector.getIngestionStats(dataSourceId, jobId);
        System.out.println("Ingestion stats: " + stats);
    }
    
    private static void demonstrateAdvancedKmsLighthouseIntegration(BedrockAgentClient client,
                                                                  String knowledgeBaseId) 
            throws ConnectorException {
        System.out.println("\n=== Advanced KMS Lighthouse Integration ===");
        
        // Create connector with custom configuration
        ConnectorConfig connectorConfig = ConnectorConfig.builder()
            .maxResults(100)
            .retryAttempts(5)
            .retryDelayMs(2000)
            .enableValidation(true)
            .build();
            
        KmsLighthouseConnector kmsConnector = (KmsLighthouseConnector) 
            ConnectorFactory.createConnector(
                ConnectorFactory.ConnectorType.KMS_LIGHTHOUSE,
                client,
                knowledgeBaseId,
                connectorConfig
            );
        
        // Configure KMS Lighthouse with specific categories and custom headers
        Map<String, String> customHeaders = new HashMap<>();
        customHeaders.put("X-Department", "Engineering");
        customHeaders.put("X-Access-Level", "Internal");
        
        DataSourceConfiguration kmsConfig = kmsConnector.createKmsLighthouseConfigurationByCategory(
            "https://lighthouse.company.com",
            "advanced-api-key",
            Arrays.asList("engineering", "architecture", "best-practices", "troubleshooting"),
            customHeaders
        );
        
        // Create data source
        CreateDataSourceResponse createResponse = kmsConnector.createDataSource(
            "KMS Lighthouse Engineering Knowledge",
            kmsConfig
        );
        
        String dataSourceId = createResponse.dataSource().dataSourceId();
        System.out.println("Created advanced KMS Lighthouse data source: " + dataSourceId);
        
        // Start ingestion with advanced options
        KmsIngestionOptions advancedOptions = KmsIngestionOptions.builder()
            .clientToken("engineering-docs-" + System.currentTimeMillis())
            .enableMonitoring(true)
            .batchSize(25)
            .enableParallelProcessing(true)
            .extractMetadata(true)
            .retryAttempts(5)
            .build();
            
        StartIngestionJobResponse ingestionResponse = kmsConnector.startKmsLighthouseIngestion(
            dataSourceId,
            advancedOptions
        );
        
        System.out.println("Started advanced ingestion: " + 
                         ingestionResponse.ingestionJob().ingestionJobId());
    }
    
    private static void demonstrateKmsLighthouseWithAuthentication(BedrockAgentClient client,
                                                                 String knowledgeBaseId) 
            throws ConnectorException {
        System.out.println("\n=== KMS Lighthouse with Authentication ===");
        
        KmsLighthouseConnector kmsConnector = (KmsLighthouseConnector) 
            ConnectorFactory.createConnector(
                ConnectorFactory.ConnectorType.KMS_LIGHTHOUSE,
                client,
                knowledgeBaseId
            );
        
        // Configure authentication
        KmsAuthenticationConfig authConfig = KmsAuthenticationConfig.builder()
            .bearerToken("your-bearer-token")
            .customHeader("X-API-Version", "v2")
            .customHeader("X-Client-ID", "knowledge-base-connector")
            .build();
        
        // Build comprehensive KMS Lighthouse configuration
        KmsLighthouseConfig kmsConfig = KmsLighthouseConfig.builder()
            .baseUrl("https://secure-lighthouse.company.com")
            .apiKey("secure-api-key")
            .addDocumentEndpoint("https://secure-lighthouse.company.com/api/secure/documents")
            .addDocumentEndpoint("https://secure-lighthouse.company.com/api/confidential/documents")
            .addInclusionPattern(".*\\.(pdf|docx|md)$")
            .addInclusionPattern(".*\\/secure\\/.*")
            .addInclusionPattern(".*\\/confidential\\/.*")
            .addExclusionPattern(".*\\/draft\\/.*")
            .addExclusionPattern(".*\\/personal\\/.*")
            .rateLimit(20) // Conservative rate for secure endpoints
            .authenticationConfig(authConfig)
            .enableMetadataExtraction(true)
            .maxDocumentSize(20971520) // 20MB for secure documents
            .build();
        
        DataSourceConfiguration dataSourceConfig = kmsConnector.createKmsLighthouseConfiguration(kmsConfig);
        
        // Create secure data source
        CreateDataSourceResponse createResponse = kmsConnector.createDataSource(
            "KMS Lighthouse Secure Documents",
            dataSourceConfig
        );
        
        String dataSourceId = createResponse.dataSource().dataSourceId();
        System.out.println("Created secure KMS Lighthouse data source: " + dataSourceId);
        
        // Start secure ingestion
        KmsIngestionOptions secureOptions = KmsIngestionOptions.builder()
            .clientToken("secure-ingestion-" + System.currentTimeMillis())
            .enableMonitoring(true)
            .batchSize(10) // Smaller batches for secure content
            .enableParallelProcessing(false) // Sequential processing for security
            .extractMetadata(true)
            .retryAttempts(3)
            .build();
            
        StartIngestionJobResponse ingestionResponse = kmsConnector.startKmsLighthouseIngestion(
            dataSourceId,
            secureOptions
        );
        
        System.out.println("Started secure ingestion: " + 
                         ingestionResponse.ingestionJob().ingestionJobId());
        
        // List all KMS Lighthouse data sources
        ListDataSourcesResponse listResponse = kmsConnector.listDataSources();
        System.out.println("Total KMS Lighthouse data sources: " + listResponse.dataSources().size());
        
        for (DataSource ds : listResponse.dataSources()) {
            System.out.println("- " + ds.name() + " (ID: " + ds.dataSourceId() + 
                             ", Status: " + ds.status() + ")");
        }
    }
}