package com.example.connector;

import java.util.ArrayList;
import java.util.List;

/**
 * Builder class for S3 data source configuration
 */
public class S3ConfigBuilder {
    private String bucketName;
    private String bucketOwnerAccountId;
    private List<String> inclusionPrefixes;
    private List<String> exclusionPrefixes;
    
    private S3ConfigBuilder() {
        this.inclusionPrefixes = new ArrayList<>();
        this.exclusionPrefixes = new ArrayList<>();
    }
    
    public static S3ConfigBuilder builder() {
        return new S3ConfigBuilder();
    }
    
    public S3ConfigBuilder bucketName(String bucketName) {
        this.bucketName = bucketName;
        return this;
    }
    
    public S3ConfigBuilder bucketOwnerAccountId(String accountId) {
        this.bucketOwnerAccountId = accountId;
        return this;
    }
    
    public S3ConfigBuilder addInclusionPrefix(String prefix) {
        if (prefix != null && !prefix.isEmpty()) {
            this.inclusionPrefixes.add(prefix);
        }
        return this;
    }
    
    public S3ConfigBuilder inclusionPrefixes(List<String> prefixes) {
        if (prefixes != null) {
            this.inclusionPrefixes.addAll(prefixes);
        }
        return this;
    }
    
    public S3ConfigBuilder addExclusionPrefix(String prefix) {
        if (prefix != null && !prefix.isEmpty()) {
            this.exclusionPrefixes.add(prefix);
        }
        return this;
    }
    
    public S3ConfigBuilder exclusionPrefixes(List<String> prefixes) {
        if (prefixes != null) {
            this.exclusionPrefixes.addAll(prefixes);
        }
        return this;
    }
    
    // Getters for internal use
    public String getBucketArn() {
        return bucketName != null ? "arn:aws:s3:::" + bucketName : null;
    }
    
    public String getBucketName() {
        return bucketName;
    }
    
    public String getBucketOwnerAccountId() {
        return bucketOwnerAccountId;
    }
    
    public List<String> getInclusionPrefixes() {
        return inclusionPrefixes;
    }
    
    public List<String> getExclusionPrefixes() {
        return exclusionPrefixes;
    }
}