package org.atlasapi.client.testing;

import java.util.List;
import java.util.Map;

import org.atlasapi.client.AtlasClient;
import org.atlasapi.client.query.AtlasQuery;
import org.atlasapi.media.entity.simple.Description;

import com.google.common.collect.Maps;

public class TestAtlasClient implements AtlasClient {
    
    Map<String, Description> contentMap = Maps.newHashMap();

    @Override
    public <T> List<T> query(AtlasQuery<T> query) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<String, Description> any(Iterable<String> ids) {
        Map<String, Description> results = Maps.newHashMap();
        
        for (String id : ids) {
            results.put(id, contentMap.get(id));
        }
       
        return results;
    }

    public void put(String uri, Description content) {
        contentMap.put(uri, content);
    }
}
