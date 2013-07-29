package org.atlasapi.client.testing;

import java.util.Map;

import org.atlasapi.client.AtlasClient;
import org.atlasapi.client.ContentQuery;
import org.atlasapi.client.PeopleQuery;
import org.atlasapi.client.ScheduleQuery;
import org.atlasapi.client.SearchQuery;
import org.atlasapi.client.query.AtlasQuery;
import org.atlasapi.media.entity.simple.ContentQueryResult;
import org.atlasapi.media.entity.simple.Description;
import org.atlasapi.media.entity.simple.DiscoverQueryResult;
import org.atlasapi.media.entity.simple.PeopleQueryResult;
import org.atlasapi.media.entity.simple.Person;
import org.atlasapi.media.entity.simple.ScheduleQueryResult;

import com.google.common.collect.Maps;

import org.atlasapi.media.entity.simple.ContentGroupQueryResult;

public class StubAtlasClient implements AtlasClient {
    
    Map<String, Description> contentMap = Maps.newHashMap();

    @Override
    public DiscoverQueryResult discover(AtlasQuery query) {
        throw new UnsupportedOperationException();
    }

    public void put(String uri, Description content) {
        contentMap.put(uri, content);
    }

	@Override
	public ContentQueryResult content(Iterable<String> ids) {
		ContentQueryResult result = new ContentQueryResult();
        for (String id : ids) {
            result.add(contentMap.get(id));
        }
        
        return result;
	}

    @Override
    public ContentGroupQueryResult contentGroup(String id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ContentGroupQueryResult contentGroups() {
        throw new UnsupportedOperationException();
    }

	@Override
	public ScheduleQueryResult scheduleFor(ScheduleQuery query) {
        throw new UnsupportedOperationException();
	}

    @Override
    public ContentQueryResult search(SearchQuery query) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PeopleQueryResult people(Iterable<String> uris) {
        PeopleQueryResult result = new PeopleQueryResult();
        for (String uri : uris) {
            result.add((Person) contentMap.get(uri));
        }
        return result;
    }

    @Override
    public PeopleQueryResult people(PeopleQuery query) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ContentQueryResult content(ContentQuery query) {
        throw new UnsupportedOperationException();
    }
}
