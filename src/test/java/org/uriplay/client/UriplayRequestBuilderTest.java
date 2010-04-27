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

import java.net.URLEncoder;

import junit.framework.TestCase;

/**
 * @author Robert Chatley (robert@metabroadcast.com)
 */
public class UriplayRequestBuilderTest extends TestCase {
	
	public void testCanBuildUriForUriQuery() throws Exception {
		String uri = new UriplayRequestBuilder("http://uriplay.org/api/2.0", "rdf.xml").withUri("http://example.com").build();
		assertEquals("http://uriplay.org/api/2.0/doc.rdf.xml?uri=" + urlEncode("http://example.com"), uri);
	}
	
	public void testCanBuildUriForItemCurieQuery() throws Exception {
		String uri = new UriplayRequestBuilder("http://uriplay.org/api/2.0", "rdf.xml").withItemCurie("bbc:b00abc").build();
		assertEquals("http://uriplay.org/api/2.0/items.rdf.xml?item.curie=bbc:b00abc", uri);
	}

	@SuppressWarnings("deprecation")
	private String urlEncode(String url) {
		return URLEncoder.encode(url);
	}
}
