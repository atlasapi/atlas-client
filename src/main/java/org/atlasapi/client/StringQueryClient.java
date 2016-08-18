package org.atlasapi.client;

import org.atlasapi.client.response.ContentResponse;
import org.atlasapi.client.response.TopicUpdateResponse;
import org.atlasapi.media.entity.simple.ChannelGroupQueryResult;
import org.atlasapi.media.entity.simple.ChannelQueryResult;
import org.atlasapi.media.entity.simple.ContentGroupQueryResult;
import org.atlasapi.media.entity.simple.ContentQueryResult;
import org.atlasapi.media.entity.simple.EventQueryResult;
import org.atlasapi.media.entity.simple.Item;
import org.atlasapi.media.entity.simple.PeopleQueryResult;
import org.atlasapi.media.entity.simple.Playlist;
import org.atlasapi.media.entity.simple.ScheduleQueryResult;
import org.atlasapi.media.entity.simple.Topic;
import org.atlasapi.media.entity.simple.TopicQueryResult;

interface StringQueryClient {
	
	ContentQueryResult contentQuery(String queryUri);
    
    ContentGroupQueryResult contentGroupQuery(String queryUri);

	ScheduleQueryResult scheduleQuery(String queryUri);

	PeopleQueryResult peopleQuery(String queryUri);
	
	TopicQueryResult topicQuery(String queryUri);

	String postItem(String query, Item item);

	ContentResponse postItemWithResponse(String query, Item item);

	String putItem(String query, Item item);

	ContentResponse putItemWithResponse(String query, Item item);

	String postTopic(String queryUri, Topic topic);

	TopicUpdateResponse postTopicWithResponse(String queryUri, Topic topic);

	ChannelQueryResult channelQuery(String queryUri);
	
	ChannelGroupQueryResult channelGroupQuery(String queryUri);

	EventQueryResult eventQuery(String eventQuery);

    ContentResponse postPlayListWithResponse(String query, Playlist playlist);

    ContentResponse putPlayListWithResponse(String query, Playlist playlist);
	
}
