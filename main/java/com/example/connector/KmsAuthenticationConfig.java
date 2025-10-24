package com.example.connector;

import java.util.HashMap;
import java.util.Map;

/**
 * Authentication configuration for KMS Lighthouse connector
 */
public class KmsAuthenticationConfig {
    private final AuthenticationType type;
    private final String username;
    private final String password;
    private final String bearerToken;
    private final String oauthClientId;
    private final String oauthClientSecret;
    private final String oauthTokenUrl;
    private final Map<String, String> customHeaders;
    private final String secretArn;
    
    private KmsAuthenticationConfig(Builder builder) {
        this.type = builder.type;
        this.username = builder.username;
        this.password = builder.password;
        this.bearerToken = builder.bearerToken;
        this.oauthClientId = builder.oauthClientId;
        this.oauthClientSecret = builder.oauthClientSecret;
        this.oauthTokenUrl = builder.oauthTokenUrl;
        this.customHeaders = new HashMap<>(builder.customHeaders);
        this.secretArn = builder.secretArn;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public enum AuthenticationType {
        BASIC,
        BEARER_TOKEN,
        OAUTH2,
        CUSTOM_HEADERS,
        AWS_SECRETS_MANAGER
    }
    
    // Getters
    public AuthenticationType getType() { return type; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getBearerToken() { return bearerToken; }
    public String getOauthClientId() { return oauthClientId; }
    public String getOauthClientSecret() { return oauthClientSecret; }
    public String getOauthTokenUrl() { return oauthTokenUrl; }
    public Map<String, String> getCustomHeaders() { return customHeaders; }
    public String getSecretArn() { return secretArn; }
    
    public static class Builder {
        private AuthenticationType type = AuthenticationType.CUSTOM_HEADERS;
        private String username;
        private String password;
        private String bearerToken;
        private String oauthClientId;
        private String oauthClientSecret;
        private String oauthTokenUrl;
        private Map<String, String> customHeaders = new HashMap<>();
        private String secretArn;
        
        public Builder type(AuthenticationType type) {
            this.type = type;
            return this;
        }
        
        public Builder basicAuth(String username, String password) {
            this.type = AuthenticationType.BASIC;
            this.username = username;
            this.password = password;
            return this;
        }
        
        public Builder bearerToken(String token) {
            this.type = AuthenticationType.BEARER_TOKEN;
            this.bearerToken = token;
            return this;
        }
        
        public Builder oauth2(String clientId, String clientSecret, String tokenUrl) {
            this.type = AuthenticationType.OAUTH2;
            this.oauthClientId = clientId;
            this.oauthClientSecret = clientSecret;
            this.oauthTokenUrl = tokenUrl;
            return this;
        }
        
        public Builder customHeader(String name, String value) {
            this.customHeaders.put(name, value);
            return this;
        }
        
        public Builder customHeaders(Map<String, String> headers) {
            this.customHeaders.putAll(headers);
            return this;
        }
        
        public Builder secretArn(String secretArn) {
            this.type = AuthenticationType.AWS_SECRETS_MANAGER;
            this.secretArn = secretArn;
            return this;
        }
        
        public KmsAuthenticationConfig build() {
            return new KmsAuthenticationConfig(this);
        }
    }
}