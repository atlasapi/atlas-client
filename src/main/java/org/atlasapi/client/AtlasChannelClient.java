package org.atlasapi.client;

import org.atlasapi.media.entity.simple.Channel;
import org.atlasapi.media.entity.simple.ChannelGroup;
import org.atlasapi.media.entity.simple.ChannelGroupQueryResult;
import org.atlasapi.media.entity.simple.ChannelQueryResult;

import com.google.common.base.Optional;

public interface AtlasChannelClient {
    ChannelGroupQueryResult channelGroups(ChannelGroupQuery query);
    
    Optional<ChannelGroup> channelGroup(String channelGroupId);
    
    ChannelQueryResult channels(ChannelQuery query);
    
    Optional<Channel> channel(String channelId);
}
