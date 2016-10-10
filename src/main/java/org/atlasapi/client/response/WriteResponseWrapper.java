package org.atlasapi.client.response;

import org.atlasapi.media.entity.simple.response.WriteResponse;

import com.google.gson.annotations.SerializedName;

public class WriteResponseWrapper {

    @SerializedName("writeResponse")
    private final WriteResponse writeResponse;

    public WriteResponseWrapper(WriteResponse writeResponse) {
        this.writeResponse = writeResponse;
    }

    public WriteResponse getAtlasResponse() {
        return writeResponse;
    }
}
