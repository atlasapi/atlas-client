package org.atlasapi.client.response;

import org.atlasapi.media.entity.simple.response.AtlasResponse;

import com.google.gson.annotations.SerializedName;

public class WriteResponseWrapper {

    @SerializedName("atlasResponse")
    private final AtlasResponse atlasResponse;

    public WriteResponseWrapper(AtlasResponse atlasResponse) {
        this.atlasResponse = atlasResponse;
    }

    public AtlasResponse getAtlasResponse() {
        return atlasResponse;
    }
}
