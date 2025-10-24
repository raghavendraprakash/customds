package com.example.connector;

import software.amazon.awssdk.services.bedrockagent.BedrockAgentClient;
import software.amazon.awssdk.services.bedrockagent.model.*;
import java.util.List;

/**
 * Manager class for Knowledge Base operations
 */
public class KnowledgeBaseManager {
    private final BedrockAgentClient bedrockClient;
    
    public KnowledgeBaseManager(BedrockAgentClient client) {
        this.bedrockClient = client;
    }
    
    /**
     * Create a new Knowledge Base
     */
    public CreateKnowledgeBaseResponse createKnowledgeBase(String name, String description, 
                                                          String roleArn, 
                                                          KnowledgeBaseConfiguration config,
                                                          StorageConfiguration storageConfig) 
            throws ConnectorException {
        try {
            CreateKnowledgeBaseRequest request = CreateKnowledgeBaseRequest.builder()
                .name(name)
                .description(description)
                .roleArn(roleArn)
                .knowledgeBaseConfiguration(config)
                .storageConfiguration(storageConfig)
                .build();
            return bedrockClient.createKnowledgeBase(request);
        } catch (Exception e) {
            throw new ConnectorException("Failed to create knowledge base: " + name, e);
        }
    }
    
    /**
     * Get Knowledge Base details
     */
    public GetKnowledgeBaseResponse getKnowledgeBase(String knowledgeBaseId) throws ConnectorException {
        try {
            GetKnowledgeBaseRequest request = GetKnowledgeBaseRequest.builder()
                .knowledgeBaseId(knowledgeBaseId)
                .build();
            return bedrockClient.getKnowledgeBase(request);
        } catch (Exception e) {
            throw new ConnectorException("Failed to get knowledge base: " + knowledgeBaseId, e);
        }
    }
    
    /**
     * List all Knowledge Bases
     */
    public ListKnowledgeBasesResponse listKnowledgeBases() throws ConnectorException {
        try {
            ListKnowledgeBasesRequest request = ListKnowledgeBasesRequest.builder()
                .maxResults(50)
                .build();
            return bedrockClient.listKnowledgeBases(request);
        } catch (Exception e) {
            throw new ConnectorException("Failed to list knowledge bases", e);
        }
    }
    
    /**
     * Delete Knowledge Base
     */
    public DeleteKnowledgeBaseResponse deleteKnowledgeBase(String knowledgeBaseId) throws ConnectorException {
        try {
            DeleteKnowledgeBaseRequest request = DeleteKnowledgeBaseRequest.builder()
                .knowledgeBaseId(knowledgeBaseId)
                .build();
            return bedrockClient.deleteKnowledgeBase(request);
        } catch (Exception e) {
            throw new ConnectorException("Failed to delete knowledge base: " + knowledgeBaseId, e);
        }
    }
    
    /**
     * Create default OpenSearch Serverless storage configuration
     */
    public StorageConfiguration createOpenSearchServerlessConfig(String collectionArn, 
                                                               String vectorIndexName,
                                                               String textField,
                                                               String vectorField,
                                                               String metadataField) {
        OpenSearchServerlessFieldMapping fieldMapping = OpenSearchServerlessFieldMapping.builder()
            .textField(textField)
            .vectorField(vectorField)
            .metadataField(metadataField)
            .build();
            
        OpenSearchServerlessConfiguration osConfig = OpenSearchServerlessConfiguration.builder()
            .collectionArn(collectionArn)
            .vectorIndexName(vectorIndexName)
            .fieldMapping(fieldMapping)
            .build();
            
        return StorageConfiguration.builder()
            .type(KnowledgeBaseStorageType.OPENSEARCH_SERVERLESS)
            .opensearchServerlessConfiguration(osConfig)
            .build();
    }
}