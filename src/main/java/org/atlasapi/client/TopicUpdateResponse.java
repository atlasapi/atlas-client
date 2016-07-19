package org.atlasapi.client;

public class TopicUpdateResponse {

    private final String id;
    private final String location;

    public TopicUpdateResponse(String id, String location) {
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
