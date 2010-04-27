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
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.jherd.beans.BeanGraphErrors;
import org.jherd.beans.BeanGraphExtractor;
import org.jherd.beans.BeanGraphFactory;
import org.jherd.beans.PropertyMergeException;
import org.jherd.beans.Representation;
import org.jherd.beans.UriPropertySource;
import org.jherd.naming.ResourceMapping;
import org.jherd.rdf.beans.RdfXmlTranslator;
import org.jherd.remotesite.FetchException;
import org.jherd.remotesite.http.CommonsHttpClient;
import org.jherd.remotesite.http.RemoteSiteClient;
import org.jherd.util.Selection;
import org.joda.time.DateTimeConstants;
import org.joda.time.Duration;
import org.springframework.beans.MutablePropertyValues;
import org.uriplay.beans.InMemoryResourceMapping;
import org.uriplay.beans.NaiveTypeMap;
import org.uriplay.media.entity.Brand;
import org.uriplay.media.entity.Description;
import org.uriplay.media.entity.Item;
import org.uriplay.media.entity.Playlist;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class RdfUriplayClient implements Uriplay {
		
	private static final String URIPLAY_OUTPUT_FORMAT = "rdf.xml";

	private final RemoteSiteClient<Reader> httpClient = new CommonsHttpClient().withConnectionTimeout(new Duration(30 * DateTimeConstants.MILLIS_PER_SECOND));
	
	private final String baseUri;
	
	private final BeanGraphFactory beanGraphFactory = new BeanGraphFactory(null, new UriPropertySource() {

		public void merge(Representation representation, String docId) throws PropertyMergeException {
			MutablePropertyValues mpvs = new MutablePropertyValues();
			mpvs.addPropertyValue("canonicalUri", docId);
			representation.addValues(docId, mpvs);
		}
	});
	
	private final BeanGraphExtractor<Reader> graphExtractor;
	private InMemoryResourceMapping cache;

	public RdfUriplayClient(String baseUri, List<Class<?>> resourceTypes, InMemoryResourceMapping cache) {
		this.baseUri = baseUri;
		this.graphExtractor = createRdfExtractor(new CanonicalUriExtractingResourceMapping());
		this.beanGraphFactory.setBeanTypes(resourceTypes);
		this.cache = cache;
	}
	
	public RdfUriplayClient(String baseUri, List<Class<?>> resourceTypes) {
		this(baseUri, resourceTypes, null);
	}
	
	public RdfUriplayClient(String baseUri, List<Class<?>> resourceTypes, int cacheTimoutInMins) {
		this(baseUri, resourceTypes, new InMemoryResourceMapping(cacheTimoutInMins));
	}
	
	private BeanGraphExtractor<Reader> createRdfExtractor(ResourceMapping resourceMapping) {
		RdfXmlTranslator rdfXmlTranslator = new RdfXmlTranslator(new NaiveTypeMap(), resourceMapping);
		rdfXmlTranslator.setNsPrefixes(rdfNsPrefixes());
		return rdfXmlTranslator;
	}

	@SuppressWarnings("unchecked")
	public Set<Description> query(String uri) {
		Description cached = fromCache(uri);
		if (cached != null) {
			return Sets.newHashSet(cached);
		}
		String queryUri = queryUriFor(uri);
		try {
			Set beanGraph = retrieveAndCacheData(queryUri);
			return beanGraph;
		} catch (Exception e) {
			throw new FetchException("Problem fetching " + uri + " from " + queryUri, e);
		} 
	}

	@Override
	public Item itemWithCurie(String curie) {
		Description cached = fromCache(curie);
		if (cached != null) {
			return (Item) cached;
		}
		String queryUri = queryUriForItemCurie(curie);
		try {
			Set beanGraph = retrieveAndCacheData(queryUri);
			return (Item) descriptionWith(curie, beanGraph);
		} catch (Exception e) {
			throw new FetchException("Problem fetching " + curie + " from " + queryUri, e);
		} 
	}

	@Override
	public Brand brandWithCurie(String curie) {
		Description cached = fromCache(curie);
		if (cached != null) {
			return (Brand) cached;
		}
		String queryUri = queryUriForBrandCurie(curie);
		try {
			Set beanGraph = retrieveAndCacheData(queryUri);
			return (Brand) descriptionWith(curie, beanGraph);
		} catch (Exception e) {
			throw new FetchException("Problem fetching " + curie + " from " + queryUri, e);
		} 
	}

	@Override
	public Playlist genreQuery(String genre, Selection selection) {
		String queryUri = queryUriForGenre(genre, selection);
		try {
			Set beanGraph = retrieveAndCacheData(queryUri);
			Playlist genreList = new Playlist();
			genreList.getItems().addAll(itemsFrom(beanGraph));
			genreList.setTitle(GenreNamesMap.genres.get(genre));
			return genreList;
		} catch (Exception e) {
			throw new FetchException("Problem fetching " + genre + " from " + queryUri, e);
		} 
	}

	static class GenreNamesMap {

		private static final Map<String, String> genres = Maps.newHashMap();

	    static {
	    	genres.put("http://uriplay.org/genres/uriplay/childrens", "Kids");
	    	genres.put("http://uriplay.org/genres/uriplay/comedy", "Comedy");
	    	genres.put("http://uriplay.org/genres/uriplay/drama", "Drama");
	    	genres.put("http://uriplay.org/genres/uriplay/entertainment", "Entertainment");
	    	genres.put("http://uriplay.org/genres/uriplay/factual", "Factual");
	    	genres.put("http://uriplay.org/genres/uriplay/film", "Film and Animation");
	    	genres.put("http://uriplay.org/genres/uriplay/learning", "Learning");
	    	genres.put("http://uriplay.org/genres/uriplay/lifestyle", "Lifestyle");
	    	genres.put("http://uriplay.org/genres/uriplay/music", "Music");
	    	genres.put("http://uriplay.org/genres/uriplay/news", "News and Current Affairs");
	    	genres.put("http://uriplay.org/genres/uriplay/animals", "Pets and Animals");
	    	genres.put("http://uriplay.org/genres/uriplay/sports", "Sport");
	    }
	}
	
	private Set retrieveAndCacheData(String queryUri) throws Exception {
		Reader document = httpClient.get(queryUri);
		Set beanGraph = (Set) beanGraphFactory.createGraph(graphExtractor.extractFrom(document), new BeanGraphErrors());
		cache(beanGraph);
		return beanGraph;
	}
	
	private Description descriptionWith(String curie, Set beanGraph) {

		if (curie == null || beanGraph == null) {
			return null;
		}

		for (Object bean : beanGraph) {
			if (bean instanceof Description) {
				Description description = (Description) bean;
				if (curie.equals(description.getCurie())) {
					return description;
				}
			}
		}
		return null;
	}
	
	private Set<Item> itemsFrom(Set<Object> beanGraph) {

		if (beanGraph == null) {
			return null;
		}

		Set<Item> items = Sets.newHashSet();
		
		for (Object bean : beanGraph) {
			if (bean instanceof Item) {
				items.add((Item) bean);
			}
		}
		return items;
	}

	private void cache(Set beanGraph) {
		if (cache != null) {
			cache.store(beanGraph);
		}
	}

	private Description fromCache(String uri) {
		if (cache == null) {
			return null;
		}
		return (Description) cache.getResource(uri);
	}
	
	private String queryUriFor(String uri) {
		return new UriplayRequestBuilder(baseUri, URIPLAY_OUTPUT_FORMAT)
		.withUri(uri)
		.build();
	}
	
	private String queryUriForItemCurie(String curie) {
		return new UriplayRequestBuilder(baseUri, URIPLAY_OUTPUT_FORMAT)
		.withAvailable(true)
		.withItemCurie(curie)
		.build();
	}
	
	private String queryUriForBrandCurie(String curie) {
		return new UriplayRequestBuilder(baseUri, URIPLAY_OUTPUT_FORMAT)
		.withAvailable(true)
		.withListCurie(curie)
		.build();
	}
	
	private String queryUriForGenre(String genreUri, Selection selection) {
		return new UriplayRequestBuilder(baseUri, URIPLAY_OUTPUT_FORMAT)
		.withAvailable(true)
		.withIsLongForm(true)
		.withGenre(genreUri)
		.withSelection(selection)
		.build();
	}
	
	private static class CanonicalUriExtractingResourceMapping implements ResourceMapping {

		public boolean canMatch(String uri) {
			return true;
		}

		public Object getResource(String uri) {
			return null;
		}

		public String getUri(Object bean) {
			if (bean instanceof Description) {
				return ((Description) bean).getCanonicalUri();
			}
			return null;
		}

		public Set<String> getUris(Object bean) {
			String uri = getUri(bean);
			if (uri == null) {
				return Collections.emptySet();
			} else {
				return Collections.singleton(uri);
			}
		}

		public boolean isReserved(String uri) {
			return false;
		}
	}
	
	private static Properties rdfNsPrefixes() {
		Properties nsPrefixes= new Properties();
		nsPrefixes.setProperty("http://uriplay.org/elements/", "play");
		nsPrefixes.setProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "rdf");
		nsPrefixes.setProperty("http://www.w3.org/2000/01/rdf-schema#", "rdfs");
		nsPrefixes.setProperty("http://purl.org/dc/elements/1.1/", "dc");
		nsPrefixes.setProperty("http://purl.org/ontology/po/", "po");
		nsPrefixes.setProperty("http://xmlns.com/foaf/0.1/", "foaf");
		nsPrefixes.setProperty("http://rdfs.org/sioc/ns#", "sioc");
		nsPrefixes.setProperty("http://purl.org/dc/terms/", "dcterms");
		return nsPrefixes;
	}

}
