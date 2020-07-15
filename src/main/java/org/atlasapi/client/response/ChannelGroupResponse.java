package org.atlasapi.client.response;

public class ChannelGroupResponse {

    private final String id;
    private final String location;

    public ChannelGroupResponse(String id, String location) {
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
