package org.atlasapi.client.exception;


public class BadResponseException extends RuntimeException {

    private static final long serialVersionUID = -4105647791651230192L;

    public BadResponseException() {
        super();
    }

    public BadResponseException(String message) {
        super(message);
    }
}
