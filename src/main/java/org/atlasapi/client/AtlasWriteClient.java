package org.atlasapi.client;

import org.atlasapi.client.query.ChannelGroupWriteOptions;
import org.atlasapi.client.query.ChannelWriteOptions;
import org.atlasapi.client.query.ContentWriteOptions;
import org.atlasapi.client.response.ChannelGroupResponse;
import org.atlasapi.client.response.ChannelResponse;
import org.atlasapi.client.response.ContentResponse;
import org.atlasapi.client.response.TopicUpdateResponse;
import org.atlasapi.media.entity.simple.Channel;
import org.atlasapi.media.entity.simple.ChannelGroup;
import org.atlasapi.media.entity.simple.Item;
import org.atlasapi.media.entity.simple.Person;
import org.atlasapi.media.entity.simple.Playlist;
import org.atlasapi.media.entity.simple.Topic;

public interface AtlasWriteClient {
    
    void writePerson(Person person);
    
    void updatePerson(Person person);

    ContentResponse writeItem(Item item, ContentWriteOptions options);

    ContentResponse writePlaylist(Playlist playlist, ContentWriteOptions options);

    ChannelGroupResponse writeChannelGroup(ChannelGroup channelGroup, ChannelGroupWriteOptions overwriteExisting);

    void writeChannel(Channel channel, ChannelWriteOptions channelWriteOptions);

    /**
     * @deprecated Use {@link AtlasWriteClient#writeItem(Item, ContentWriteOptions)}
     */
    @Deprecated
    String writeItem(Item item);

    /**
     * @deprecated Use {@link AtlasWriteClient#writeItem(Item, ContentWriteOptions)}
     */
    @Deprecated
    String writeItemAsync(Item item);

    /**
     * @deprecated Use {@link AtlasWriteClient#writeItem(Item, ContentWriteOptions)}
     */
    @Deprecated
    ContentResponse writeItemWithResponse(Item item);

    /**
     * @deprecated Use {@link AtlasWriteClient#writeItem(Item, ContentWriteOptions)}
     */
    @Deprecated
    ContentResponse writeItemWithResponseAsync(Item item);

    /**
     * @deprecated Use {@link AtlasWriteClient#writeItem(Item, ContentWriteOptions)}
     */
    @Deprecated
    void writeItemOverwriteExisting(Item item);

    /**
     * @deprecated Use {@link AtlasWriteClient#writeItem(Item, ContentWriteOptions)}
     */
    @Deprecated
    void writeItemOverwriteExistingAsync(Item item);

    /**
     * @deprecated Use {@link AtlasWriteClient#writeItem(Item, ContentWriteOptions)}
     */
    @Deprecated
    ContentResponse writeItemOverwriteExistingWithResponse(Item item);

    /**
     * @deprecated Use {@link AtlasWriteClient#writeItem(Item, ContentWriteOptions)}
     */
    @Deprecated
    ContentResponse writeItemOverwriteExistingAsyncWithResponse(Item item);

    /**
     * @deprecated Use {@link AtlasWriteClient#writePlaylist(Playlist, ContentWriteOptions)}
     */
    @Deprecated
    ContentResponse writePlayListWithResponse(Playlist playlist);

    /**
     * @deprecated Use {@link AtlasWriteClient#writePlaylist(Playlist, ContentWriteOptions)}
     */
    @Deprecated
    ContentResponse writePlayListOverwriteExistingWithResponse(Playlist playlist);

    TopicUpdateResponse writeTopicWithResponse(Topic topic);

    void unpublishContentById(String id);

    void unpublishContentByUri(String uri);
}
