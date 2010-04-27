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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.jherd.util.Selection;
import org.uriplay.remotesite.aggregator.Profile;


public class UriplayRequestBuilder {

	private final String baseUri;
	private final String format;

	private String uri;
	private String itemUri;
	private Profile profile;
	private String itemCurie;
	private String listCurie;
	private String brandUri;
	private String genreUri;
	private Boolean available;
	private Boolean islongForm;
	private Selection selection;
	private String brandTitleSearch;

	public UriplayRequestBuilder(String baseUri, String format) {
		this.baseUri = baseUri;
		this.format = format;
	}
	
	public String build() {
		StringBuilder query = new StringBuilder();
		query.append(baseUri);
		if (uri != null) {
			query.append("/doc.");
		} else if (itemCurie != null || itemUri != null || genreUri != null) {
			query.append("/items.");
		} else if (listCurie != null || brandUri != null || brandTitleSearch != null) {
			query.append("/brands.");
		}
		query.append(format);
		query.append("?");
		if (profile != null) {
			query.append("profile=");
			query.append(profile.toString().toLowerCase());
			query.append("&");
		}
		if (uri != null) {
			query.append("uri=");
			encode(uri, query);
		}
		if (itemUri != null) {
			query.append("item.uri=");
			encode(itemUri, query);
		}
		if (itemCurie != null) {
			query.append("item.curie=");
			query.append(itemCurie);
		}
		if (listCurie != null) {
			query.append("brand.curie=");
			query.append(listCurie);
		}
		
		if (brandUri != null) {
			query.append("brand.uri=");
			encode(brandUri, query);
		}
		
		if (brandTitleSearch != null) {
			query.append("brand.title-search=");
			encode(brandTitleSearch, query);
		}
		
		if (genreUri != null) {
			query.append("genre=");
			query.append(genreUri);
		}
		if (available != null) {
			query.append("&");
			query.append("available=");
			query.append(available);
		}
		if (islongForm != null) {
			query.append("&");
			query.append("available=");
			query.append(available);
		}
		if (selection != null) {
			if (selection.getStartIndex() != null) {
				query.append("&");
				query.append("startIndex=");
				query.append(selection.getStartIndex());
			}
			if (selection.getLimit() != null) {
				query.append("&");
				query.append("limit=");
				query.append(selection.getLimit());
			}
		}
		return query.toString();
	}

	private void encode(String uri, StringBuilder query) {
		try {
			query.append(URLEncoder.encode(uri, "utf-8"));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
	
	public UriplayRequestBuilder withUri(String uri) {
		this.uri = uri;
		return this;
	}
	
	public UriplayRequestBuilder withProfile(Profile profile) {
		this.profile = profile;
		return this;
	}

	public UriplayRequestBuilder withItemCurie(String curie) {
		this.itemCurie = curie;
		return this;
	}
	
	public UriplayRequestBuilder withItemUri(String uri) {
		this.itemUri = uri;
		return this;
	}
	
	public UriplayRequestBuilder withListCurie(String curie) {
		this.listCurie = curie;
		return this;
	}

	public UriplayRequestBuilder withGenre(String genreUri) {
		this.genreUri = genreUri;
		return this;
	}

	public UriplayRequestBuilder withAvailable(boolean available) {
		this.available = available;
		return this;
	}

	public UriplayRequestBuilder withSelection(Selection selection) {
		this.selection = selection;
		return this;
	}

	public UriplayRequestBuilder withIsLongForm(boolean islongForm) {
		this.islongForm = islongForm;
		return this;
	}

	public UriplayRequestBuilder withBrandUri(String uri) {
		this.brandUri = uri;
		return this;
	}

	public UriplayRequestBuilder withBrandTitleSearch(String text) {
		this.brandTitleSearch = text;
		return this;
	}
}
