/* Copyright 2010 Meta Broadcast Ltd

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

import static org.uriplay.content.criteria.ContentQueryBuilder.query;

import org.jmock.integration.junit3.MockObjectTestCase;
import org.joda.time.DateTime;
import org.uriplay.content.criteria.ContentQueryBuilder;
import org.uriplay.content.criteria.attribute.Attributes;

import com.metabroadcast.common.query.Selection;

public class QueryStringBuilderTest extends MockObjectTestCase {

	public void testTheBuilder() throws Exception {
		
		check(query().equalTo(Attributes.ITEM_TITLE, "foo"), "item.title=foo");
		check(query().equalTo(Attributes.ITEM_TITLE, "foo&foo"), "item.title=foo%26foo");
		check(query().equalTo(Attributes.ITEM_URI, "http://example.com?item=test"), "item.uri=http%3A%2F%2Fexample.com%3Fitem%3Dtest");

		check(query().equalTo(Attributes.LOCATION_AVAILABLE, true), "location.available=true");
		
		check(query().after(Attributes.BROADCAST_TRANSMISSION_TIME, new DateTime().withMillis(101)), "broadcast.transmissionTime-after=101");

		check(query().equalTo(Attributes.ITEM_TITLE, "foo").equalTo(Attributes.LOCATION_AVAILABLE, true), "item.title=foo&location.available=true");
		
		check(query().equalTo(Attributes.ITEM_GENRE, "a", "b"), "item.genre=a,b");
	}
	
	public void testQueryLimits() throws Exception {
		check(query().equalTo(Attributes.ITEM_GENRE, "funny").withSelection(new Selection(0, 10)), "item.genre=funny&limit=10");

	}

	private void check(ContentQueryBuilder query, String expected) {
		QueryStringBuilder builder = new QueryStringBuilder();
		assertEquals(expected, builder.build(query.build()));
	}
	

}
