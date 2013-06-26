package org.atlasapi.client;

import org.atlasapi.media.entity.simple.ContentQueryResult;
import org.atlasapi.media.entity.simple.Topic;
import org.atlasapi.media.entity.simple.TopicQueryResult;

import com.google.common.base.Optional;

public interface AtlasTopicClient {

    TopicQueryResult topics(TopicQuery query);
    
    Optional<Topic> topic(String topicId);
    
    ContentQueryResult contentFor(String topicId, ContentQuery query);
 
    void postTopic(Topic topic);
}
