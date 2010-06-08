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
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.uriplay.content.criteria.ContentQuery;
import org.uriplay.media.entity.simple.Description;
import org.uriplay.media.entity.simple.Item;
import org.uriplay.media.entity.simple.Playlist;
import org.uriplay.media.entity.simple.UriplayQueryResult;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Sets;
import com.metabroadcast.common.base.Maybe;
import com.metabroadcast.common.http.HttpStatusCodeException;
import com.metabroadcast.common.http.SimpleHttpClient;
import com.metabroadcast.common.http.SimpleHttpClientBuilder;
import com.metabroadcast.common.url.UrlEncoding;

/**
 * @author Robert Chatley (robert@metabroadcast.com)
 */
public class JaxbUriplayClient implements SimpleUriplayClient {
		
	private static final String USER_AGENT = "Mozilla/5.0 (compatible; uriplay/2.0; +http://uriplay.org)";

	private final SimpleHttpClient httpClient = new SimpleHttpClientBuilder().withUserAgent(USER_AGENT).build();
	
	private final String baseUri;
	
	private JAXBContext context;
	

	private QueryStringBuilder queryStringBuilder = new QueryStringBuilder();

	private final MapMaker cacheTemplate = new MapMaker().softValues().expiration(10, TimeUnit.MINUTES);
	
	
    private ConcurrentMap<String, List<Item>> itemQueryCache = cacheTemplate.makeComputingMap(new Function<String, List<Item>>() {

		@Override
		public List<Item> apply(String query) {
			try {
				return retrieveData(baseUri + "/items.xml?" +  query).getItems();
			} catch (Exception e) {
				throw new RuntimeException("Problem requesting query: " + query, e);
			} 
		}
    });
    
    private ConcurrentMap<String, List<Playlist>> brandQueryCache = cacheTemplate.makeComputingMap(new Function<String, List<Playlist>>() {

		@Override
		public List<Playlist> apply(String query) {
			try {
				return retrieveData(baseUri +  "/brands.xml?" +  query).getPlaylists();
			} catch (Exception e) {
				throw new RuntimeException("Problem requesting query: " + query, e);
			}
		}
    });
    
    private ConcurrentMap<String, List<Playlist>> playlistQueryCache = cacheTemplate.makeComputingMap(new Function<String, List<Playlist>>() {

		@Override
		public List<Playlist> apply(String query) {
			try {
				return retrieveData(baseUri +  "/playlists.xml?" +  query).getPlaylists();
			} catch (Exception e) {
				throw new RuntimeException("Problem requesting query: " + query, e);
			} 
		}
    });

	
	private ConcurrentMap<String, Maybe<Description>> anyQueryCache = cacheTemplate.makeComputingMap(new Function<String, Maybe<Description>>() {

		@Override
		public Maybe<Description> apply(String uri) {
			try {
				UriplayQueryResult result = retrieveData(baseUri + "/any.xml?uri=" +  UrlEncoding.encode(uri));
				Set<Description> all = Sets.newHashSet();
				all.addAll(result.getItems());
				all.addAll(result.getPlaylists());
				return Maybe.fromPossibleNullValue(Iterables.getOnlyElement(all, null));
			} catch (HttpStatusCodeException e) {
				if (HttpServletResponse.SC_NOT_FOUND == e.getStatusCode()) {
					return Maybe.nothing();
				}
				throw new RuntimeException("Problem requesting query: " + uri, e);
			} catch (Exception e) {
				throw new RuntimeException("Problem requesting query: " + uri, e);
			} 
		}
	});

	

	public JaxbUriplayClient(String baseUri) throws JAXBException {
		this.baseUri = baseUri;
		context = JAXBContext.newInstance(UriplayQueryResult.class);
	}
	

	private UriplayQueryResult retrieveData(String queryUri) throws Exception {
		Reader document = new StringReader(httpClient.get(queryUri));

		Unmarshaller unmarshaller = context.createUnmarshaller();
		
		return (UriplayQueryResult) unmarshaller.unmarshal(document);
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
	public Maybe<Description> anyQuery(String uri) {
		return anyQueryCache.get(uri);
	}

}
