# Amazon Bedrock Knowledge Base Data Source Connector

A comprehensive Java framework for managing data sources in Amazon Bedrock Knowledge Bases using object-oriented design principles.

## Features

- **Multiple Data Source Types**: S3, Web Crawler, SharePoint, Confluence
- **Knowledge Base Management**: Create, update, delete, and list knowledge bases
- **Robust Error Handling**: Custom exceptions and validation
- **Configurable**: Flexible configuration options for different use cases
- **Type Safety**: Strong typing with AWS SDK v2
- **Builder Patterns**: Easy-to-use configuration builders

## Architecture

### Core Components

- `DataSourceConnector` - Abstract base class for all connectors
- `ConnectorFactory` - Factory for creating connector instances
- `ConnectorConfig` - Configuration management
- `KnowledgeBaseManager` - Knowledge base operations
- `ConnectorException` - Custom exception handling

### Supported Data Sources

1. **S3DataSourceConnector** - Amazon S3 buckets
2. **WebCrawlerDataSourceConnector** - Web crawling
3. **SharePointDataSourceConnector** - Microsoft SharePoint
4. **ConfluenceDataSourceConnector** - Atlassian Confluence

## Usage Examples

### Basic S3 Data Source

```java
BedrockAgentClient client = BedrockAgentClient.builder().build();
String knowledgeBaseId = "your-kb-id";

S3DataSourceConnector connector = (S3DataSourceConnector) 
    ConnectorFactory.createConnector(
        ConnectorFactory.ConnectorType.S3, 
        client, 
        knowledgeBaseId
    );

DataSourceConfiguration config = connector.createS3Configuration(
    "my-bucket", 
    "documents/"
);

CreateDataSourceResponse response = connector.createDataSource(
    "My Documents", 
    config
);
```

### Advanced S3 Configuration

```java
DataSourceConfiguration config = connector.createS3Configuration(
    "my-bucket",
    Arrays.asList("docs/", "manuals/"),  // inclusion prefixes
    Arrays.asList("temp/", "archive/")   // exclusion prefixes
);
```

### Web Crawler Data Source

```java
WebCrawlerDataSourceConnector webConnector = (WebCrawlerDataSourceConnector)
    ConnectorFactory.createConnector(
        ConnectorFactory.ConnectorType.WEB_CRAWLER,
        client,
        knowledgeBaseId
    );

DataSourceConfiguration webConfig = webConnector.createWebCrawlerConfiguration(
    "https://docs.example.com",
    3  // crawl depth
);
```

### Knowledge Base Management

```java
KnowledgeBaseManager kbManager = new KnowledgeBaseManager(client);

// List knowledge bases
ListKnowledgeBasesResponse kbList = kbManager.listKnowledgeBases();

// Get specific knowledge base
GetKnowledgeBaseResponse kb = kbManager.getKnowledgeBase("kb-id");
```

## Configuration

### Connector Configuration

```java
ConnectorConfig config = ConnectorConfig.builder()
    .maxResults(50)
    .retryAttempts(3)
    .retryDelayMs(1000)
    .enableValidation(true)
    .build();
```

### S3 Configuration Builder

```java
DataSourceConfiguration config = connector.createS3Configuration(
    S3ConfigBuilder.builder()
        .bucketName("my-bucket")
        .addInclusionPrefix("documents/")
        .addExclusionPrefix("temp/")
        .bucketOwnerAccountId("123456789012")
);
```

## Error Handling

All operations throw `ConnectorException` for consistent error handling:

```java
try {
    CreateDataSourceResponse response = connector.createDataSource(name, config);
} catch (ConnectorException e) {
    System.err.println("Failed to create data source: " + e.getMessage());
    e.printStackTrace();
}
```

## Dependencies

- AWS SDK for Java v2 (BedrockAgent)
- Java 11+
- Maven 3.6+

## Authentication

Authentication is handled by the AWS SDK. You can override authentication in your target repository by configuring:

- AWS credentials file
- Environment variables
- IAM roles
- AWS SSO

## Building

```bash
mvn clean compile
mvn exec:java -Dexec.mainClass="com.example.Main"
```

## License

This project is provided as an example implementation for educational purposes.