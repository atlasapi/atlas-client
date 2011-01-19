package org.atlasapi.client.testing;

import java.util.List;
import java.util.Map;

import org.atlasapi.client.AtlasClient;
import org.atlasapi.client.query.AtlasQuery;
import org.atlasapi.media.entity.simple.Description;

import com.google.common.collect.Maps;

public class StubAtlasClient implements AtlasClient {
    
    Map<String, Description> contentMap = Maps.newHashMap();

    @Override
    public List<Description> discover(AtlasQuery query) {
        throw new UnsupportedOperationException();
    }

    public void put(String uri, Description content) {
        contentMap.put(uri, content);
    }

	@Override
	public Map<String, Description> any(Iterable<String> ids, AtlasQuery filter) {
		return any(ids);
	}

	@Override
	public Map<String, Description> any(Iterable<String> ids) {
		Map<String, Description> results = Maps.newHashMap();
        
        for (String id : ids) {
            results.put(id, contentMap.get(id));
        }
        return results;
	}
}
