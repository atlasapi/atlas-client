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
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import org.atlasapi.client.query.AtlasQuery;
import org.atlasapi.media.entity.simple.ContentQueryResult;
import org.atlasapi.media.entity.simple.Description;
import org.atlasapi.media.entity.simple.DiscoverQueryResult;
import org.atlasapi.media.entity.simple.PeopleQueryResult;
import org.atlasapi.media.entity.simple.ScheduleQueryResult;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.metabroadcast.common.base.Maybe;
import com.metabroadcast.common.url.QueryStringParameters;
import com.metabroadcast.common.url.UrlEncoding;
import org.atlasapi.media.entity.simple.ContentGroupQueryResult;

/**
 * @author Robert Chatley (robert@metabroadcast.com)
 */
@Deprecated
public class CachingJaxbAtlasClient implements AtlasClient {
		
	private QueryStringBuilder queryStringBuilder = new QueryStringBuilder();
	private StringQueryClient queryClient;

	private final MapMaker cacheTemplate = new MapMaker().softValues().expireAfterWrite(10, TimeUnit.MINUTES);
	
    private ConcurrentMap<String, ContentQueryResult> queryCache = cacheTemplate.makeComputingMap(new Function<String, ContentQueryResult>() {

		@Override
		public ContentQueryResult apply(String query) {
			return queryClient.contentQuery(query);
		}
    });
 
	private ConcurrentMap<String, Maybe<Description>> contentIdentifierQueryCache = cacheTemplate.makeMap();

	private final String baseUri;
	private String apiKey;

	public CachingJaxbAtlasClient(String baseUri, StringQueryClient queryClient) {
		this.baseUri = baseUri;
		this.queryClient = queryClient;
	}
	
	public CachingJaxbAtlasClient() {
		this("http://stage.atlas.metabroadcast.com/3.0", new JaxbStringQueryClient());
	}
	
	public CachingJaxbAtlasClient(String baseUri) {
	    this(baseUri, new JaxbStringQueryClient());
	}
	
	public CachingJaxbAtlasClient(String baseUri, String apiKey) {
	    this(baseUri, new JaxbStringQueryClient());
	    this.withApiKey(apiKey);
	}
	
	public CachingJaxbAtlasClient withApiKey(String apiKey) {
		this.apiKey = apiKey;
		this.queryStringBuilder.setApiKey(apiKey);
		return this;
	}
	
	@Override
	public DiscoverQueryResult discover(AtlasQuery query) {
	    List<Description> contents = queryCache.get(baseUri + "/discover.xml?" + queryStringBuilder.build(query.build())).getContents();
		return new DiscoverQueryResult(contents);
	}

	private Map<String, Description> fetchIdentifierQuery(Iterable<String> uris) {
		ContentQueryResult result = queryClient.contentQuery(baseUri + "/content.xml?uri=" +  Joiner.on(",").join(UrlEncoding.encode(uris)) + apiKeyQueryPart());
		return cacheResults(Sets.newHashSet(result.getContents()), Sets.newHashSet(uris));
	}
	
	private String apiKeyQueryPart() {
		if (this.apiKey != null) {
			return "&apiKey="+this.apiKey;
		}
		return "";
	}

	private Map<String, Description> cacheResults(Set<Description> all, Set<String> uris) {
		Map<String, Description> results = Maps.newHashMap();
		Set<String> allIdentifiersReturned = Sets.newHashSet();
		for (Description description : all) {
			results.put(description.getUri(), description);
			putInCache(contentIdentifierQueryCache, description);
			allIdentifiersReturned.addAll(description.identifiers());
			allIdentifiersReturned.addAll(description.getSameAs());
		}
		// Make sure negative results are cached
		Set<String> notFound = Sets.difference(uris, allIdentifiersReturned);
		for (String notfound : notFound) {
			contentIdentifierQueryCache.put(notfound, Maybe.<Description>nothing());
		}
		return results;
	}

	private void putInCache(ConcurrentMap<String, Maybe<Description>> cache, Description content) {
		cache.put(content.getUri(), Maybe.just(content));
		if (content.getCurie() != null) {
			cache.put(content.getCurie(),  Maybe.just(content));
		}
	}
	
	@Override
	public ScheduleQueryResult scheduleFor(ScheduleQuery query) {
		// avoid using the naive cache, schedules require day-binned caches provided by higher-layers.
		QueryStringParameters params = query.toParams();
		if (apiKey != null) {
			params.add("apiKey", apiKey);
		}
		return queryClient.scheduleQuery(baseUri + "/schedule.xml?" + params.toQueryString());
	}

	@Override
	public ContentQueryResult content(Iterable<String> ids) {
		Map<String, Description> results = Maps.newHashMap();
		List<String> toFetch = Lists.newArrayList();
		
		for (String id : ids) {
			if (contentIdentifierQueryCache.containsKey(id)) {
				Maybe<Description> description = contentIdentifierQueryCache.get(id);
				
				if (description.hasValue()) {
					results.put(description.requireValue().getUri(), description.requireValue());
				}
			}
			else {
				toFetch.add(id);
			}
		}
		if (!toFetch.isEmpty()) {
			results.putAll(fetchIdentifierQuery(toFetch));
		}
		
		ContentQueryResult result = new ContentQueryResult();
		result.setContents(results.values());
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
    public ContentQueryResult content(ContentQuery query) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ContentQueryResult search(SearchQuery query) {
        QueryStringParameters params = query.toParams();
        if (apiKey != null) {
            params.add("apiKey", apiKey);
        }
        return queryClient.contentQuery(baseUri + "/search.xml?" + params.toQueryString());
    }

    @Override
    public PeopleQueryResult people(Iterable<String> uris) {
        return queryClient.peopleQuery(baseUri + "/people.xml?uri=" + Joiner.on(",").join(UrlEncoding.encode(uris)) + apiKeyQueryPart());
    }
}
