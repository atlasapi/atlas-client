package org.atlasapi.client;

import org.atlasapi.media.entity.simple.ContentGroupQueryResult;
import org.atlasapi.media.entity.simple.ContentQueryResult;

import com.google.common.base.Optional;


public interface AtlasContentGroupClient {

    ContentGroupQueryResult contentGroup(String id);

    ContentGroupQueryResult contentGroup(String id, Optional<String> apiKey);

    ContentGroupQueryResult contentGroups();

    ContentGroupQueryResult contentGroups(Optional<String> apiKey);

    ContentQueryResult contentFor(String contentGroupid, Optional<ContentQuery> contentQuery);

    ContentQueryResult contentFor(String contentGroupid, Optional<ContentQuery> contentQuery, Optional<String> apiKey);
}
