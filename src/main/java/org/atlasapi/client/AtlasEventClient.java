package org.atlasapi.client;


import org.atlasapi.media.entity.simple.EventQueryResult;

public interface AtlasEventClient {
    EventQueryResult events(EventQuery eventQuery);
}
