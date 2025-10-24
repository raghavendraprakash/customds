package com.example.connector;

/**
 * Custom exception for data source connector operations
 */
public class ConnectorException extends Exception {
    
    public ConnectorException(String message) {
        super(message);
    }
    
    public ConnectorException(String message, Throwable cause) {
        super(message, cause);
    }
}