package org.atlasapi.client;

import org.atlasapi.media.entity.simple.ContentGroupQueryResult;
import org.atlasapi.media.entity.simple.ContentQueryResult;

import com.google.common.base.Optional;


public interface AtlasContentGroupClient {
    ContentGroupQueryResult contentGroup(String id);    
    
    ContentGroupQueryResult contentGroups();    
    
    ContentQueryResult contentFor(String contentGroupid, Optional<ContentQuery> contentQuery);
}
