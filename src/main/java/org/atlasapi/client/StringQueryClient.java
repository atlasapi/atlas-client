package org.atlasapi.client;

import org.atlasapi.media.entity.simple.ContentQueryResult;

interface StringQueryClient {
	
	ContentQueryResult query(String queryUri);

}
