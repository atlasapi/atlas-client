/* Copyright 2009 Meta Broadcast Ltd

Licensed under the Apache License, Version 2.0 (the "License"); you
may not use this file except in compliance with the License. You may
obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
implied. See the License for the specific language governing
permissions and limitations under the License. */

package org.atlasapi.client;

import org.atlasapi.client.query.AtlasQuery;
import org.atlasapi.media.entity.simple.ContentGroupQueryResult;
import org.atlasapi.media.entity.simple.ContentQueryResult;
import org.atlasapi.media.entity.simple.DiscoverQueryResult;
import org.atlasapi.media.entity.simple.PeopleQueryResult;
import org.atlasapi.media.entity.simple.ScheduleQueryResult;

/**
 * Client interface to the URIplay service.
 *  
 * @author John Ayres (john@metabroadcast.com)
 * @author Robert Chatley (robert@metabroadcast.com)
 */
public interface AtlasClient {
	
    @Deprecated
	DiscoverQueryResult discover(AtlasQuery query);

	ScheduleQueryResult scheduleFor(ScheduleQuery query);
	
	ContentQueryResult content(Iterable<String> ids);
	
	ContentQueryResult search(SearchQuery query);

	PeopleQueryResult people(Iterable<String> uris);
	
	PeopleQueryResult people(PeopleQuery query);
	
	ContentQueryResult content(ContentQuery query);
    
	@Deprecated
    ContentGroupQueryResult contentGroup(String id);	
    
	@Deprecated
    ContentGroupQueryResult contentGroups();	
}
