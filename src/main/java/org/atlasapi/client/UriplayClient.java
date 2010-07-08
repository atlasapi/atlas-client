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

import java.util.List;
import java.util.Map;

import org.atlasapi.content.criteria.ContentQuery;
import org.atlasapi.media.entity.simple.Description;
import org.atlasapi.media.entity.simple.Item;
import org.atlasapi.media.entity.simple.Playlist;

/**
 * Client interface to the URIplay service.
 *  
 * @author John Ayres (john@metabroadcast.com)
 * @author Robert Chatley (robert@metabroadcast.com)
 */
public interface UriplayClient {

	List<Item> items(ContentQuery query);
	
	List<Playlist> brands(ContentQuery query);
	
	List<Playlist> playlists(ContentQuery query);

	Map<String, Description> any(Iterable<String> ids);
	
}
