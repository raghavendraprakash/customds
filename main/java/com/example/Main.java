package com.example;

import com.example.connector.*;
import software.amazon.awssdk.services.bedrockagent.BedrockAgentClient;
import software.amazon.awssdk.services.bedrockagent.model.*;
import java.util.Arrays;

/**
 * Example usage of the Knowledge Base data source connector framework
 */
public class Main {
    
    public static void main(String[] args) {
        // Note: Authentication will be overridden in target repository
        BedrockAgentClient client = BedrockAgentClient.builder().build();
        
        try {
            // Example 1: Knowledge Base Management
            demonstrateKnowledgeBaseManagement(client);
            
            // Example 2: S3 Data Source
            demonstrateS3DataSource(client, "kb-example-123");
            
            // Example 3: Web Crawler Data Source
            demonstrateWebCrawlerDataSource(client, "kb-example-123");
            
            // Example 4: KMS Lighthouse Data Source
            demonstrateKmsLighthouseDataSource(client, "kb-example-123");
            
        } catch (ConnectorException e) {
            System.err.println("Connector Error: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Unexpected Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            client.close();
        }
    }
    
    private static void demonstrateKnowledgeBaseManagement(BedrockAgentClient client) 
            throws ConnectorException {
        System.out.println("=== Knowledge Base Management Demo ===");
        
        KnowledgeBaseManager kbManager = new KnowledgeBaseManager(client);
        
        // List existing knowledge bases
        ListKnowledgeBasesResponse listResponse = kbManager.listKnowledgeBases();
        System.out.println("Found " + listResponse.knowledgeBaseSummaries().size() + " knowledge bases");
        
        for (KnowledgeBaseSummary kb : listResponse.knowledgeBaseSummaries()) {
            System.out.println("- " + kb.name() + " (ID: " + kb.knowledgeBaseId() + ")");
        }
    }
    
    private static void demonstrateS3DataSource(BedrockAgentClient client, String knowledgeBaseId) 
            throws ConnectorException {
        System.out.println("\n=== S3 Data Source Demo ===");
        
        // Create S3 connector with custom configuration
        ConnectorConfig config = ConnectorConfig.builder()
            .maxResults(25)
            .retryAttempts(3)
            .enableValidation(true)
            .build();
            
        S3DataSourceConnector s3Connector = (S3DataSourceConnector) ConnectorFactory.createConnector(
            ConnectorFactory.ConnectorType.S3, 
            client, 
            knowledgeBaseId,
            config
        );
        
        // Create S3 configuration with multiple prefixes
        DataSourceConfiguration s3Config = s3Connector.createS3Configuration(
            "my-documents-bucket",
            Arrays.asList("documents/", "manuals/"),
            Arrays.asList("temp/", "archive/")
        );
        
        // Create data source
        CreateDataSourceResponse createResponse = s3Connector.createDataSource(
            "Corporate Documents S3 Source", 
            s3Config
        );
        
        String dataSourceId = createResponse.dataSource().dataSourceId();
        System.out.println("Created S3 data source: " + dataSourceId);
        
        // Start ingestion with client token
        StartIngestionJobResponse ingestionResponse = s3Connector.startIngestion(
            dataSourceId, 
            "ingestion-" + System.currentTimeMillis()
        );
        
        String jobId = ingestionResponse.ingestionJob().ingestionJobId();
        System.out.println("Started ingestion job: " + jobId);
        
        // Monitor ingestion status
        GetIngestionJobResponse jobStatus = s3Connector.getIngestionJob(dataSourceId, jobId);
        System.out.println("Ingestion status: " + jobStatus.ingestionJob().status());
    }
    
    private static void demonstrateWebCrawlerDataSource(BedrockAgentClient client, String knowledgeBaseId) 
            throws ConnectorException {
        System.out.println("\n=== Web Crawler Data Source Demo ===");
        
        WebCrawlerDataSourceConnector webConnector = (WebCrawlerDataSourceConnector) 
            ConnectorFactory.createConnector(
                ConnectorFactory.ConnectorType.WEB_CRAWLER, 
                client, 
                knowledgeBaseId
            );
        
        // Create web crawler configuration
        DataSourceConfiguration webConfig = webConnector.createWebCrawlerConfiguration(
            "https://docs.example.com", 
            3  // crawl depth
        );
        
        // Create data source
        CreateDataSourceResponse createResponse = webConnector.createDataSource(
            "Documentation Web Crawler", 
            webConfig
        );
        
        System.out.println("Created Web Crawler data source: " + 
                         createResponse.dataSource().dataSourceId());
        
        // List all data sources
        ListDataSourcesResponse listResponse = webConnector.listDataSources();
        System.out.println("Total data sources in KB: " + listResponse.dataSources().size());
    }
    
    private static void demonstrateKmsLighthouseDataSource(BedrockAgentClient client, String knowledgeBaseId) 
            throws ConnectorException {
        System.out.println("\n=== KMS Lighthouse Data Source Demo ===");
        
        KmsLighthouseConnector kmsConnector = (KmsLighthouseConnector) 
            ConnectorFactory.createConnector(
                ConnectorFactory.ConnectorType.KMS_LIGHTHOUSE, 
                client, 
                knowledgeBaseId
            );
        
        // Create KMS Lighthouse configuration
        DataSourceConfiguration kmsConfig = kmsConnector.createKmsLighthouseConfiguration(
            "https://lighthouse.example.com",
            "api-key-123",
            Arrays.asList("documentation", "procedures", "knowledge")
        );
        
        // Create data source
        CreateDataSourceResponse createResponse = kmsConnector.createDataSource(
            "KMS Lighthouse Repository", 
            kmsConfig
        );
        
        String dataSourceId = createResponse.dataSource().dataSourceId();
        System.out.println("Created KMS Lighthouse data source: " + dataSourceId);
        
        // Start ingestion with monitoring
        KmsIngestionOptions options = KmsIngestionOptions.builder()
            .enableMonitoring(true)
            .batchSize(50)
            .extractMetadata(true)
            .build();
            
        StartIngestionJobResponse ingestionResponse = kmsConnector.startKmsLighthouseIngestion(
            dataSourceId, 
            options
        );
        
        System.out.println("Started KMS Lighthouse ingestion: " + 
                         ingestionResponse.ingestionJob().ingestionJobId());
    }
}