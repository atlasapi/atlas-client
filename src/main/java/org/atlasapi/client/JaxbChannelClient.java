package org.atlasapi.client;

import org.atlasapi.media.entity.simple.Channel;
import org.atlasapi.media.entity.simple.ChannelGroup;
import org.atlasapi.media.entity.simple.ChannelGroupQueryResult;
import org.atlasapi.media.entity.simple.ChannelQueryResult;

import com.google.common.base.Optional;
import com.google.common.collect.Iterables;
import com.metabroadcast.common.url.QueryStringParameters;

public class JaxbChannelClient implements AtlasChannelClient {
    private String channelsPattern = "/channels";
    private String channelPattern = "/channels/";
    private String channelGroupsPattern = "/channel_groups";
    private String channelGroupPattern = "/channel_groups/";
    
    private QueryStringBuilder queryStringBuilder = new QueryStringBuilder();
    private StringQueryClient queryClient;

    private final String baseUri;
    private String apiKey;

    public JaxbChannelClient(String baseUri, StringQueryClient queryClient) {
        this.baseUri = baseUri;
        this.queryClient = queryClient;
    }
    
    public JaxbChannelClient(String baseUri) {
        this(baseUri, new JaxbStringQueryClient());
    }
    
    public JaxbChannelClient(String baseUri, String apiKey) {
        this(baseUri, new JaxbStringQueryClient());
        this.withApiKey(apiKey);
    }
    
    public JaxbChannelClient withApiKey(String apiKey) {
        this.apiKey = apiKey;
        this.queryStringBuilder.setApiKey(apiKey);
        return this;
    }
    
    @Override
    public ChannelGroupQueryResult channelGroups(ChannelGroupQuery query) {
        QueryStringParameters params = query.toQueryStringParameters();
        if (apiKey != null) {
            params.add("apiKey", apiKey);
        }
        return queryClient.channelGroupQuery(baseUri + channelGroupsPattern + ".xml?" + params.toQueryString());      
    }

    @Override
    public Optional<ChannelGroup> channelGroup(String channelGroupId) {
        return Optional.fromNullable(Iterables.getOnlyElement(queryClient.channelGroupQuery(baseUri + channelGroupPattern + channelGroupId + ".xml").getChannelGroups(), null));   
    }

    @Override
    public ChannelQueryResult channels(ChannelQuery query) {
        QueryStringParameters params = query.toQueryStringParameters();
        if (apiKey != null) {
            params.add("apiKey", apiKey);
        }
        return queryClient.channelQuery(baseUri + channelsPattern + ".xml?" + params.toQueryString());
    }

    @Override
    public Optional<Channel> channel(String channelId) {
        return Optional.fromNullable(Iterables.getOnlyElement(queryClient.channelQuery(baseUri + channelPattern + channelId + ".xml").getChannels(), null));
    }

}
