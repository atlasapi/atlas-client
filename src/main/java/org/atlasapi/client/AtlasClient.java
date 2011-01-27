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

import org.atlasapi.client.query.AtlasQuery;
import org.atlasapi.media.entity.simple.Description;

/**
 * Client interface to the URIplay service.
 *  
 * @author John Ayres (john@metabroadcast.com)
 * @author Robert Chatley (robert@metabroadcast.com)
 */
public interface AtlasClient {
	
	<T extends Description> List<T> query(AtlasQuery<T> query);
	
	Map<String, Description> any(Iterable<String> ids);
	
}
