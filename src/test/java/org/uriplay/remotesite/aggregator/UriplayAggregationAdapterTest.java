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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import org.jherd.beans.BeanGraphExtractor;
import org.jherd.remotesite.FetchException;
import org.jherd.remotesite.http.RemoteSiteClient;
import org.jherd.remotesite.timing.RequestTimer;
import org.jmock.Expectations;
import org.jmock.integration.junit3.MockObjectTestCase;


/**
 * Unit test for {@link UriplayAggregationAdapter}.
 * @author Robert Chatley (robert@metabroadcast.com)
 */
public class UriplayAggregationAdapterTest extends MockObjectTestCase {

	static final Reader DOCUMENT = new InputStreamReader(new ByteArrayInputStream("doc".getBytes()));
	
	RemoteSiteClient<Reader> httpClient;
	BeanGraphExtractor<Reader> transformer;
	UriplayAggregationAdapter adapter;
	RequestTimer timer = mock(RequestTimer.class);

	@SuppressWarnings("unchecked")
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		httpClient = mock(RemoteSiteClient.class);
		transformer = mock(BeanGraphExtractor.class);
		adapter = new UriplayAggregationAdapter(httpClient, transformer, "http://test.uriplay.org", "[a-z]+://[^/]*bbc.co.uk.*");
	
		checking(new Expectations() {{ 
			ignoring(timer);
		}});
	}
	
	public void testPerformsGetCorrespondingGivenUriAndPassesResultToTransformer() throws Exception {
		
		checking(new Expectations() {{
			one(httpClient).get("http://test.uriplay.org/doc.rdf.xml?uri=http%3A%2F%2Fexample.com"); will(returnValue(DOCUMENT));
			one(transformer).extractFrom(DOCUMENT);
		}});
		
		adapter.fetch("http://example.com", timer);
	}
	
	public void testThrowsFetchExceptionIfHttpClientThrowsException() throws Exception {
		
		checking(new Expectations() {{
			allowing(httpClient).get(with(startsWith("http://test.uriplay.org/doc.rdf.xml?"))); will(throwException(new IOException()));
		}});
		
		try {
			adapter.fetchInternal("http://example.com", timer);
			fail("Expected exception");
		} catch (FetchException fe) {
			assertThat(fe.getMessage(), is("Problem fetching http://example.com"));
		}
	}
	
	public void testCanSpecifyParticularProfile() throws Exception {
		
		adapter.setProfile(Profile.EMBED);
		
		checking(new Expectations() {{
			ignoring(transformer);
			one(httpClient).get("http://test.uriplay.org/doc.rdf.xml?profile=embed&uri=http%3A%2F%2Fexample.com"); will(returnValue(DOCUMENT));
		}});
		
		adapter.fetch("http://example.com", timer);
	}
	
	public void testCanFetchResourcesFromBbc() throws Exception {
		assertFalse(adapter.canFetch("http://www.example.com"));
		assertTrue(adapter.canFetch("http://www.bbc.co.uk"));
	}
}
