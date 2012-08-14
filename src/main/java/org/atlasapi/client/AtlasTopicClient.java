package org.atlasapi.client;

import org.atlasapi.media.entity.simple.ContentQueryResult;
import org.atlasapi.media.entity.simple.Topic;
import org.atlasapi.media.entity.simple.TopicQueryResult;
import org.atlasapi.output.Annotation;

import com.google.common.base.Optional;
import com.metabroadcast.common.query.Selection;

public interface AtlasTopicClient {

    TopicQueryResult topics(TopicQuery query);
    
    Optional<Topic> topic(String topicId);
    
    ContentQueryResult contentFor(String topicId, ContentQuery query);
    
}
