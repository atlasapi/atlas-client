package org.uriplay.client;

import org.uriplay.media.entity.simple.UriplayQueryResult;

interface StringQueryClient {
	
	UriplayQueryResult query(String queryUri);

}
