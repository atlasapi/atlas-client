package org.atlasapi.client.response;

import org.atlasapi.media.entity.simple.response.AtlasResponse;

public class ContentResponse {

    private final AtlasResponse response;
    private final String location;

    public ContentResponse(AtlasResponse response, String location) {
        this.response = response;
        this.location = location;
    }

    public AtlasResponse getResponse() {
        return response;
    }

    public String getLocation() {
        return location;
    }
}
