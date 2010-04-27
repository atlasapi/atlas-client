/* Copyright 2009 British Broadcasting Corporation
   Copyright 2009 Meta Broadcast Ltd

Licensed under the Apache License, Version 2.0 (the "License"); you
may not use this file except in compliance with the License. You may
obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
implied. See the License for the specific language governing
permissions and limitations under the License. */

package org.uriplay.remotesite.aggregator;

import java.io.Reader;

import org.jherd.beans.BeanGraphExtractor;
import org.jherd.beans.Representation;
import org.jherd.remotesite.FetchException;
import org.jherd.remotesite.SiteSpecificRepresentationAdapter;
import org.jherd.remotesite.http.CommonsHttpClient;
import org.jherd.remotesite.http.RemoteSiteClient;
import org.jherd.remotesite.timing.RequestTimer;
import org.jherd.remotesite.timing.TimedFetcher;
import org.uriplay.client.UriplayRequestBuilder;

/**
 * Service that queries a remote URIplay server.
 * Can be configured with a remote address, and an api key.
 * 
 * @author Robert Chatley (robert@metabroadcast.com)
 */
public class UriplayAggregationAdapter extends TimedFetcher<Representation> implements SiteSpecificRepresentationAdapter {

	private final RemoteSiteClient<Reader> httpClient;
	
	private String baseUri;
	
	private String delegatePattern;

	private Profile profile;

	private final BeanGraphExtractor<Reader> graphExtractor;

	public UriplayAggregationAdapter(BeanGraphExtractor<Reader> graphExtractor, String baseUri, String delegatePattern) {
		this(new CommonsHttpClient().withAcceptHeader("application/rdf+xml"), graphExtractor, baseUri, delegatePattern);
	}
	
	public UriplayAggregationAdapter(RemoteSiteClient<Reader> httpClient, BeanGraphExtractor<Reader> graphExtractor, String baseUri, String delegatePattern) {
		this.httpClient = httpClient;
		this.graphExtractor = graphExtractor;
		this.baseUri = baseUri;
		this.delegatePattern = delegatePattern;
	}

	public Representation fetchInternal(String uri, RequestTimer timer) {
		Reader document = null;
		
		String queryUri = queryUriFor(uri);
		try {
			timer.nest();
			timer.start(this, "Contacting remote URIplay server: " + queryUri);
			document = httpClient.get(queryUri);
		} catch (Exception e) {
			throw new FetchException("Problem fetching " + uri, e);
		} finally {
			timer.stop(this, "Contacting remote URIplay server: " + queryUri);
			timer.unnest();
		}

		return graphExtractor.extractFrom(document);
	}
	
	private String queryUriFor(String uri) {
		return new UriplayRequestBuilder(baseUri, "rdf.xml")
			.withUri(uri)
			.withProfile(profile)
			.build();
	}

	public void setBaseUri(String baseUri) {
		this.baseUri = baseUri;
	}

	public boolean canFetch(String uri) {
		return uri.matches(delegatePattern); 
	}

	public void setProfile(Profile profile) {
		this.profile = profile;
	}
}
