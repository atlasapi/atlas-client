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

package org.uriplay.client;

import java.io.Reader;
import java.io.StringReader;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.uriplay.content.criteria.ContentQuery;
import org.uriplay.media.entity.simple.Description;
import org.uriplay.media.entity.simple.Item;
import org.uriplay.media.entity.simple.Playlist;
import org.uriplay.media.entity.simple.UriplayQueryResult;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.metabroadcast.common.cache.FixedExpiryCache;
import com.metabroadcast.common.http.SimpleHttpClient;
import com.metabroadcast.common.http.SimpleHttpClientBuilder;
import com.metabroadcast.common.url.UrlEncoding;

/**
 * @author Robert Chatley (robert@metabroadcast.com)
 */
@SuppressWarnings("deprecation")
public class JaxbUriplayClient implements SimpleUriplayClient {
		
	private final SimpleHttpClient httpClient = client();
	
	private final String baseUri;
	
	private JAXBContext context;

	private QueryStringBuilder queryStringBuilder = new QueryStringBuilder();
	
	
    private FixedExpiryCache<String, List<Item>> itemQueryCache = new FixedExpiryCache<String, List<Item>>(10) {

		@Override
		protected List<Item> cacheMissFor(String query) {
			try {
				return extractContentOfType(retrieveData(baseUri + "/items.xml?" +  query), Item.class);
			} catch (Exception e) {
				throw new RuntimeException("Problem requesting query: " + query, e);
			} 
		}
	};
	
	private FixedExpiryCache<String, Description> anyQueryCache = new FixedExpiryCache<String, Description>(10) {

			@Override
			protected Description cacheMissFor(String uri) {
				try {
					return Iterables.getOnlyElement(retrieveData(baseUri + "/any.xml?uri=" +  UrlEncoding.encode(uri)), null);
				} catch (Exception e) {
					throw new RuntimeException("Problem requesting query: " + uri, e);
				} 
			}
		};
	
	private FixedExpiryCache<String, List<Playlist>> playlistQueryCache = new FixedExpiryCache<String, List<Playlist>>(10) {

		@Override
		protected List<Playlist> cacheMissFor(String query) {
			try {
				return extractContentOfType(retrieveData(baseUri +  "/playlists.xml?" +  query), Playlist.class);
			} catch (Exception e) {
				throw new RuntimeException("Problem requesting query: " + query, e);
			} 
		}
	};
	
	private FixedExpiryCache<String, List<Playlist>> brandQueryCache = new FixedExpiryCache<String, List<Playlist>>(10) {

		@Override
		protected List<Playlist> cacheMissFor(String query) {
			try {
				return extractContentOfType(retrieveData(baseUri +  "/brands.xml?" +  query), Playlist.class);
			} catch (Exception e) {
				throw new RuntimeException("Problem requesting query: " + query, e);
			} 
		}
	};

	public JaxbUriplayClient(String baseUri) throws JAXBException {
		this.baseUri = baseUri;
		context = JAXBContext.newInstance(UriplayQueryResult.class);
	}
	

	private List<Description> retrieveData(String queryUri) throws Exception {
		Reader document = new StringReader(httpClient.get(queryUri));

		Unmarshaller unmarshaller = context.createUnmarshaller();
		
		UriplayQueryResult wrapper = (UriplayQueryResult) unmarshaller.unmarshal(document);
		
		List<Description> beanGraph = Lists.newArrayList();
		
		if (wrapper.getItems() != null) {
			beanGraph.addAll(wrapper.getItems());
		}
		
		if (wrapper.getPlaylists() != null) {
			beanGraph.addAll(wrapper.getPlaylists());
		}
		return beanGraph;
	}
	
	@SuppressWarnings("unchecked")
	private static <T extends Description> List<T> extractContentOfType(List<Description> beanGraph, Class<T> type) {

		if (beanGraph == null) {
			return null;
		}
		List<T> items = Lists.newArrayList();

		for (Object bean : beanGraph) {
			if (bean.getClass().equals(type)) {
				items.add((T) bean);
			}
		}
		return Collections.unmodifiableList(items);
	}

	
    @Override
	public List<Item> itemQuery(ContentQuery query) {
		return itemQueryCache.get(queryStringBuilder.build(query));
	}

	@Override
	public List<Playlist> brandQuery(ContentQuery query) {
		return brandQueryCache.get(queryStringBuilder.build(query));
	}
	
	@Override
	public List<Playlist> playlistQuery(ContentQuery query) {
		return playlistQueryCache.get(queryStringBuilder.build(query));
	}
	
	@Override
	public Description anyQuery(String uri) {
		return anyQueryCache.get(uri);
	}

	private static SimpleHttpClient client() {
        return new SimpleHttpClientBuilder().
            withUserAgent("Mozilla/5.0 (compatible; uriplay/2.0; +http://uriplay.org)")
        .build();
    }


}
