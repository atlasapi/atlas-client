package org.atlasapi.client;

import org.atlasapi.media.entity.simple.ContentQueryResult;
import org.atlasapi.media.entity.simple.Topic;
import org.atlasapi.media.entity.simple.TopicQueryResult;
import org.atlasapi.output.Annotation;

import com.google.common.base.Optional;
import com.metabroadcast.common.query.Selection;

public interface AtlasTopicClient {

    //TODO: add topics method
    
    Topic topic(String topicId);
    
    ContentQueryResult contentFor(TopicQuery query);

    TopicQueryResult topicsFor(TopicQuery query);
    
}
