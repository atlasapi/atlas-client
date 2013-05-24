package org.atlasapi.client;

import org.atlasapi.media.entity.simple.Channel;
import org.atlasapi.media.entity.simple.ChannelGroup;
import org.atlasapi.media.entity.simple.ChannelGroupQueryResult;
import org.atlasapi.media.entity.simple.ChannelQueryResult;

import com.google.common.base.Optional;
import com.google.common.collect.Iterables;
import com.google.common.net.HostSpecifier;
import com.metabroadcast.common.url.QueryStringParameters;
import com.metabroadcast.common.url.Urls;

public class GsonChannelClient implements AtlasChannelClient {
    private String channelsPattern;
    private String channelPattern;
    private String channelGroupsPattern;
    private String channelGroupPattern;
    private final GsonQueryClient stringQueryClient;
    private final Optional<String> apiKey;
    
    public GsonChannelClient(HostSpecifier atlasHost, Optional<String> apiKey) {
        this.apiKey = apiKey;
        this.channelPattern = String.format("http://%s/3.0/channels/%%s.json", atlasHost);
        this.channelsPattern = String.format("http://%s/3.0/channels.json", atlasHost);
        this.channelGroupPattern = String.format("http://%s/3.0/channel_groups/%%s.json", atlasHost);
        this.channelGroupsPattern = String.format("http://%s/3.0/channel_groups.json", atlasHost);
        this.stringQueryClient = new GsonQueryClient();
    }
    @Override
    public ChannelGroupQueryResult channelGroups(ChannelGroupQuery query) {
        QueryStringParameters queryParams = query.toQueryStringParameters();
        if (apiKey.isPresent()) {
            queryParams.add("apiKey", apiKey.get());
        }
        
        String queryString = Urls.appendParameters(channelGroupsPattern, query.toQueryStringParameters());
        return stringQueryClient.channelGroupQuery(queryString);
    }

    @Override
    public Optional<ChannelGroup> channelGroup(String channelGroupId) {
        String queryString = apiKey.isPresent() ? Urls.appendParameters(String.format(channelGroupPattern, channelGroupId), "apiKey", apiKey.get()) : String.format(channelGroupPattern, channelGroupId);
        return Optional.fromNullable(Iterables.getOnlyElement(stringQueryClient.channelGroupQuery(queryString).getChannels(), null));
    }

    @Override
    public ChannelQueryResult channels(ChannelQuery query) {
        QueryStringParameters queryParams = query.toQueryStringParameters();
        if (apiKey.isPresent()) {
            queryParams.add("apiKey", apiKey.get());
        }
        
        String queryString = Urls.appendParameters(channelsPattern, query.toQueryStringParameters());
        return stringQueryClient.channelQuery(queryString);
    }

    @Override
    public Optional<Channel> channel(String channelId) {
        String queryString = apiKey.isPresent() ? Urls.appendParameters(String.format(channelPattern, channelId), "apiKey", apiKey.get()) : String.format(channelPattern, channelId);
        return Optional.fromNullable(Iterables.getOnlyElement(stringQueryClient.channelQuery(queryString).getChannels(), null));
    }

}
