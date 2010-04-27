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
import java.util.Collections;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.jherd.remotesite.FetchException;
import org.jherd.remotesite.http.CommonsHttpClient;
import org.jherd.remotesite.http.RemoteSiteClient;
import org.jherd.util.caching.FixedExpiryCache;
import org.joda.time.DateTimeConstants;
import org.joda.time.Duration;
import org.uriplay.content.criteria.ContentQuery;
import org.uriplay.media.entity.simple.Description;
import org.uriplay.media.entity.simple.Item;
import org.uriplay.media.entity.simple.Playlist;
import org.uriplay.media.entity.simple.UriplayXmlOutput;

import com.google.soy.common.collect.Lists;

/**
 * @author Robert Chatley (robert@metabroadcast.com)
 */
public class JaxbUriplayClient implements SimpleUriplayClient {
		
	private final RemoteSiteClient<Reader> httpClient = new CommonsHttpClient().withConnectionTimeout(new Duration(30 * DateTimeConstants.MILLIS_PER_SECOND));
	
	private final String baseUri;
	
	private JAXBContext context;

	private QueryStringBuilder queryStringBuilder = new QueryStringBuilder();
	
	
	private FixedExpiryCache<String, List<Item>> itemQueryCache = new FixedExpiryCache<String, List<Item>>(10) {

		@Override
		protected List<Item> cacheMissFor(String query) {
			try {
				return extractContentOfType(retrieveData(baseUri + "/items.xml?" +  query), Item.class);
			} catch (Exception e) {
				throw new FetchException("Problem requesting query: " + query, e);
			} 
		}
	};
	
	private FixedExpiryCache<String, List<Playlist>> playlistQueryCache = new FixedExpiryCache<String, List<Playlist>>(10) {

		@Override
		protected List<Playlist> cacheMissFor(String query) {
			try {
				return extractContentOfType(retrieveData(baseUri +  "/playlists.xml?" +  query), Playlist.class);
			} catch (Exception e) {
				throw new FetchException("Problem requesting query: " + query, e);
			} 
		}
	};
	
	private FixedExpiryCache<String, List<Playlist>> brandQueryCache = new FixedExpiryCache<String, List<Playlist>>(10) {

		@Override
		protected List<Playlist> cacheMissFor(String query) {
			try {
				return extractContentOfType(retrieveData(baseUri +  "/brands.xml?" +  query), Playlist.class);
			} catch (Exception e) {
				throw new FetchException("Problem requesting query: " + query, e);
			} 
		}
	};

	public JaxbUriplayClient(String baseUri) throws JAXBException {
		this.baseUri = baseUri;
		context = JAXBContext.newInstance(UriplayXmlOutput.class);
	}
	

	private List<Description> retrieveData(String queryUri) throws Exception {
		Reader document = httpClient.get(queryUri);

		Unmarshaller unmarshaller = context.createUnmarshaller();
		
		UriplayXmlOutput wrapper = (UriplayXmlOutput) unmarshaller.unmarshal(document);
		
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
}
