package org.atlasapi.client;

import org.atlasapi.media.entity.simple.ContentQueryResult;
import org.atlasapi.media.entity.simple.Topic;
import org.atlasapi.output.Annotation;

public interface AtlasTopicClient {

    //TODO: add topics method
    
    Topic topic(String topicId);
    
    //TODO: encapsulate parameters into TopicQuery
    ContentQueryResult contentFor(String topicId, Annotation...annotations);
    
}
