package org.atlasapi.client.response;

import org.atlasapi.media.entity.simple.response.WriteResponseWithOldIds;

import com.google.gson.annotations.SerializedName;

public class WriteResponseWithOldIdsWrapper {

    @SerializedName("writeResponseWithOldIds")
    private final WriteResponseWithOldIds writeResponseWithOldIds;

    public WriteResponseWithOldIdsWrapper(WriteResponseWithOldIds writeResponseWithOldIds) {
        this.writeResponseWithOldIds = writeResponseWithOldIds;
    }

    public WriteResponseWithOldIds getAtlasResponse() {
        return writeResponseWithOldIds;
    }

}
