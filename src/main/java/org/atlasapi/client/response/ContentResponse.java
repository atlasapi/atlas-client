package org.atlasapi.client.response;

import org.atlasapi.media.entity.simple.response.WriteResponse;

public class ContentResponse {

    private final WriteResponse response;
    private final String location;

    public ContentResponse(WriteResponse response, String location) {
        this.response = response;
        this.location = location;
    }

    public WriteResponse getResponse() {
        return response;
    }

    public String getLocation() {
        return location;
    }
}
