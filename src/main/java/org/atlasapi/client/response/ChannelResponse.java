package org.atlasapi.client.response;

public class ChannelResponse {

    private final String id;
    private final String location;

    public ChannelResponse(String id, String location) {
        this.id = id;
        this.location = location;
    }

    public String getId() {
        return id;
    }

    public String getLocation() {
        return location;
    }

}
