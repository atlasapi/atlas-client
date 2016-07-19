package org.atlasapi.client;

public class TopicResponse {

    private final String id;
    private final String location;

    public TopicResponse(String id, String location) {
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
