package org.atlasapi.client.exception;

import com.metabroadcast.common.http.HttpResponse;

public class BadResponseException extends RuntimeException {

    private static final long serialVersionUID = -4105647791651230192L;
    private HttpResponse response;
    private String queryInfo;

    public BadResponseException() {
        super();
    }

    public BadResponseException(String message) {
        super(message);
    }

    public String getQueryInfo() {
        return queryInfo;
    }

    public void setQueryInfo(String queryInfo) {
        this.queryInfo = queryInfo;
    }

    public void setResponse(HttpResponse response) {
        this.response = response;
    }

    public HttpResponse getResponse() {
        return response;
    }

    public String toDetailedString() {
        return String.format(
                "%s%nCode %s, %s%nResponseBody%s%nQueryInfo:%s",
                super.toString(),
                getResponse().statusCode(),
                getResponse().statusLine(),
                getResponse().body(),
                getQueryInfo()
        );
    }
}
