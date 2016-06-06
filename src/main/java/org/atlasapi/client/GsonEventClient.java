package org.atlasapi.client;


import com.google.common.base.Optional;
import com.google.common.net.HostSpecifier;
import com.metabroadcast.common.url.QueryStringParameters;
import com.metabroadcast.common.url.Urls;
import org.atlasapi.media.entity.simple.EventQueryResult;

public class GsonEventClient implements AtlasEventClient {
    private final String API_KEY = "apiKey";
    private final Optional<String> apiKey;
    private final String eventsQueryPattern;
    private final GsonQueryClient queryClient;

    public GsonEventClient(HostSpecifier hostSpecifier, Optional<String> apiKey) {
        this.apiKey = apiKey;
        this.eventsQueryPattern = String.format("http://%s/3.0/events.json", hostSpecifier);
        this.queryClient = new GsonQueryClient();
    }

    @Override
    public EventQueryResult events(EventQuery eventQuery) {
        QueryStringParameters parameters = eventQuery.toQueryStringParameters();
        if(apiKey.isPresent()) {
            parameters.add(API_KEY, apiKey.get());
        }
        String eventQueryUri = Urls.appendParameters(eventsQueryPattern, parameters);
        return queryClient.eventQuery(eventQueryUri);
    }
}
